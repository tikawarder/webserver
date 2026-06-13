# Java Learning Portfolio

A self-built web application documenting a full Java backend learning journey — from Servlets to Microservices.
Each branch represents a completed learning milestone, all merged into `master` as a chronological history.

---

## Learning path (merged to master)

| Branch | What was learned |
|---|---|
| `master` | Base — full learning history in chronological order |
| `5.SpringBoot` | Spring MVC, JPA, Validation, DTOs, Pagination, Unit & Integration testing, `@Async` + `CompletableFuture` |
| `LearnVulnerabilities` | OWASP Top 10 in practice: XSS, CSRF, IDOR, SQL injection, SSRF — with fixes |
| `React` | React basics, components, fetch API, webpack, npm |
| `6.Cloud` | GCP deployment, Docker on Cloud Run, Terraform |
| `sql` | JPA/SQL deep dive: N+1, ACID, JOINs, all 4 relationship types, LAZY/EAGER, Flyway migrations, Optimistic Locking |

---

## Current development

**Branch:** `microservices`

Full microservices stack:
- Spring Cloud Gateway
- AuthService (JWT)
- DatabaseServer
- NotificationService
- Kafka (Transactional Outbox Pattern)
- Resilience4j circuit breaker
- Zipkin distributed tracing
- Docker Compose with healthchecks

---

## How to run (sql branch — SQL/JPA demo)

**Prerequisites:** Docker

```bash
git checkout sql
cd /path/to/webserver
docker compose up --build
```

App starts at `http://localhost:8081`

### Demo endpoints

```bash
# N+1 problem — watch the logs for multiple vs single SQL
GET  /api/demo/n1/broken
GET  /api/demo/n1/fixed

# ACID + @Transactional rollback
POST /api/demo/acid/transfer?fromId=1&toId=2&amount=100
POST /api/demo/acid/transfer-fail?fromId=1&toId=2&amount=10

# JOIN types
GET  /api/demo/join/inner
GET  /api/demo/join/left

# Aggregates (GROUP BY, SUM, COUNT)
GET  /api/demo/aggregates/revenue-per-city
GET  /api/demo/aggregates/order-count

# Optimistic Locking (@Version) conflict demo
POST /api/demo/optimistic-lock/demo?orderId=1

# JPA relationships (OneToOne, OneToMany, ManyToMany, FetchType)
GET  /api/relations/one-to-one/lazy/1
GET  /api/relations/one-to-one/join-fetch
GET  /api/relations/many-to-many/orders-with-tags
GET  /api/relations/full-customer/1
```

### What Flyway does on first start

```
Migrating schema `usersdb` to version "1 - create core tables"
Migrating schema `usersdb` to version "2 - create demo tables"
Migrating schema `usersdb` to version "3 - add optimistic locking"
Successfully applied 3 migrations
```

On subsequent starts: `Schema is up to date. No migration necessary.`

---

## Next learning phases

| Branch | Builds on | Topic |
|---|---|---|
| `PlaywrightE2E` | `microservices` | End-to-end tests |
| `kubernetes` | `microservices` | K8s deployment (Minikube) |
| `ci-cd` | `kubernetes` | GitHub Actions pipeline |
