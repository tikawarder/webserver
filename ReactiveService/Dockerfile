FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/target/*.jar /app.jar

# r2dbc-postgresql 1.0.7 (reactor-netty 1.2.1 / netty-resolver 4.1.116) fails to
# resolve the DB hostname: RoundRobinInetAddressResolver passes "host:port" as a
# single string into InetAddress.getAllByName(), which always throws
# UnknownHostException — connecting via a raw IP works fine, so this is a bug in
# that resolver chain, not a DNS-readiness race. Workaround: resolve the hostname
# ourselves with getent (proven reliable) and rewrite SPRING_R2DBC_URL to use the
# resolved IP before the JVM starts, sidestepping Netty's resolver entirely.
ENTRYPOINT ["sh", "-c", "\
  host=$(echo \"$SPRING_R2DBC_URL\" | sed -E 's#.*://([^:/]+).*#\\1#'); \
  ip=''; \
  until [ -n \"$ip\" ]; do ip=$(getent hosts \"$host\" | awk '{print $1}'); [ -n \"$ip\" ] || { echo \"waiting for $host DNS...\"; sleep 1; }; done; \
  export SPRING_R2DBC_URL=$(echo \"$SPRING_R2DBC_URL\" | sed \"s#://$host#://$ip#\"); \
  echo \"Resolved $host -> $ip, using $SPRING_R2DBC_URL\"; \
  exec java -jar /app.jar"]
