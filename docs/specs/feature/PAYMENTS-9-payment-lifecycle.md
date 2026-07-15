# Feature Spec: PAYMENTS-9

## Context

Payment processing needs a lifecycle step after creation so an operator or downstream system can move a payment from `PENDING` to a terminal state.

## Goal

Add a reactive payment status transition flow with persistence and an emitted status-changed Kafka event.

## Scope

- In scope:
  - Add `PATCH /payments/{id}/status` to the payment processor API.
  - Persist status transitions in the payment queue.
  - Publish a payment status changed event to Kafka.
  - Track the last update timestamp for each payment record.
- Out of scope:
  - Workflow orchestration across multiple services.
  - Automatic retries or compensation logic.
  - Customer-facing payment status history endpoints.

## API Changes

- Endpoints:
  - `PATCH /payments/{id}/status`
- Events:
  - `payments.payment-status-changed`
- DTOs:
  - `UpdatePaymentStatusRequest`
  - `PaymentStatusChangedEvent`

## Data Changes

- Tables:
  - `payment_queue`
- Indexes:
  - None.
- Migrations:
  - Add `updated_at` to `payment_queue`.
  - Backfill existing rows from `created_at`.

## Implementation Notes

- Service boundaries:
  - `PaymentService` owns the transition rules.
  - `PaymentStatusRepository` performs the update.
  - `KafkaPaymentEventPublisher` emits the event.
- Reactive flow:
  - Load the payment record.
  - Validate the requested status transition.
  - Persist the new state.
  - Reload and publish the updated event payload.
- Error handling:
  - Return `400` for invalid requests.
  - Return `404` when the payment does not exist.
  - Return `409` when the payment is not in a transitionable state.

## Testing

- Unit tests:
  - Validate accepted and rejected status transitions.
  - Validate event payload mapping.
- Integration tests:
  - Create a payment.
  - Transition it to `PROCESSED`.
  - Verify persistence and Kafka publication.
- Contract tests:
  - None for now.

## Acceptance Criteria

- [ ] A payment can transition from `PENDING` to a terminal status through the API.
- [ ] The updated payment state is stored in the database.
- [ ] A status-changed Kafka event is published after a successful transition.
- [ ] The build passes with unit and integration tests.
