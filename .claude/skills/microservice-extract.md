# Skill: Extract a Microservice from the Monolith

## Usage
`/microservice-extract [ServiceName]`

Example: `/microservice-extract AuthService`

## Steps

1. **Identify the domain in DatabaseServer**
   - Locate the relevant Controller, Service, Repository, and Entity classes
   - Map all dependencies — what does this domain need from other domains?
   - Identify which database tables belong to this domain

2. **Define the service boundary**
   - What HTTP endpoints will this service expose?
   - What Kafka events will it publish?
   - What Kafka events will it consume?
   - What data does it own exclusively?

3. **Scaffold the new Spring Boot project**
   - Create directory: `/home/me/Documents/cv/webserver/[ServiceName]/`
   - Generate `pom.xml` with required dependencies (Spring Boot, Spring Web, Kafka, JPA, etc.)
   - Create standard package structure: `controller`, `service`, `repository`, `model`, `event`
   - Copy and adapt relevant classes from DatabaseServer

4. **Define Kafka events**
   - Create event classes (e.g. `PersonCreatedEvent`, `AuthSuccessEvent`)
   - Add producer in the source service (DatabaseServer)
   - Add consumer in the new service

5. **Database separation**
   - Create a separate schema or datasource config for the new service
   - Add Flyway/Liquibase migration scripts

6. **Docker + Docker Compose**
   - Create `Dockerfile` for the new service
   - Add the new service to `docker-compose.yml`

7. **Kubernetes manifests**
   - Generate `Deployment`, `Service`, `ConfigMap`, `Secret` YAML files
   - Save to `/home/me/Documents/cv/webserver/k8s/[service-name]/`

8. **Helm chart skeleton**
   - Generate basic Helm chart structure under `/home/me/Documents/cv/webserver/helm/[service-name]/`

9. **Learning summary**
   - Explain what pattern was applied (Strangler Fig, Database per Service, etc.)
   - Point out what this is called in interviews
   - Reference the relevant phase in `microservices_roadmap.md`
