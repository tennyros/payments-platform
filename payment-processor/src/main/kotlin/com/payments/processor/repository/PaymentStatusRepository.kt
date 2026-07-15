package com.payments.processor.repository

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.UUID

@Repository
class PaymentStatusRepository(
    private val databaseClient: DatabaseClient,
) {
    fun updateStatus(
        paymentId: UUID,
        status: String,
        updatedAt: Instant,
    ): Mono<Long> =
        databaseClient
            .sql(
                """
                UPDATE "payment_queue"
                SET "status" = :status, "updated_at" = :updatedAt
                WHERE "id" = :paymentId
                """.trimIndent(),
            ).bind("status", status)
            .bind("updatedAt", updatedAt)
            .bind("paymentId", paymentId)
            .fetch()
            .rowsUpdated()
}
