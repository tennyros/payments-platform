package com.payments.notification.api

import com.payments.common.notification.CreateNotificationRequest
import com.payments.common.notification.NotificationChannel
import com.payments.common.notification.NotificationStatus
import com.payments.notification.service.NotificationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class NotificationControllerTest {
    private val notificationService = NotificationService()
    private val controller = NotificationController(notificationService)

    @Test
    fun `createNotification returns created notification`() {
        val request =
            CreateNotificationRequest(
                recipient = "user@example.com",
                channel = NotificationChannel.EMAIL,
                subject = "Payment received",
                body = "Your payment was processed successfully",
            )

        val response = controller.createNotification(request).block()!!
        val notification = response.body!!

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(request.recipient, notification.recipient)
        assertEquals(request.channel, notification.channel)
        assertEquals(request.subject, notification.subject)
        assertEquals(request.body, notification.body)
        assertEquals(NotificationStatus.PENDING, notification.status)
        assertEquals(7, notification.id.version())
        assertNotNull(notification.id)
        assertNotNull(notification.createdAt)
    }

    @Test
    fun `listNotifications returns empty array by default`() {
        val notifications = controller.listNotifications().collectList().block()!!

        assertEquals(0, notifications.size)
    }
}
