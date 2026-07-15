package com.payments.common.payment

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

enum class PaymentStatus {
    PENDING,
    PROCESSED,
    FAILED,
}

data class PaymentDto(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val createdAt: Instant,
)

data class CreatePaymentRequest(
    @field:NotNull
    val accountId: UUID?,
    @field:Positive
    val amount: BigDecimal,
    @field:NotBlank
    val currency: String,
)

data class UpdatePaymentStatusRequest(
    @field:NotNull
    val status: PaymentStatus?,
)
