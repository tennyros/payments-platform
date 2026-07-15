package com.payments.processor.repository

import com.payments.processor.model.PaymentRecord
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface PaymentRepository : ReactiveCrudRepository<PaymentRecord, UUID>
