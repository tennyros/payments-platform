package com.payments.notification.model

import com.payments.common.id.UuidV7Generator
import com.payments.common.notification.CreateNotificationRequest
import com.payments.common.notification.NotificationChannel
import com.payments.common.notification.NotificationDto
import com.payments.common.notification.NotificationStatus
import java.time.Instant
import java.util.UUID

data class NotificationRecord(
    val id: UUID,
    val recipient: String,
    val channel: NotificationChannel,
    val subject: String,
    val body: String,
    val status: NotificationStatus,
    val createdAt: Instant,
)

fun NotificationRecord.toDto(): NotificationDto =
    NotificationDto(
        id = id,
        recipient = recipient,
        channel = channel,
        subject = subject,
        body = body,
        status = status,
        createdAt = createdAt,
    )

fun CreateNotificationRequest.toRecord(
    id: UUID = UuidV7Generator.generate(),
    createdAt: Instant = Instant.now(),
): NotificationRecord =
    NotificationRecord(
        id = id,
        recipient = recipient,
        channel = channel!!,
        subject = subject,
        body = body,
        status = NotificationStatus.PENDING,
        createdAt = createdAt,
    )
