package com.payments.account.service

import com.payments.account.model.AccountRecord
import com.payments.account.model.toRecord
import com.payments.account.repository.AccountRepository
import com.payments.common.account.CreateAccountRequest
import com.payments.common.id.UuidV7Generator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class AccountServiceTest {
    private val repository: AccountRepository = mock()
    private val service = AccountService(repository)

    @Test
    fun `listAccounts returns stored accounts`() {
        val account =
            AccountRecord(
                accountId = UUID.fromString("11111111-1111-7111-8111-111111111111"),
                ownerName = "Alice Carter",
                currency = "USD",
                balance = BigDecimal("250.00"),
                active = true,
                createdAt = Instant.parse("2026-07-15T10:15:30Z"),
            )

        whenever(repository.findAll()).thenReturn(Flux.just(account))

        StepVerifier
            .create(service.listAccounts())
            .assertNext { dto ->
                assertEquals(account.accountId, dto.id)
                assertEquals(account.ownerName, dto.ownerName)
                assertEquals(account.currency, dto.currency)
                assertEquals(account.balance, dto.balance)
                assertEquals(account.active, dto.active)
            }.verifyComplete()
    }

    @Test
    fun `getAccount returns not found for unknown id`() {
        val id = UuidV7Generator.generate()
        whenever(repository.findById(id)).thenReturn(Mono.empty())

        StepVerifier
            .create(service.getAccount(id))
            .expectErrorMatches { error -> error.message?.contains("Account not found") == true }
            .verify()
    }

    @Test
    fun `createAccount persists active account`() {
        val request =
            CreateAccountRequest(
                ownerName = "Bob Miller",
                currency = "eur",
                initialBalance = BigDecimal("99.99"),
            )
        val record =
            request.toRecord(
                id = UUID.fromString("11111111-1111-7111-8111-222222222222"),
                createdAt = Instant.parse("2026-07-15T10:15:30Z"),
            )

        whenever(repository.save(any())).thenAnswer { invocation ->
            Mono.just(invocation.getArgument(0))
        }

        val created = service.createAccount(request).block()!!

        assertEquals(record.ownerName, created.ownerName)
        assertEquals("EUR", created.currency)
        assertEquals(record.balance, created.balance)
        assertEquals(true, created.active)
        assertEquals(7, created.id.version())
        assertTrue(created.id.toString().isNotBlank())
        verify(repository).save(any())
    }
}
