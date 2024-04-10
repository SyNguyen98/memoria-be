# Use the official OpenJDK 17 as base image
FROM adoptopenjdk/openjdk17:alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container
COPY target/my-spring-boot-app.jar /app/my-spring-boot-app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "my-spring-boot-app.jar"]