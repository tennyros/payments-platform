package com.payments.processor.service

import com.payments.common.id.UuidV7Generator
import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentStatus
import com.payments.processor.model.PaymentRecord
import com.payments.processor.model.toRecord
import com.payments.processor.repository.PaymentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentServiceTest {
    private val repository: PaymentRepository = mock()
    private val paymentEventPublisher: PaymentEventPublisher = mock()
    private val service = PaymentService(repository, paymentEventPublisher)

    @Test
    fun `listPayments returns stored payments`() {
        val payment =
            PaymentRecord(
                paymentId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                accountId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
                amount = BigDecimal("125.50"),
                currency = "USD",
                status = PaymentStatus.PENDING.name,
                createdAt = Instant.parse("2026-07-15T10:15:30Z"),
            )

        whenever(repository.findAll()).thenReturn(
            reactor.core.publisher.Flux
                .just(payment),
        )

        StepVerifier
            .create(service.listPayments())
            .assertNext { dto ->
                assertEquals(payment.paymentId, dto.id)
                assertEquals(payment.accountId, dto.accountId)
                assertEquals(payment.amount, dto.amount)
                assertEquals(payment.currency, dto.currency)
                assertEquals(PaymentStatus.PENDING, dto.status)
                assertEquals(payment.createdAt, dto.createdAt)
            }.verifyComplete()
    }

    @Test
    fun `getPayment returns not found for unknown id`() {
        val id = UuidV7Generator.generate()
        whenever(repository.findById(id)).thenReturn(Mono.empty())

        StepVerifier
            .create(service.getPayment(id))
            .expectErrorMatches { error -> error.message?.contains("Payment not found") == true }
            .verify()
    }

    @Test
    fun `createPayment persists pending payment`() {
        val request =
            CreatePaymentRequest(
                accountId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
                amount = BigDecimal("19.99"),
                currency = "usd",
            )
        val record =
            request.toRecord(
                id = UUID.fromString("44444444-4444-4444-4444-444444444444"),
                createdAt = Instant.parse("2026-07-15T10:15:30Z"),
            )

        whenever(repository.save(any())).thenAnswer { invocation ->
            Mono.just(invocation.getArgument(0))
        }
        whenever(paymentEventPublisher.publishPaymentCreated(any())).thenReturn(Mono.empty())

        val created = service.createPayment(request).block()!!

        assertEquals(record.accountId, created.accountId)
        assertEquals(record.amount, created.amount)
        assertEquals("USD", created.currency)
        assertEquals(PaymentStatus.PENDING, created.status)
        assertEquals(7, created.id.version())
        assertTrue(created.id.toString().isNotBlank())
        verify(paymentEventPublisher).publishPaymentCreated(created)
    }
}
