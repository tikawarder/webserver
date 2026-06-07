---
trigger: always_on
---

# Learning Context

## Purpose
This is a **learning project**. The goal is to reach Medior Java Developer level,
with focus on the Swiss and Hungarian job market requirements.

## Teaching approach
- **Claude writes the code** — never leave TODOs for the user to implement. Always write the full, working implementation.
- Walk through the code **step by step**: explain each meaningful block before or after writing it
- Connect explanations to real-world patterns (e.g. "this is the Strangler Fig pattern because...")
- Point out what this skill is called in interviews and job postings
- After each implementation, state clearly: what concept this demonstrates, and how to explain it in an interview

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
