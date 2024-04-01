FROM maven:3.8.3-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/itrum.jar /app/itrum.jar


EXPOSE 8050
ENTRYPOINT ["java", "-jar", "/app/itrum.jar"]