package com.payments.common.payment

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentStatusChangedEvent(
    val paymentId: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val previousStatus: PaymentStatus,
    val newStatus: PaymentStatus,
    val changedAt: Instant,
)
