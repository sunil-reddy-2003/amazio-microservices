FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY product-service/pom.xml .
COPY product-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]