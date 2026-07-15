package com.payments.processor.model

import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("payment_queue")
data class PaymentRecord(
    @Id
    val id: UUID,
    @Column("account_id")
    val accountId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: String,
    @Column("created_at")
    val createdAt: Instant,
)

fun PaymentRecord.toDto(): PaymentDto =
    PaymentDto(
        id = id,
        accountId = accountId,
        amount = amount,
        currency = currency,
        status = PaymentStatus.valueOf(status),
        createdAt = createdAt,
    )

fun CreatePaymentRequest.toRecord(
    id: UUID = UUID.randomUUID(),
    createdAt: Instant = Instant.now(),
): PaymentRecord =
    PaymentRecord(
        id = id,
        accountId = accountId!!,
        amount = amount,
        currency = currency.uppercase(),
        status = PaymentStatus.PENDING.name,
        createdAt = createdAt,
    )
