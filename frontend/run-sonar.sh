#!/bin/bash

# Script para ejecutar análisis de SonarQube en Pharmacy Frontend (Angular)
echo "🚀 Iniciando análisis de SonarQube para Pharmacy Frontend..."

# Verificar que Node.js esté instalado
if ! command -v node &> /dev/null; then
    echo "❌ Error: Node.js no está instalado"
    exit 1
fi

# Verificar que npm esté instalado
if ! command -v npm &> /dev/null; then
    echo "❌ Error: npm no está instalado"
    exit 1
fi

# Verificar que Angular CLI esté instalado
if ! command -v ng &> /dev/null; then
    echo "🔧 Instalando Angular CLI globalmente..."
    npm install -g @angular/cli
fi

# Verificar que SonarQube esté ejecutándose
echo "🔍 Verificando que SonarQube esté ejecutándose..."
if ! curl -s http://localhost:9000/api/system/status | grep -q "UP"; then
    echo "❌ Error: SonarQube no está ejecutándose en http://localhost:9000"
    echo "💡 Inicia SonarQube con: docker-compose up -d sonarqube"
    exit 1
fi

echo "✅ SonarQube está ejecutándose"

# Verificar que sonar-scanner esté instalado
if ! command -v sonar-scanner &> /dev/null; then
    echo "🔧 Instalando sonar-scanner..."
    npm install -g sonarqube-scanner
fi

# Limpiar instalaciones previas
echo "🧹 Limpiando instalaciones previas..."
rm -rf node_modules package-lock.json dist .angular

# Instalar dependencias
echo "📦 Instalando dependencias..."
npm ci

if [ $? -ne 0 ]; then
    echo "❌ Error en la instalación de dependencias"
    exit 1
fi

# Construir el proyecto
echo "🔨 Construyendo el proyecto Angular..."
npm run build -- --configuration=production

if [ $? -ne 0 ]; then
    echo "❌ Error en la construcción del proyecto"
    exit 1
fi

# Solicitar token de SonarQube
echo "🔑 Ingresa tu token de SonarQube (o presiona Enter para usar admin/admin):"
read -s SONAR_TOKEN

if [ -z "$SONAR_TOKEN" ]; then
    SONAR_TOKEN="admin"
    SONAR_PASSWORD="admin"
    echo "🔑 Usando credenciales por defecto: admin/admin"
else
    SONAR_PASSWORD=""
fi

# Ejecutar análisis de SonarQube
echo "📊 Ejecutando análisis de SonarQube..."

if [ "$SONAR_PASSWORD" = "admin" ]; then
    sonar-scanner \
        -Dsonar.projectKey=pharmacy-frontend-dev \
        -Dsonar.host.url=http://localhost:9000 \
        -Dsonar.login=admin \
        -Dsonar.password=admin \
        -Dsonar.projectVersion=1.0.1
else
    sonar-scanner \
        -Dsonar.projectKey=pharmacy-frontend-dev \
        -Dsonar.host.url=http://localhost:9000 \
        -Dsonar.login=$SONAR_TOKEN \
        -Dsonar.projectVersion=1.0.1
fi

if [ $? -eq 0 ]; then
    echo "✅ Análisis de SonarQube completado exitosamente"
    echo "🌐 Abre http://localhost:9000 para ver los resultados"
    echo "🔍 Busca el proyecto: pharmacy-frontend-dev"
    echo "📊 Métricas disponibles: Bugs, Vulnerabilidades, Code Smells, Duplicaciones"
    echo "⚠️  Nota: No hay métricas de cobertura (esperado para frontend sin tests)"
else
    echo "❌ Error en el análisis de SonarQube"
    exit 1
fi

