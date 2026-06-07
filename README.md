# Microservices Demo App

A hands-on learning project that grew from a simple servlet into a full microservices architecture.
It demonstrates how independent Spring Boot services communicate, stay resilient, and handle events asynchronously — all running locally with Docker Compose.

---

## What it does

Users can log in, add people to a database, and browse the list with pagination.
Behind the scenes, every new person triggers a Kafka event that a separate notification service picks up — no direct coupling between services.

---

## Architecture

```
Browser
  └── localhost:9080
        └── Nginx (UserInputServer / React)
              └── /api/** → Spring Cloud Gateway :8090
                    ├── /api/auth/** → AuthService :8083  (JWT login/logout)
                    └── /api/**      → DatabaseServer :8080 (person CRUD)
                                            └── Kafka → NotificationService :8082
                                            └── Zipkin (distributed tracing)
PostgreSQL
  ├── authdb   (accounts, roles)
  └── usersdb  (persons, outbox_messages)
```

---

## Quick start

**Requirements:** Docker and Docker Compose

```bash
git clone <repo>
cd webserver
docker compose up --build
```

Open **http://localhost:9080**

Login credentials: `admin` / `password`

The gateway and frontend wait for the backend services to become healthy before starting — no manual ordering needed.

---

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React, Nginx |
| Gateway | Spring Cloud Gateway |
| Auth | Spring Security, JWT (HttpOnly cookie) |
| Backend | Spring Boot 3.4.1, Spring Data JPA |
| Database | PostgreSQL 15 |
| Messaging | Apache Kafka, Transactional Outbox Pattern |
| Resilience | Resilience4j Circuit Breaker |
| Tracing | Zipkin (distributed trace IDs across all services) |
| Testing | Spring Cloud Contract, Playwright E2E |
| Infra | Docker Compose with healthchecks |

---

## Key implementation highlights

- **JWT via HttpOnly cookie** — the token never touches JavaScript; the browser sends it automatically on every request
- **Transactional Outbox** — person creation and its Kafka event are written in one DB transaction; a background scheduler publishes reliably (at-least-once delivery)
- **Circuit Breaker** — DatabaseServer wraps calls to AuthService in a Resilience4j breaker; auth failures degrade gracefully instead of cascading
- **Service healthchecks** — Docker Compose waits for `/actuator/health` to return `UP` on each service before starting dependents
- **Distributed tracing** — every request carries a `traceId` and `spanId` across all services; traces are visible at http://localhost:9411

---

## Roles

Three roles are defined (`ADMIN`, `USER`, `GUEST`) and stored per account. Currently the default `admin` user is seeded on startup. Role-based access control (e.g. read-only for USER) is a planned next step.

---

## What's next / learning roadmap

- [ ] Role-based authorization (ADMIN vs USER permissions)
- [ ] Kubernetes deployment (Minikube config already started)
- [ ] Prometheus + Grafana metrics
- [ ] CI/CD with GitHub Actions
- [ ] Secret management (Vault or GCP Secret Manager)

---

## Observability

| Service | URL |
|---|---|
| App | http://localhost:9080 |
| Zipkin traces | http://localhost:9411 |
| Auth actuator | http://localhost:9083/actuator/health |
| DB actuator | http://localhost:9081/actuator/health |
