# Common Product Specification

## Product

Payments Platform is a microservice-based payment system built as a Kotlin monorepo.

## Architecture

- Java 21 runtime
- Kotlin 2.4.0
- Kotlin + Gradle multi-module monorepo
- Reactive HTTP stack with Spring WebFlux
- Container-first local development
- Kubernetes-ready deployment model

## Core Services

- `api-gateway` - entry point for client traffic
- `account-service` - account and balance domain
- `payment-processor` - payment execution, queue persistence, and orchestration
- `notification-service` - notification intake and delivery events
- `common` - shared contracts and utilities

## Infrastructure

- PostgreSQL for transactional data
- Redis for caching and short-lived state
- Local development via `docker-compose`

## Engineering Rules

- Prefer reactive APIs for new service code.
- Keep service boundaries explicit.
- Put shared DTOs and contracts in `common` only when necessary.
- Track every significant change in a dedicated spec file.
