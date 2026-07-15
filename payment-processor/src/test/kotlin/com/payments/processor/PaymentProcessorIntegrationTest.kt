package com.payments.processor

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentCreatedEvent
import com.payments.common.payment.PaymentStatus
import com.payments.common.payment.PaymentStatusChangedEvent
import com.payments.common.payment.UpdatePaymentStatusRequest
import com.payments.processor.repository.PaymentRepository
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import tools.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.time.Duration
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["payments.payment-created", "payments.payment-status-changed"])
@Import(TestKafkaConfiguration::class)
class PaymentProcessorIntegrationTest {
    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun cleanDatabase() {
        webTestClient =
            WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
        prepareSchema()
        paymentRepository.deleteAll().block()
    }

    private fun prepareSchema() {
        val dataSource =
            DriverManagerDataSource().apply {
                setDriverClassName("org.h2.Driver")
                setUrl("jdbc:h2:mem:payments;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                username = "sa"
                password = ""
            }

        ResourceDatabasePopulator(
            ClassPathResource("db/test/schema/001-create-payment-queue.sql"),
            ClassPathResource("db/test/data/001-seed-payment-statuses.sql"),
        ).execute(dataSource)
    }

    @Test
    fun `create payment persists row and publishes kafka event`() {
        val request =
            CreatePaymentRequest(
                accountId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                amount = BigDecimal("42.50"),
                currency = "usd",
            )

        webTestClient
            .post()
            .uri("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody()
            .jsonPath("$.accountId")
            .isEqualTo(request.accountId.toString())
            .jsonPath("$.amount")
            .isEqualTo(42.50)
            .jsonPath("$.currency")
            .isEqualTo("USD")
            .jsonPath("$.status")
            .isEqualTo(PaymentStatus.PENDING.name)
            .jsonPath("$.id")
            .isNotEmpty

        val payments = paymentRepository.findAll().collectList().block()!!
        assertEquals(1, payments.size)
        val storedPayment = payments.first()
        assertEquals(7, storedPayment.id.version())
        assertEquals(request.accountId, storedPayment.accountId)
        assertEquals(request.amount, storedPayment.amount)
        assertEquals("USD", storedPayment.currency)
        assertEquals(PaymentStatus.PENDING.name, storedPayment.status)

        createConsumer().use { consumer ->
            consumer.subscribe(listOf(paymentCreatedTopic(), paymentStatusChangedTopic()))
            val records = consumer.poll(Duration.ofSeconds(10))
            val record: ConsumerRecord<String, String> = records.records(paymentCreatedTopic()).first()
            val event = objectMapper.readValue(record.value(), PaymentCreatedEvent::class.java)

            assertNotNull(event)
            assertEquals(storedPayment.id, event.paymentId)
            assertEquals(7, event.paymentId.version())
            assertEquals(storedPayment.accountId, event.accountId)
            assertEquals(storedPayment.amount, event.amount)
            assertEquals(storedPayment.currency, event.currency)
            assertEquals(PaymentStatus.PENDING, event.status)
            assertEquals(storedPayment.createdAt, event.createdAt)

            webTestClient
                .patch()
                .uri("/payments/${storedPayment.id}/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdatePaymentStatusRequest(status = PaymentStatus.PROCESSED))
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(storedPayment.id.toString())
                .jsonPath("$.status")
                .isEqualTo(PaymentStatus.PROCESSED.name)

            val updatedPayment = paymentRepository.findById(storedPayment.id).block()!!
            assertEquals(PaymentStatus.PROCESSED.name, updatedPayment.status)
            assertTrue(updatedPayment.updatedAt >= storedPayment.createdAt)

            val statusRecords = consumer.poll(Duration.ofSeconds(10))
            val statusRecord: ConsumerRecord<String, String> = statusRecords.records(paymentStatusChangedTopic()).first()
            val statusEvent = objectMapper.readValue(statusRecord.value(), PaymentStatusChangedEvent::class.java)

            assertNotNull(statusEvent)
            assertEquals(storedPayment.id, statusEvent.paymentId)
            assertEquals(storedPayment.accountId, statusEvent.accountId)
            assertEquals(storedPayment.amount, statusEvent.amount)
            assertEquals(storedPayment.currency, statusEvent.currency)
            assertEquals(PaymentStatus.PENDING, statusEvent.previousStatus)
            assertEquals(PaymentStatus.PROCESSED, statusEvent.newStatus)
            assertEquals(updatedPayment.updatedAt, statusEvent.changedAt)
        }
    }

    private fun createConsumer() =
        DefaultKafkaConsumerFactory<String, String>(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to embeddedKafkaBroker.brokersAsString,
                ConsumerConfig.GROUP_ID_CONFIG to "payments-platform-it-${UUID.randomUUID()}",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "true",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ),
            StringDeserializer(),
            StringDeserializer(),
        ).createConsumer()

    private fun paymentCreatedTopic(): String = environment.getRequiredProperty("payments.topics.payment-created")

    private fun paymentStatusChangedTopic(): String = environment.getRequiredProperty("payments.topics.payment-status-changed")
}
