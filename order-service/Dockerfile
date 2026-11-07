FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY order-service/pom.xml .
COPY order-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY certificates/kafka-truststore.jks /app/certificates/kafka-truststore.jks


EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
