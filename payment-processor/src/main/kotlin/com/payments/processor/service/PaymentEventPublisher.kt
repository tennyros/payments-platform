package com.payments.processor.service

import com.payments.common.payment.PaymentDto
import reactor.core.publisher.Mono

interface PaymentEventPublisher {
    fun publishPaymentCreated(payment: PaymentDto): Mono<Void>
}
