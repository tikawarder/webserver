---
trigger: always_on
---

# Learning Context

## Purpose
This is a **learning project**. The goal is to reach Medior Java Developer level,
with focus on the Swiss and Hungarian job market requirements (2025/2026).

## Teaching approach
- After every important topic or milestone, stop and explain what was done and why — in Hungarian
- Connect explanations to real-world patterns (e.g. "this is what Hibernate does internally because...")
- Point out what this skill is called in interviews and job postings

## Architecture direction
- The learning path follows the branch strategy documented in `docs/branches.md`
- Current active branch: `microservices` — full microservices stack with Kafka, Gateway, Resilience4j, Zipkin
- Next phases: PlaywrightE2E → kubernetes → ci-cd

## Project structure
- `DatabaseServer` — Spring Boot backend (JPA, Security, JWT)
- `UserInputServer` — React frontend + Node BFF
- `AuthService` — JWT authentication microservice
- `GatewayService` — Spring Cloud Gateway
- `NotificationService` — Kafka consumer
- `Terraform/` — IaC for GCP
