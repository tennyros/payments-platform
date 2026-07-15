package com.payments.account.model

import com.payments.common.account.AccountDto
import com.payments.common.account.CreateAccountRequest
import com.payments.common.id.UuidV7Generator
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("account")
data class AccountRecord(
    @Id
    @Column("id")
    val accountId: UUID,
    @Column("owner_name")
    val ownerName: String,
    @Column("currency")
    val currency: String,
    @Column("balance")
    val balance: BigDecimal,
    @Column("active")
    val active: Boolean,
    @Column("created_at")
    val createdAt: Instant,
) : Persistable<UUID> {
    @Transient
    private val newRecord: Boolean = true

    override fun getId(): UUID = accountId

    override fun isNew(): Boolean = newRecord
}

fun AccountRecord.toDto(): AccountDto =
    AccountDto(
        id = accountId,
        ownerName = ownerName,
        currency = currency,
        balance = balance,
        active = active,
    )

fun CreateAccountRequest.toRecord(
    id: UUID = UuidV7Generator.generate(),
    createdAt: Instant = Instant.now(),
): AccountRecord =
    AccountRecord(
        accountId = id,
        ownerName = ownerName,
        currency = currency.uppercase(),
        balance = initialBalance,
        active = true,
        createdAt = createdAt,
    )
