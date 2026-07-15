package com.payments.processor.service

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.processor.model.toDto
import com.payments.processor.model.toRecord
import com.payments.processor.repository.PaymentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentEventPublisher: PaymentEventPublisher,
) {
    fun listPayments(): Flux<PaymentDto> = paymentRepository.findAll().map { it.toDto() }

    fun getPayment(id: UUID): Mono<PaymentDto> =
        paymentRepository
            .findById(id)
            .map { it.toDto() }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")))

    fun createPayment(request: CreatePaymentRequest): Mono<PaymentDto> =
        paymentRepository
            .save(request.toRecord())
            .map { it.toDto() }
            .flatMap { payment ->
                paymentEventPublisher.publishPaymentCreated(payment).thenReturn(payment)
            }
}
