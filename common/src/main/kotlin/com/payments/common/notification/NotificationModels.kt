package com.payments.common.notification

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.util.UUID

enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
}

enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED,
}

data class NotificationDto(
    val id: UUID,
    val recipient: String,
    val channel: NotificationChannel,
    val subject: String,
    val body: String,
    val status: NotificationStatus,
    val createdAt: Instant,
)

data class CreateNotificationRequest(
    @field:NotBlank
    val recipient: String,
    @field:NotNull
    val channel: NotificationChannel?,
    @field:NotBlank
    val subject: String,
    @field:NotBlank
    val body: String,
)
