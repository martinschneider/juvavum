# Multi-stage build for Spring Boot application
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy halodb project and build it first
COPY halodb ./halodb
WORKDIR /app/halodb
RUN mvn clean install -DskipTests

# Go back to app directory for main project
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-alpine

# Install su-exec for dropping privileges
RUN apk add --no-cache su-exec

# Create app directory and user
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy entrypoint script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Create directories for certificates and logs
RUN mkdir -p /app/certs /app/logs && \
    chown -R spring:spring /app

# Expose HTTPS port only
EXPOSE 443

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider https://localhost:443/actuator/health --no-check-certificate || exit 1

# Run the application with Docker profile
ENTRYPOINT ["docker-entrypoint.sh"]
