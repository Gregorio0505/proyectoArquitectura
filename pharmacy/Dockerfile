# Use OpenJDK como base
FROM openjdk:17-jdk-alpine

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el .jar generado por Maven al contenedor
COPY target/*.jar app.jar

# Definir el comando que corre la app
ENTRYPOINT ["java", "-jar", "app.jar"]
