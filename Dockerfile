FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/deal-0.0.1-SNAPSHOT.jar /app/deal.jar
ENTRYPOINT ["java", "-jar", "/app/deal.jar"]