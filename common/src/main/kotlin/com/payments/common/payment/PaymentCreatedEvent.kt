package com.payments.common.payment

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentCreatedEvent(
    val paymentId: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val createdAt: Instant,
)
