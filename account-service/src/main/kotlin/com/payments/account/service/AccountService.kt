package com.payments.account.service

import com.payments.account.model.toDto
import com.payments.account.model.toRecord
import com.payments.account.repository.AccountRepository
import com.payments.common.account.AccountDto
import com.payments.common.account.CreateAccountRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {
    fun listAccounts(): Flux<AccountDto> = accountRepository.findAll().map { it.toDto() }

    fun getAccount(id: UUID): Mono<AccountDto> =
        accountRepository
            .findById(id)
            .map { it.toDto() }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))

    fun createAccount(request: CreateAccountRequest): Mono<AccountDto> = accountRepository.save(request.toRecord()).map { it.toDto() }
}
