FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/springboot-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV FLASK_BASE_URL=http://flask:5001

ENTRYPOINT ["java", "-jar", "app.jar"]
