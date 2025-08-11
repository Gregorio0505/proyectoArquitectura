#!/bin/bash

# Script para ejecutar análisis de SonarQube en Pharmacy Backend
echo "🚀 Iniciando análisis de SonarQube para Pharmacy Backend..."

# Verificar que Maven esté instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Error: Maven no está instalado"
    exit 1
fi

# Verificar que SonarQube esté ejecutándose
echo "🔍 Verificando que SonarQube esté ejecutándose..."
if ! curl -s http://localhost:9000/api/system/status | grep -q "UP"; then
    echo "❌ Error: SonarQube no está ejecutándose en http://localhost:9000"
    echo "💡 Inicia SonarQube con: docker-compose up -d sonarqube"
    exit 1
fi

echo "✅ SonarQube está ejecutándose"

# Limpiar y compilar el proyecto
echo "🔨 Limpiando y compilando el proyecto..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Error en la compilación"
    exit 1
fi

# Ejecutar tests y generar reporte de cobertura
echo "🧪 Ejecutando tests y generando reporte de cobertura..."
mvn test

if [ $? -ne 0 ]; then
    echo "❌ Error en la ejecución de tests"
    exit 1
fi

# Ejecutar análisis de SonarQube
echo "📊 Ejecutando análisis de SonarQube..."
sonar-scanner

if [ $? -eq 0 ]; then
    echo "✅ Análisis de SonarQube completado exitosamente"
    echo "🌐 Abre http://localhost:9000 para ver los resultados"
    echo "🔑 Usuario: admin, Contraseña: admin"
else
    echo "❌ Error en el análisis de SonarQube"
    exit 1
fi
