package com.payments.account.api

import com.payments.account.service.AccountService
import com.payments.common.account.AccountDto
import com.payments.common.account.CreateAccountRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.UUID

class AccountControllerTest {
    @Test
    fun `createAccount returns created account`() {
        val accountService: AccountService = mock()
        val controller = AccountController(accountService)
        val request =
            CreateAccountRequest(
                ownerName = "Alice Carter",
                currency = "usd",
                initialBalance = BigDecimal("125.50"),
            )
        val responseAccount =
            AccountDto(
                id = UUID.fromString("11111111-1111-7111-8111-111111111111"),
                ownerName = "Alice Carter",
                currency = "USD",
                balance = BigDecimal("125.50"),
                active = true,
            )

        whenever(accountService.createAccount(any())).thenReturn(Mono.just(responseAccount))

        val response = controller.createAccount(request).block()!!

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(responseAccount, response.body)
        assertEquals(7, response.body!!.id.version())
        assertTrue(
            response.body!!
                .id
                .toString()
                .isNotBlank(),
        )
    }

    @Test
    fun `listAccounts returns empty flux by default`() {
        val accountService: AccountService = mock()
        val controller = AccountController(accountService)

        whenever(accountService.listAccounts()).thenReturn(Flux.empty())

        val accounts = controller.listAccounts().collectList().block()!!

        assertEquals(0, accounts.size)
    }
}
