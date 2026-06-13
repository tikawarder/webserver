FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean verify

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]