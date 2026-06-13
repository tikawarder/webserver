# Branch overview

## Learning path — completed phases (merged to master)

| Branch | What was learned |
|---|---|
| `master` | Base — full learning history in chronological order |
| `5.SpringBoot` | Spring Boot: MVC, JPA, Validation, DTOs, Pagination, Unit & Integration testing |
| `LearnVulnerabilities` | OWASP top 10 in practice: XSS, CSRF, IDOR, SQL injection, SSRF — with fixes |
| `React` | React basics, components, fetch API, webpack, npm |
| `6.Cloud` | GCP deployment, Docker on Cloud Run, Terraform |

These are all merged into `master` — the full journey is readable via `git log --oneline master`.

---

## Active development branch

| Branch | Status | Description |
|---|---|---|
| `microservices` | 🔵 **current** | Full microservices stack: Spring Cloud Gateway, AuthService (JWT), DatabaseServer, NotificationService, Kafka (Outbox pattern), Resilience4j, Zipkin distributed tracing, Docker Compose with healthchecks |

---

## Next learning phases (not yet merged)

| Branch | Builds on | Description | When |
|---|---|---|---|
| `PlaywrightE2E` | `microservices` | End-to-end tests for login and person registration flow — to be rewritten from stash | next |
| `kubernetes` | `microservices` | K8s deployment started (Minikube) — continue after microservices is merged | after PlaywrightE2E |
| `ci-cd` | `kubernetes` | GitHub Actions pipeline + Terraform — CI/CD on the complete stack | after kubernetes |

---

## Standalone learning branches

| Branch | Description | Action |
|---|---|---|
| `sql` | SQL N+1 problem demo, JOIN FETCH solution — independent of the main stack | merge to master when ready |
| `AI-implementation` | LLM integration experiment, Claude rules/skills setup | keep separate, not part of main line |

---

## Archived / superseded

| Branch | Reason |
|---|---|
| `Event-driven-arch` | Early Kafka experiment — fully superseded by `microservices` |
| ~~`feat-microservices`~~ | Deleted — its 2 unique commits (AuthService + NotificationService extraction) are already in `microservices` |

---

## Stash

| Stash | Content |
|---|---|
| `stash@{0}` | `feat: Playwright E2E tests` — saved on `microservices`, to be popped when working on `PlaywrightE2E` |

---

## Suggested merge order

```
microservices  →  master          (when this phase is "done")
     │
     ├── PlaywrightE2E  →  microservices   (bring in E2E tests)
     ├── kubernetes     →  microservices   (K8s deployment)
     └── ci-cd          →  kubernetes      (pipeline on top)

sql            →  master          (anytime, independent)
```
