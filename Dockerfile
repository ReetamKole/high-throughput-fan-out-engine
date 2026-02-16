# Use an official JDK 21 runtime as a parent image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src
COPY config ./config
COPY data ./data

# Install Maven and build the application
RUN apk add --no-cache maven
RUN mvn clean install -DskipTests

# Run the application with limited heap memory to prove 512MB constraint
ENTRYPOINT ["java", "-Xmx512m", "-cp", "target/high-throughput-fanout-1.0-SNAPSHOT.jar:target/lib/*", "com.engine.App"]