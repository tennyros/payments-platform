# Feature Spec: PAYMENTS-6

## Context

The platform needs a reactive payment processor that stores payment requests and exposes a simple payment API on top of PostgreSQL.

## Goal

Provide a reactive payment processor API with Liquibase-managed schema and a payment queue model.

## Scope

- In scope:
  - `GET /payments`
  - `GET /payments/{id}`
  - `POST /payments`
  - Shared payment DTOs in `common`
  - PostgreSQL schema managed by XML Liquibase changelogs
- Out of scope:
  - Payment orchestration with external providers
  - Retry and compensation workflows
  - Kafka event publication

## API Changes

- `GET /payments` returns stored payments.
- `GET /payments/{id}` returns one payment by UUID.
- `POST /payments` creates a pending payment.

## Data Changes

- `payment_queue` stores payment rows.
- `payment_status_reference` stores status reference rows.
- Liquibase seeds the status reference table.

## Implementation Notes

- The service is reactive via WebFlux and R2DBC.
- Liquibase uses XML changelogs under `db/changelog/release/01.00.00`.
- Payment statuses are modeled as shared enums in `common`.

## Testing

- Verify list endpoint returns stored payments.
- Verify creation returns a generated UUID.
- Verify unknown payment returns 404.

## Acceptance Criteria

- [ ] Payment processor exposes reactive payment endpoints.
- [ ] PostgreSQL schema is managed by Liquibase XML changelogs.
- [ ] Status reference data is seeded from Liquibase.
- [ ] Branch and spec use the same ticket ID.
