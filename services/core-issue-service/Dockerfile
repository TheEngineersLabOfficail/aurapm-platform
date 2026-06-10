# ==========================================
# STAGE 1: Build & Compile (The Heavy Lifting)
# ==========================================
# Using an official ARM64-compatible Temurin JDK image layered on a tiny Alpine Linux footprint
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy only the pom.xml first to cache dependencies (Speed optimization for subsequent builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code
COPY src ./src

# Compile and package the application into a production-ready fat JAR file
# skipping unit tests to accelerate the build time
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Runtime Environment (The Lean Machine)
# ==========================================
# Discarding the heavy Maven build environment and JDK, using only a lightweight JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the compiled executable artifact from Stage 1 into our clean runtime container
COPY --from=build /app/target/core-issue-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8081 out of the container filesystem
EXPOSE 8081

# Optimize JVM memory allocation rules for container execution limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]