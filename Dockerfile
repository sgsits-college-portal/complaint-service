# ==========================================
# STAGE 1: Build the Application
# ==========================================
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies (speeds up future builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the actual source code and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Run the Application
# ==========================================
# Use a lightweight JRE alpine image for the final container to save memory
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy the built jar from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 10001

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
