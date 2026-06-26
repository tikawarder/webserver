# Microservices Demo App

A hands-on learning project that grew from a simple servlet into a full microservices architecture.
It demonstrates how independent Spring Boot services communicate, stay resilient, and handle events asynchronously — all running locally with Docker Compose.

---

## What it does

Users can log in via **Keycloak** (OAuth2/OIDC), add people to a database, and browse the list with pagination.
Behind the scenes, every new person triggers a Kafka event that a separate notification service picks up — no direct coupling between services.

---

## Architecture

```
Browser
  └── localhost:9080
        └── Nginx (UserInputServer / React)
              │
              ├── Login → Redirect to Keycloak (localhost:8180)
              │            ├── OAuth2 Authorization Code Flow + PKCE
              │            └── Returns JWT Access Token (RS256 signed)
              │
              └── /api/** → Spring Cloud Gateway :8090 (validates JWT via JWKS)
                    ├── /api/auth/** → AuthService :8083  (supplementary user validation)
                    └── /api/**      → DatabaseServer :8080 (person CRUD + RBAC)
                                            └── Kafka → NotificationService :8082
                                            └── Zipkin (distributed tracing)

Keycloak :8080
  ├── Realm: webserver-realm
  ├── Clients: react-app (public), gateway-client (confidential)
  ├── Roles: ADMIN, USER, GUEST
  └── Users: admin, user1, guest (all password: "password")

PostgreSQL
  ├── usersdb       (persons, outbox_messages)
  ├── authdb        (accounts, roles)
  └── keycloakdb    (Keycloak internal data)
```

---

## Quick start

**Requirements:** Docker and Docker Compose

```bash
git clone <repo>
cd webserver
git checkout keycloak-iam
docker compose up --build
```

Open **http://localhost:9080** → Click "Sign in with Keycloak" → Login: `admin` / `password`

| Service | URL |
|---|---|
| App | http://localhost:9080 |
| Keycloak Admin | http://localhost:8180/admin (admin/admin) |
| Zipkin traces | http://localhost:9411 |
| Auth actuator | http://localhost:9083/actuator/health |
| DB actuator | http://localhost:9081/actuator/health |

> **Fresh install note:** PostgreSQL init scripts (`db/`) only run on an empty data volume.
> If you previously ran another branch and the volume already exists, run:
> ```bash
> docker compose down -v && docker compose up --build
> ```

---

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React, Nginx |
| Gateway | Spring Cloud Gateway |
| IAM | **Keycloak 26** (OAuth2, OpenID Connect, RBAC) |
| Auth | Spring Security OAuth2 Resource Server (JWT via JWKS) |
| Backend | Spring Boot 3.4.1, Spring Data JPA |
| Database | PostgreSQL 15 |
| Messaging | Apache Kafka, Transactional Outbox Pattern |
| Resilience | Resilience4j Circuit Breaker |
| Tracing | Zipkin (distributed trace IDs across all services) |
| Metrics | Micrometer + Prometheus + Grafana (JVM dashboard) |
| Testing | Spring Cloud Contract, Playwright E2E |
| Infra | Docker Compose with healthchecks |

---

## Key implementation highlights

- **Keycloak IAM** — centralized identity management with OAuth2/OIDC. Login is handled by Keycloak's themed login page, not custom forms
- **JWT via Bearer token** — RS256 signed tokens validated against Keycloak's JWKS public keys. No shared secrets between services
- **Role-Based Access Control (RBAC)** — `@PreAuthorize` annotations enforce: ADMIN=full, USER=read+write, GUEST=read-only
- **Offline token validation** — if Keycloak goes down, existing tokens still work (public keys are cached)
- **Transactional Outbox** — person creation and its Kafka event are written in one DB transaction
- **Circuit Breaker** — DatabaseServer wraps calls to AuthService in a Resilience4j breaker
- **Distributed tracing** — every request carries a `traceId` and `spanId` across all services

---

## Roles & Access Control

| Role | GET /persons | POST /persons | DELETE /persons/{id} |
|---|---|---|---|
| ADMIN | ✅ | ✅ | ✅ |
| USER | ✅ | ✅ | ❌ |
| GUEST | ✅ | ❌ | ❌ |

Pre-seeded users: `admin` (ADMIN), `user1` (USER), `guest` (GUEST) — all with password `password`

---

## Keycloak Configuration

The realm is auto-imported from `keycloak/realm-export.json` on first startup:

- **Realm:** `webserver-realm`
- **Clients:**
  - `react-app` — public client for the React SPA (uses PKCE)
  - `gateway-client` — confidential client for backend services
- **Access token lifespan:** 5 minutes
- **JWKS endpoint:** `http://localhost:8180/realms/webserver-realm/protocol/openid-connect/certs`

---

## ReactiveService — Spring WebFlux + R2DBC

A learning chapter on the `reactive` branch: the same Person CRUD as `DatabaseServer`, rebuilt
fully non-blocking. Instead of one thread per request (MVC + JDBC), an event loop handles
thousands of in-flight requests with a handful of I/O threads — no thread blocks while waiting
for PostgreSQL to respond.

**Port:** `9084` (Docker) / `8084` (local)

| Layer | Technology |
|---|---|
| HTTP | Spring WebFlux — controllers return `Mono<T>` / `Flux<T>` |
| Service | Reactor operators (`switchIfEmpty`, `flatMap`, `map`) |
| Repository | `ReactiveCrudRepository` — non-blocking `Mono`/`Flux` results |
| DB driver | R2DBC — non-blocking wire protocol, not JDBC |
| Streaming | Server-Sent Events on `/stream` — elements pushed over time |

---

## What's next / learning roadmap

- [x] Role-based authorization (ADMIN vs USER permissions)
- [x] Keycloak IAM integration
- [ ] Kubernetes deployment (Minikube config already started)
- [ ] CI/CD with GitHub Actions
- [ ] Secret management (Vault or GCP Secret Manager)

---

## E2E Testing

End-to-end tests are written with [Playwright](https://playwright.dev/).
The app must be running (`docker compose up`) before executing the tests.

```bash
cd UserInputServer
npm install
npx playwright install chromium
```

| Command | Mode |
|---|---|
| `npm run e2e` | Headed, sequential, slowMo=500ms — watch it run |
| `npm run e2e:fast` | Headless, 4 parallel workers — ~6s, use after merges |
| `npx playwright test --ui` | Interactive UI — pick tests, inspect steps, replay |

### What is covered

| File | Tests | What it checks |
|---|---|---|
| `smoke.spec.js` | 3 | App loads, login succeeds, logout works |
| `auth.spec.js` | 2 | Form hidden before login, wrong password shows error |
| `person-flow.spec.js` | 1 | Full flow: login → create person → cleanup |
| `validation.spec.js` | 3 | Empty form errors, valid save resets form, DELETE endpoint |

### Implementation notes

- All interactive elements have `data-testid` attributes — selectors are stable across UI text changes
- Each test cleans up after itself via `DELETE /api/persons/{id}` — no data accumulates between runs
- `CI=true` switches to headless + parallel mode; GitHub Actions sets this automatically

---

## Observability

| Service | URL |
|---|---|
| App | http://localhost:9080 |
| Keycloak Admin | http://localhost:8180/admin |
| Zipkin traces | http://localhost:9411 |
| Prometheus | http://localhost:9091 |
| Grafana | http://localhost:3000 (admin / admin) |
| Auth actuator | http://localhost:9083/actuator/health |
| DB actuator | http://localhost:9081/actuator/health |
