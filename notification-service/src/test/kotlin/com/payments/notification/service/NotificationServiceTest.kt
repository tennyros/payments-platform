package com.payments.notification.service

import com.payments.common.id.UuidV7Generator
import com.payments.common.notification.CreateNotificationRequest
import com.payments.common.notification.NotificationChannel
import com.payments.common.notification.NotificationStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class NotificationServiceTest {
    private val service = NotificationService()

    @Test
    fun `listNotifications returns empty flux initially`() {
        StepVerifier
            .create(service.listNotifications())
            .verifyComplete()
    }

    @Test
    fun `createNotification stores pending notification`() {
        val request =
            CreateNotificationRequest(
                recipient = "user@example.com",
                channel = NotificationChannel.EMAIL,
                subject = "Payment received",
                body = "Your payment was processed successfully",
            )

        val created = service.createNotification(request).block()!!

        assertEquals("user@example.com", created.recipient)
        assertEquals(NotificationChannel.EMAIL, created.channel)
        assertEquals(NotificationStatus.PENDING, created.status)
        assertEquals(7, created.id.version())
        assertTrue(created.id.toString().isNotBlank())

        StepVerifier
            .create(service.getNotification(created.id))
            .assertNext { dto -> assertEquals(created, dto) }
            .verifyComplete()
    }

    @Test
    fun `getNotification returns not found for unknown id`() {
        StepVerifier
            .create(service.getNotification(UuidV7Generator.generate()))
            .expectErrorMatches { error -> error.message?.contains("Notification not found") == true }
            .verify()
    }
}
