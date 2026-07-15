# Feature Spec: PAYMENTS-7

## Context

The platform needs a reactive account service with durable storage so payment and balance workflows can rely on persistent account data.

## Goal

Provide a reactive account service backed by PostgreSQL and Liquibase-managed schema.

## Scope

- In scope:
  - `GET /accounts`
  - `GET /accounts/{id}`
  - `POST /accounts`
  - Shared account DTOs in `common`
  - PostgreSQL persistence for accounts
- Out of scope:
  - Account balance mutation workflows
  - Reservation or locking semantics
  - Redis caching usage

## API Changes

- `GET /accounts` returns stored accounts.
- `GET /accounts/{id}` returns one account by UUID.
- `POST /accounts` creates a new active account.

## Data Changes

- `account` stores account rows.
- Liquibase manages the account schema in XML.
- New account ids are generated in the application and use UUIDv7.

## Implementation Notes

- The service is reactive via WebFlux and R2DBC.
- Account currency values are normalized to uppercase.
- Shared account DTOs and request models live in `common`.

## Testing

- Verify list endpoint returns stored accounts.
- Verify creation returns a generated UUIDv7.
- Verify unknown account returns 404.
- Verify the account is persisted and can be fetched after creation.

## Acceptance Criteria

- [ ] Account service exposes reactive endpoints.
- [ ] PostgreSQL schema is managed by Liquibase XML changelogs.
- [ ] New account ids use UUIDv7.
- [ ] Branch and spec use the same ticket ID.
