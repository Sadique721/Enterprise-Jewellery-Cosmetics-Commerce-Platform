# ═══════════════════════════════════════════════════════════════════════════
# SANAB Spring Boot Backend Engine — Multi-Stage Production Dockerfile (JDK 25)
# ═══════════════════════════════════════════════════════════════════════════

# Stage 1: Build & Package Reactor
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml files first for Docker layer caching
COPY pom.xml .
COPY sanab-shared/pom.xml sanab-shared/
COPY sanab-security/pom.xml sanab-security/
COPY sanab-identity/pom.xml sanab-identity/
COPY sanab-notifications/pom.xml sanab-notifications/
COPY sanab-catalog/pom.xml sanab-catalog/
COPY sanab-inventory/pom.xml sanab-inventory/
COPY sanab-customer/pom.xml sanab-customer/
COPY sanab-cart/pom.xml sanab-cart/
COPY sanab-orders/pom.xml sanab-orders/
COPY sanab-payments/pom.xml sanab-payments/
COPY sanab-shipping/pom.xml sanab-shipping/
COPY sanab-promotions/pom.xml sanab-promotions/
COPY sanab-reviews/pom.xml sanab-reviews/
COPY sanab-search/pom.xml sanab-search/
COPY sanab-cms/pom.xml sanab-cms/
COPY sanab-support/pom.xml sanab-support/
COPY sanab-analytics/pom.xml sanab-analytics/
COPY sanab-app/pom.xml sanab-app/

# Copy all source files
COPY . .

# Compile & package application executable jar
RUN mvn clean package -DskipTests -pl sanab-app -am

# Stage 2: Minimal Production JRE Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root system user for security hardening
RUN addgroup -S sanab && adduser -S sanab -G sanab
USER sanab:sanab

COPY --from=builder /app/sanab-app/target/sanab-app-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 1808

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
