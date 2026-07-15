package com.payments.processor.model

import com.payments.common.id.UuidV7Generator
import com.payments.common.payment.CreatePaymentRequest
import com.payments.common.payment.PaymentDto
import com.payments.common.payment.PaymentStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("payment_queue")
data class PaymentRecord(
    @Id
    @Column("id")
    val paymentId: UUID,
    @Column("account_id")
    val accountId: UUID,
    @Column("amount")
    val amount: BigDecimal,
    @Column("currency")
    val currency: String,
    @Column("status")
    val status: String,
    @Column("created_at")
    val createdAt: Instant,
) : Persistable<UUID> {
    @Transient
    private val newRecord: Boolean = true

    override fun getId(): UUID = paymentId

    override fun isNew(): Boolean = newRecord
}

fun PaymentRecord.toDto(): PaymentDto =
    PaymentDto(
        id = paymentId,
        accountId = accountId,
        amount = amount,
        currency = currency,
        status = PaymentStatus.valueOf(status),
        createdAt = createdAt,
    )

fun CreatePaymentRequest.toRecord(
    id: UUID = UuidV7Generator.generate(),
    createdAt: Instant = Instant.now(),
): PaymentRecord =
    PaymentRecord(
        paymentId = id,
        accountId = accountId!!,
        amount = amount,
        currency = currency.uppercase(),
        status = PaymentStatus.PENDING.name,
        createdAt = createdAt,
    )
