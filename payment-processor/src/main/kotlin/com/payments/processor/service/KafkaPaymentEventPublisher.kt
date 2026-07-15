package com.payments.processor.service

import com.payments.common.payment.PaymentCreatedEvent
import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import com.payments.common.payment.PaymentStatusChangedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import tools.jackson.databind.ObjectMapper
import java.time.Instant

@Service
class KafkaPaymentEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    @Value("\${payments.topics.payment-created}") private val paymentCreatedTopic: String,
    @Value("\${payments.topics.payment-status-changed}") private val paymentStatusChangedTopic: String,
) : PaymentEventPublisher {
    override fun publishPaymentCreated(payment: PaymentDto): Mono<Void> {
        val event =
            PaymentCreatedEvent(
                paymentId = payment.id,
                accountId = payment.accountId,
                amount = payment.amount,
                currency = payment.currency,
                status = payment.status,
                createdAt = payment.createdAt,
            )

        return Mono
            .fromFuture(
                kafkaTemplate.send(
                    paymentCreatedTopic,
                    payment.id.toString(),
                    objectMapper.writeValueAsString(event),
                ),
            ).then()
    }

    override fun publishPaymentStatusChanged(
        payment: PaymentDto,
        previousStatus: PaymentStatus,
        changedAt: Instant,
    ): Mono<Void> {
        val event =
            PaymentStatusChangedEvent(
                paymentId = payment.id,
                accountId = payment.accountId,
                amount = payment.amount,
                currency = payment.currency,
                previousStatus = previousStatus,
                newStatus = payment.status,
                changedAt = changedAt,
            )

        return Mono
            .fromFuture(
                kafkaTemplate.send(
                    paymentStatusChangedTopic,
                    payment.id.toString(),
                    objectMapper.writeValueAsString(event),
                ),
            ).then()
    }
}
