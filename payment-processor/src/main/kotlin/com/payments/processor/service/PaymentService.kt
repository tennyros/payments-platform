package com.payments.processor.service

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import com.payments.common.payment.UpdatePaymentStatusRequest
import com.payments.processor.model.toDto
import com.payments.processor.model.toRecord
import com.payments.processor.repository.PaymentRepository
import com.payments.processor.repository.PaymentStatusRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentStatusRepository: PaymentStatusRepository,
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

    fun updatePaymentStatus(
        id: UUID,
        request: UpdatePaymentStatusRequest,
    ): Mono<PaymentDto> {
        val targetStatus =
            request.status
                ?: return Mono.error(
                    ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status is required"),
                )

        if (targetStatus == PaymentStatus.PENDING) {
            return Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status cannot be reset to pending"))
        }

        return paymentRepository
            .findById(id)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found")))
            .flatMap { current ->
                if (current.status != PaymentStatus.PENDING.name) {
                    return@flatMap Mono.error<PaymentDto>(
                        ResponseStatusException(HttpStatus.CONFLICT, "Payment is already finalized"),
                    )
                }

                val updatedAt = Instant.now()
                paymentStatusRepository
                    .updateStatus(id, targetStatus.name, updatedAt)
                    .flatMap { paymentRepository.findById(id) }
                    .map { it.toDto() }
                    .flatMap { updated ->
                        paymentEventPublisher
                            .publishPaymentStatusChanged(
                                updated,
                                PaymentStatus.valueOf(current.status),
                                updatedAt,
                            ).thenReturn(updated)
                    }
            }
    }
}
