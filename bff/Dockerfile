FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN apk update && apk upgrade --no-cache && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk update && apk upgrade --no-cache
RUN apk add --no-cache curl
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENV JAVA_TOOL_OPTIONS="-Dspring.profiles.active=docker"
ENTRYPOINT ["java", "-jar", "app.jar"]