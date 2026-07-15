package com.payments.account.api

import com.payments.account.service.AccountService
import com.payments.common.account.AccountDto
import com.payments.common.account.CreateAccountRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountService: AccountService,
) {
    @GetMapping
    fun listAccounts(): Flux<AccountDto> = accountService.listAccounts()

    @GetMapping("/{id}")
    fun getAccount(
        @PathVariable id: UUID,
    ): Mono<AccountDto> = accountService.getAccount(id)

    @PostMapping
    fun createAccount(
        @Valid @RequestBody request: CreateAccountRequest,
    ): Mono<ResponseEntity<AccountDto>> =
        accountService.createAccount(request).map {
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        }
}
