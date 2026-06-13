---
trigger: always_on
---

# Learning Context

## Purpose
This is a **learning project**. The goal is to reach Medior Java Developer level,
with focus on the Swiss and Hungarian job market requirements (2025/2026).

## Teaching approach
- After every important topic or milestone, stop and explain what was done and why — in Hungarian
- For every non-trivial change, briefly explain **what** was done and **why**
- Connect explanations to real-world patterns (e.g. "this is the Strangler Fig pattern because...")
- Point out what this skill is called in interviews and job postings

## Architecture direction
- The learning path follows the branch strategy documented in `docs/branches.md`
- Current active branch: `microservices` — full microservices stack with Kafka, Gateway, Resilience4j, Zipkin
- Next phases: PlaywrightE2E → kubernetes → ci-cd
- Follow the **Strangler Fig Pattern** for microservice extraction
- The current roadmap is in `microservices_roadmap.md` — use it as the north star
- Current phase: extracting services from the two monoliths (DatabaseServer, UserInputServer)
- Kafka is already running — prefer event-driven communication between new services

## Project structure
- `DatabaseServer` — Spring Boot backend (JPA, Security, JWT)
- `UserInputServer` — React frontend + Node BFF
- `AuthService` — JWT authentication microservice
- `GatewayService` — Spring Cloud Gateway
- `NotificationService` — Kafka consumer
- `Terraform/` — existing IaC, to be extended for AWS
- `.github/workflows/` — existing CI/CD, to be extended
