FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY notification-service/pom.xml .
COPY notification-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY certificates/kafka-truststore.jks /app/certificates/kafka-truststore.jks


EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
