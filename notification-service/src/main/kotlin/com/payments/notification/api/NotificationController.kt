package com.payments.notification.api

import com.payments.common.notification.CreateNotificationRequest
import com.payments.common.notification.NotificationDto
import com.payments.notification.service.NotificationService
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
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @GetMapping
    fun listNotifications(): Flux<NotificationDto> = notificationService.listNotifications()

    @GetMapping("/{id}")
    fun getNotification(
        @PathVariable id: UUID,
    ): Mono<NotificationDto> = notificationService.getNotification(id)

    @PostMapping
    fun createNotification(
        @Valid @RequestBody request: CreateNotificationRequest,
    ): Mono<ResponseEntity<NotificationDto>> =
        notificationService.createNotification(request).map {
            ResponseEntity.status(HttpStatus.CREATED).body(it)
        }
}
