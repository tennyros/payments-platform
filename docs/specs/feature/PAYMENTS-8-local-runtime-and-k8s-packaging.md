# PAYMENTS-8: Local Runtime and Kubernetes Packaging

## Goal

Provide a real-world local development and deployment shape for the payments monorepo.

## Scope

- Add a single local launcher for developers.
- Containerize each service with a shared monorepo-friendly Dockerfile.
- Extend local `docker-compose` to run the full stack.
- Add a Helm chart skeleton for Kubernetes deployment.
- Enable health probes in service configs for Kubernetes readiness/liveness.

## Acceptance Criteria

- `make up` starts the infrastructure and all services locally.
- `make down` stops the local environment.
- Each service can be built into a runnable container image from the repo root.
- Helm chart templates render deployments and services for all microservices.
- Kubernetes probes are available on the services through actuator health endpoints.
