# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -Dnet.bytebuddy.experimental=true

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from builder stage
COPY --from=builder /build/target/portal-0.0.1-SNAPSHOT.jar app.jar

# Copy key files
COPY privatekey.pem /app/privatekey.pem
COPY publickey.pem /app/publickey.pem

# Expose port 8080 for public access
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
