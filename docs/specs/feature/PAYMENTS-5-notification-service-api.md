# Feature Spec: PAYMENTS-5

## Context

The platform needs a reactive notification service to store and expose notification requests as a first step before event-driven delivery.

## Goal

Provide a basic notification service API with reactive CRUD-style endpoints.

## Scope

- In scope:
  - `GET /notifications`
  - `GET /notifications/{id}`
  - `POST /notifications`
  - Shared notification DTOs in `common`
- Out of scope:
  - Kafka consumption
  - Actual email/SMS delivery providers
  - Persistent storage

## API Changes

- `GET /notifications` returns stored notifications.
- `GET /notifications/{id}` returns a single notification by UUID.
- `POST /notifications` creates a pending notification.

## Data Changes

- None yet.

## Implementation Notes

- The service is reactive and uses in-memory storage for now.
- Notification channel and status are modeled as shared enums in `common`.
- See the companion coroutine mirror spec for the Kotlin coroutine equivalent of this API shape.

## Testing

- Verify list endpoint returns stored notifications.
- Verify creation returns a generated UUID.
- Verify unknown notification returns 404.

## Acceptance Criteria

- [ ] Notification service exposes reactive endpoints.
- [ ] Shared notification models are available in `common`.
- [ ] Branch and spec use the same ticket ID.
