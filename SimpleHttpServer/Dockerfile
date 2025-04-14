# Base image
FROM openjdk:17

# Create app directory
WORKDIR /app

# Copy Java file and compile
COPY src/SimpleHttpServer.java .

RUN javac SimpleHttpServer.java

# Run the compiled server
CMD ["java", "SimpleHttpServer"]