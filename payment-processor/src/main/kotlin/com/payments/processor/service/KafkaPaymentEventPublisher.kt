package com.payments.processor.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.payments.common.payment.PaymentCreatedEvent
import com.payments.common.payment.PaymentDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class KafkaPaymentEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    @Value("\${payments.topics.payment-created}") private val paymentCreatedTopic: String,
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
}
