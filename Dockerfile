
FROM openjdk:21-jdk-slim

LABEL maintainer="user-management-app"

WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8080

# Set environment variables with defaults
ENV EXTERNAL_API_URL=http://localhost:8080/external-api/data
ENV EXTERNAL_API_INTERVAL=60000

# Run the application
ENTRYPOINT ["java", "-jar", "target/user-management-app-0.0.1-SNAPSHOT.jar"]
