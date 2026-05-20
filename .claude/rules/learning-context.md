---
trigger: always_on
---

# Learning Context

## Purpose
This is a **learning project**. The goal is to reach Medior Java Developer level,
with focus on the Swiss and Hungarian job market requirements.

## Teaching approach
- For every non-trivial change, briefly explain **what** was done and **why**
- Connect explanations to real-world patterns (e.g. "this is the Strangler Fig pattern because...")
- Point out what this skill is called in interviews and job postings

## Architecture direction
- Follow the **Strangler Fig Pattern** for microservice extraction
- The current roadmap is in `microservices_roadmap.md` — use it as the north star
- Current phase: extracting services from the two monoliths (DatabaseServer, UserInputServer)
- Kafka is already running — prefer event-driven communication between new services

## Project structure awareness
- `DatabaseServer` — Spring Boot backend, contains `AuthController` and `PersonController`
- `UserInputServer` — React frontend + Node BFF
- `Terraform/` — existing IaC, to be extended for AWS
- `.github/workflows/` — existing CI/CD, to be extended
