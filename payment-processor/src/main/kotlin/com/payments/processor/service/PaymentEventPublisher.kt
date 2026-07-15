package com.payments.processor.service

import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import reactor.core.publisher.Mono
import java.time.Instant

interface PaymentEventPublisher {
    fun publishPaymentCreated(payment: PaymentDto): Mono<Void>

    fun publishPaymentStatusChanged(
        payment: PaymentDto,
        previousStatus: PaymentStatus,
        changedAt: Instant,
    ): Mono<Void>
}
