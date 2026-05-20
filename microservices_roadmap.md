# Monolitból Microservice-ekké — Teljes Útiterv
### Webserver projekt | AWS + Kubernetes végállapot

_Forrás: claude-sonnet-4-6 + gemini-3-auto + gemini-3.5-flash összesítése | 2026-05-20_

---

## Jelenlegi állapot

```
UserInputServer (React/Node) ──HTTP──► DatabaseServer (Spring Boot) ──► PostgreSQL
                                              │
                                           Kafka (már megvan!)
                                              │
                                    .github/workflows/ci.yml (már megvan!)
                                    Terraform/main.tf (már megvan!)
```

A projekt kiváló kiindulási alap:
- A **Kafka** már fut → az event-driven szétbontás alapköve kész
- A **CI/CD pipeline** már létezik → csak kibővíteni kell
- A **Terraform** már megvan → csak az AWS-re kell ráállítani
- A `DatabaseServer`-ben már vannak azonosítható **domain-ek**: `AuthController`, `PersonController`

---

## A Migrációs Stratégia: Strangler Fig Pattern

> Nem dobod el a monolitot — fokozatosan "kiszeded" belőle a funkciókat, és API Gateway mögé teszed az újakat.

```
MOST:      [UserInputServer] ──► [DatabaseServer (monolit)] ──► PostgreSQL

KÖZBEN:    [UserInputServer] ──► [API Gateway] ──► [DatabaseServer (zsugorodik)]
                                              └──► [AuthService] ──► auth_db
                                              └──► [NotificationService] ──► Kafka

VÉGÉN:     [UserInputServer] ──► [API Gateway] ──► [PersonProfileService]
                                              └──► [AuthService]
                                              └──► [NotificationService]
                                              └──► [SearchService + Redis]
```

---

## Fázisok

### 1. Fázis: Logikai szétválasztás + API Gateway (Lokális, 1-2 hét)

Az infrastruktúrát csak **akkor** bővítjük, ha a kód már fel van osztva.

**Domain Driven Design (DDD) alapján azonosítandó szervizek:**
- `DatabaseServer` → `AuthService` + `PersonProfileService`
- `UserInputServer` → marad frontend BFF szerepben

**Első kivágandó szerviz — két jelölt:**
1. **NotificationService** (Kafka consumer) — a legkönnyebb, mert az esemény már megy a Kafkán
2. **AuthService** — önállóbb, kevesebb függőség a többi domaintől

**Database per Service pattern:**
```
DatabaseServer  ──► usersdb (PostgreSQL - meglévő)
AuthService     ──► authdb  (PostgreSQL - külön séma)
NotificationService ──► nincs saját DB (csak Kafka consumer)
```

**API Gateway bevezetése:**
- Spring Cloud Gateway (ha Spring-es megközelítés kell)
- Nginx reverse proxy (egyszerűbb, gyorsabb)
- A frontend ezentúl csak a Gateway-jel kommunikál

**Mit tanulsz:** DDD alapok, Strangler Fig, Database per Service, Saga Pattern, Flyway/Liquibase migrációk, Eventual Consistency

---

### 2. Fázis: Resilience + Caching (Lokális, 1 hét)

Mi történik, ha egy szerviz leáll vagy belassul?

- **Resilience4j** — Circuit Breaker, Retry, Timeout pattern
- **Redis** — keresési eredmények gyorsítótárazása, session kezelés
- **N+1 probléma** javítása Hibernate szinten

**Mit tanulsz:** Hibatűrő rendszertervezés, Cache invalidation, Lazy vs Eager loading

---

### 3. Fázis: Lokális Kubernetes (Ingyenes, 1-2 hét)

Kubernetes koncepciókat **saját gépen** tanulni a legbiztonságosabb és legolcsóbb.

**Ajánlott eszköz: `k3d`** (k3s Dockerben — 5 perc telepítés, azonos a "valódi" K8s-sel)

```bash
# Teljes lokális stack:
Podman/Docker + k3d + Helm + kubectl
```

| Eszköz | Előny |
|--------|-------|
| **k3d** | Leggyorsabb, CI/CD-be is illeszthető |
| **minikube** | Legismertebb, legtöbb tutorial |
| **Kind** | Könnyű, Docker-alapú |

**Feladat:** A meglévő `docker-compose.yml` átírása K8s manifestekké:

```
docker-compose.yml  →  Deployment + Service + ConfigMap + Secret YAML-ok
```

**Mit tanulsz K8s-ből (interjún kérdezik):**
- Deployment, Service, Ingress YAML írása
- ConfigMap és Secret kezelés
- Liveness / Readiness probe-ok
- HorizontalPodAutoscaler
- Helm chart készítés a saját szervizekhez
- Namespace-ek és RBAC alapok
- Ingress controller és belső DNS névfeloldás

---

### 4. Fázis: CI/CD + GitOps (1 hét)

A meglévő `.github/workflows/ci.yml` kibővítése teljes delivery pipeline-ra.

```
git push
    │
    ▼
GitHub Actions: build → teszt → Docker image build → push (Docker Hub / ECR)
    │
    ▼
ArgoCD / Flux: automatikus deploy → lokális k3d klaszterbe
```

**Mit tanulsz:** GitOps szemlélet, image tagging stratégiák, rollback, ArgoCD/FluxCD alapok

---

### 5. Fázis: Megfigyelhetőség — Observability (1 hét)

Mikroszervizek nélkül nem látod, mi történik a rendszerben.

```
Prometheus ──► Grafana        (metrikák, dashboardok)
Zipkin / Jaeger               (distributed tracing — kérés követése szervizeken át)
ELK Stack / Loki              (centralizált logok)
Spring Boot Actuator          (health endpoint, metrika export)
```

**Mindez Helm-mel telepíthető** a k3d klaszterbe, pár paranccsal.

---

### 6. Fázis: AWS Cloud + Terraform véglegesítés (1-2 hét)

A meglévő `Terraform/main.tf` kibővítése:

```hcl
# Ami már van → kibővítjük:
VPC + Subnetek + Security Groups
EC2 (k3s-szel) vagy EKS
Amazon RDS (Postgres helyett managed)
ECR (Docker image tárolás)
```

---

## Ingyenes és Olcsó Cloud Lehetőségek

### Az EKS csapda
Az AWS EKS **~$73/hó** csak a control plane-ért — tanuláshoz indokolatlanul drága.

### Ajánlott tanulási sorrend:

**1. LocalStack — AWS szimulátor lokálisan (teljesen ingyenes)**
```bash
docker run -d localstack/localstack
```
Szimulál: S3, SQS, SNS, DynamoDB, Lambda, Secrets Manager — és **ugyanaz a Terraform kód** fut rajta, ami majd az éles AWS-en.

**2. K3s EC2-n — ingyenes AWS K8s élmény**
- AWS Free Tier: 12 hónapig 750 óra/hó `t2.micro` / `t3.micro` EC2
- Erre telepítve a **K3s** (Rancher könnyűsúlyú K8s) = valódi K8s, $0 control plane költséggel
- Megtanulod: VPC, IAM szerepkörök, Security Groups, AWS hálózatkezelés

**3. EKS "próbajárat" — ha CV-be kell**
```bash
terraform apply   # klaszter felépítés → gyakorlás
terraform destroy # mindent töröl → ~$0.20/óra a tényleges cost
```

**4. Olcsó managed K8s alternatívák AWS helyett:**

| Platform | Ár | Mire jó |
|----------|-----|---------|
| **Civo** | ~$5/hó | K3s alapú, legolcsóbb managed K8s |
| **DigitalOcean DOKS** | ~$12/hó | Egyszerű, jó dokumentáció |
| **Linode/Akamai LKE** | ~$12/hó | Megbízható, olcsó |
| **Google GKE Autopilot** | Pay-per-pod | Csak a futó podokért fizetsz |
| **AWS EKS** | $70+/hó | Drága, de CV-értékes |

---

## Konkrét Lépések — Javasolt Ütemterv

```
[Most]       k3d telepítés + docker-compose.yml → K8s YAML átírás
[1. hét]     NotificationService kiemelése (Kafka consumer → önálló Spring Boot app)
[2. hét]     AuthService kiemelése a DatabaseServer-ből (AuthController → külön szerviz)
[3. hét]     API Gateway bevezetése (Spring Cloud Gateway vagy Nginx)
[4. hét]     Helm chart minden szervizhez + Prometheus/Grafana telepítés
[5. hét]     Resilience4j Circuit Breaker + Redis cache bevezetése
[6. hét]     LocalStack + Terraform: S3, SQS, RDS szimulálva lokálisan
[7. hét]     GitHub Actions kibővítése: build → image push → k3d auto-deploy
[8. hét]     K3s feltelepítése AWS Free Tier EC2-ra Terraformmal
[Végén]      1-2 napra éles EKS deploy (terraform apply → screenshot → terraform destroy)
```

---

## Mi hiányzik — amit érdemes később megtanulni

- **Service Mesh** (Istio / Linkerd) — haladó, de CV-értékes svájci piacra
- **Secrets kezelés** (AWS Secrets Manager / HashiCorp Vault)
- **Multi-environment** Terraform (dev / staging / prod workspace-ek)
- **Német nyelvtanulás** — svájci piacon komoly versenyelőny

---

## Összefoglalás

**Legjobb stratégia:** Strangler Fig Pattern + lokális k3d + LocalStack kombináció.  
**Nullás költségen** megtanulod az összes kulcstechnológiát.  
**CV-hez** elég 1-2 nap éles EKS deploy, amit Terraformmal azonnal le is törölsz.

A projekt már most kiváló alapokon áll (Kafka, CI/CD, Terraform) — csak a lépések sorrendjét kell tartani.
