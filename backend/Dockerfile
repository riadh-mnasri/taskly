FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :application:bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/application/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]
