package com.payments.common.account

import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.UUID

data class AccountDto(
    val id: UUID,
    val ownerName: String,
    val currency: String,
    val balance: BigDecimal,
    val active: Boolean,
)

data class CreateAccountRequest(
    @field:NotBlank
    val ownerName: String,
    @field:NotBlank
    val currency: String,
    val initialBalance: BigDecimal = BigDecimal.ZERO,
)
