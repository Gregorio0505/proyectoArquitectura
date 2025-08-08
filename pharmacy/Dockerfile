FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
# El perfil de Spring lo manejas con SPRING_PROFILES_ACTIVE
ENTRYPOINT ["java", "-jar", "app.jar"]
