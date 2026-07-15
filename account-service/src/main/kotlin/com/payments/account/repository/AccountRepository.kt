package com.payments.account.repository

import com.payments.account.model.AccountRecord
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface AccountRepository : ReactiveCrudRepository<AccountRecord, UUID>
