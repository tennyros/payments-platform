package com.payments.account

import com.payments.account.repository.AccountRepository
import com.payments.common.account.AccountDto
import com.payments.common.account.CreateAccountRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.math.BigDecimal
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountServiceIntegrationTest {
    @Autowired
    private lateinit var accountRepository: AccountRepository

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        webTestClient =
            WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
        prepareSchema()
        accountRepository.deleteAll().block()
    }

    private fun prepareSchema() {
        val dataSource =
            DriverManagerDataSource().apply {
                setDriverClassName("org.h2.Driver")
                setUrl("jdbc:h2:file:./build/testdb/accounts;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                username = "sa"
                password = ""
            }

        ResourceDatabasePopulator(
            ClassPathResource("db/test/schema/001-create-account-table.sql"),
        ).execute(dataSource)
    }

    @Test
    fun `create account persists row and returns created account`() {
        val request =
            CreateAccountRequest(
                ownerName = "Alice Carter",
                currency = "usd",
                initialBalance = BigDecimal("250.00"),
            )

        webTestClient
            .post()
            .uri("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(AccountDto::class.java)
            .value { actual ->
                val account = actual!!
                assertEquals(request.ownerName, account.ownerName)
                assertEquals("USD", account.currency)
                assertEquals(request.initialBalance, account.balance)
                assertEquals(true, account.active)
                assertEquals(7, account.id.version())
            }

        val accounts = accountRepository.findAll().collectList().block()!!
        assertEquals(1, accounts.size)
        val storedAccount = accounts.first()
        assertEquals(request.ownerName, storedAccount.ownerName)
        assertEquals("USD", storedAccount.currency)
        assertEquals(request.initialBalance, storedAccount.balance)
        assertEquals(true, storedAccount.active)
        assertEquals(7, storedAccount.id.version())

        webTestClient
            .get()
            .uri("/accounts/${storedAccount.id}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(AccountDto::class.java)
            .value { actual ->
                val account = actual!!
                assertEquals(storedAccount.accountId, account.id)
                assertEquals(storedAccount.ownerName, account.ownerName)
                assertEquals(storedAccount.currency, account.currency)
            }
    }
}
