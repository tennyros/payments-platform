# Feature Spec: PAYMENTS-5 Coroutine Mirror

## Context

The notification service currently uses Spring WebFlux and Reactor types end-to-end.
This document shows the equivalent Kotlin coroutine shape for the same service so the team can compare the two approaches without changing production behavior.

## Goal

Document how the current notification API would look if it were written with Kotlin coroutines instead of Reactor return types.

## Scope

- In scope:
  - Coroutine equivalents for the existing notification controller and service
  - Mapping between Reactor and coroutine types
  - Notes on Reactor-to-coroutine adapters
- Out of scope:
  - Production migration of `notification-service`
  - Changes to runtime dependencies
  - Changes to persistence or HTTP contracts

## API Mapping

- `Mono<NotificationDto>` maps to `suspend fun ...: NotificationDto`
- `Flux<NotificationDto>` maps to `Flow<NotificationDto>`
- `Mono`/`Flux` remain the production implementation for now

## Example Shape

### Reactor

```kotlin
@GetMapping("/{id}")
fun getNotification(@PathVariable id: UUID): Mono<NotificationDto> =
    notificationService.getNotification(id)

@PostMapping
fun createNotification(
    @RequestBody request: CreateNotificationRequest,
): Mono<ResponseEntity<NotificationDto>> =
    notificationService.createNotification(request).map {
        ResponseEntity.status(HttpStatus.CREATED).body(it)
    }
```

### Coroutines

```kotlin
@GetMapping("/{id}")
suspend fun getNotification(@PathVariable id: UUID): NotificationDto =
    notificationService.getNotification(id).awaitSingle()

@PostMapping
suspend fun createNotification(
    @RequestBody request: CreateNotificationRequest,
): ResponseEntity<NotificationDto> =
    ResponseEntity.status(HttpStatus.CREATED).body(
        notificationService.createNotification(request).awaitSingle(),
    )
```

### Service

Reactor:

```kotlin
fun listNotifications(): Flux<NotificationDto> =
    Flux.fromIterable(storage.values).map { it.toDto() }

fun getNotification(id: UUID): Mono<NotificationDto> =
    Mono.justOrEmpty(storage[id])
        .map { it.toDto() }
        .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found")))
```

Coroutines:

```kotlin
fun listNotifications(): Flow<NotificationDto> =
    storage.values.asFlow().map { it.toDto() }

suspend fun getNotification(id: UUID): NotificationDto =
    storage[id]?.toDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found")
```

## Adapters

- `mono.awaitSingle()` or `mono.awaitSingleOrNull()` for single values
- `flux.asFlow()` for multi-value streams
- Keep adapters at the boundary if the underlying repository remains Reactor-based
- Import adapters from `kotlinx.coroutines.reactor`

## Testing Notes

- The current Reactor tests remain the source of truth for production behavior
- Coroutine samples in this doc are illustrative only
- If the service is migrated later, add coroutine-native controller and service tests

## Acceptance Criteria

- [ ] Coroutine mapping is documented for the notification service
- [ ] Reactor and coroutine examples are shown side by side
- [ ] Production code remains unchanged
