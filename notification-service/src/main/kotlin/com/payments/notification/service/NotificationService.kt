package com.payments.notification.service

import com.payments.common.notification.CreateNotificationRequest
import com.payments.common.notification.NotificationDto
import com.payments.notification.model.NotificationRecord
import com.payments.notification.model.toDto
import com.payments.notification.model.toRecord
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationService {
    private val storage = ConcurrentHashMap<UUID, NotificationRecord>()

    fun listNotifications(): Flux<NotificationDto> = Flux.fromIterable(storage.values).map { it.toDto() }

    fun getNotification(id: UUID): Mono<NotificationDto> =
        Mono
            .justOrEmpty(storage[id])
            .map { it.toDto() }
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found")))

    fun createNotification(request: CreateNotificationRequest): Mono<NotificationDto> {
        val record = request.toRecord()
        storage[record.id] = record
        return Mono.just(record.toDto())
    }
}
