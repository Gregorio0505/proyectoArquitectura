# Configuración de Jenkins para Proyecto Pharmacy

## 🔧 **Problema Identificado**

El error `JAVA_HOME: null` indica que las herramientas de Jenkins no están configuradas correctamente.

## 📋 **Herramientas Requeridas en Jenkins**

### 1. **Java Development Kit (JDK) 17**
- **Nombre de la herramienta**: `JDK17`
- **Tipo**: `hudson.model.JDK`
- **Configuración**:
  - Ir a **Manage Jenkins** → **Tools**
  - Agregar **JDK**
  - **Name**: `JDK17`
  - **JAVA_HOME**: `/usr/lib/jvm/java-17-openjdk-amd64` (o la ruta donde esté instalado Java 17)

### 2. **Apache Maven 3.8.7**
- **Nombre de la herramienta**: `Maven3.8.7`
- **Tipo**: `hudson.tasks.Maven$MavenInstallation`
- **Configuración**:
  - Ir a **Manage Jenkins** → **Tools**
  - Agregar **Maven**
  - **Name**: `Maven3.8.7`
  - **MAVEN_HOME**: `/usr/share/maven` (o la ruta donde esté instalado Maven)
  - **Instalar automáticamente**: ✅ Marcado
  - **Versión**: `3.8.7`

### 3. **Node.js 23.7.0**
- **Nombre de la herramienta**: `NodeJS`
- **Tipo**: `jenkins.plugins.nodejs.tools.NodeJSInstallation`
- **Configuración**:
  - Instalar plugin **NodeJS Plugin** si no está disponible
  - Ir a **Manage Jenkins** → **Tools**
  - Agregar **NodeJS**
  - **Name**: `NodeJS`
  - **Version**: `NodeJS 23.7.0`
  - **Instalar automáticamente**: ✅ Marcado

### 4. **SonarQube Scanner**
- **Nombre de la herramienta**: `SonarScanner`
- **Tipo**: `hudson.plugins.sonar.SonarRunnerInstallation`
- **Configuración**:
  - Instalar plugin **SonarQube Scanner** si no está disponible
  - Ir a **Manage Jenkins** → **Tools**
  - Agregar **SonarQube Scanner**
  - **Name**: `SonarScanner`
  - **SONAR_RUNNER_HOME**: Ruta donde esté instalado SonarScanner

## 🚀 **Alternativas de Configuración**

### **Opción 1: Usar Jenkinsfile Original (Recomendado)**
```bash
# Usar el Jenkinsfile principal después de configurar las herramientas
git add Jenkinsfile
git commit -m "Jenkinsfile configurado con herramientas de Jenkins"
git push origin dev
```

### **Opción 2: Usar Jenkinsfile Simplificado**
```bash
# Usar el Jenkinsfile que usa herramientas del sistema
cp Jenkinsfile.simple Jenkinsfile
git add Jenkinsfile
git commit -m "Jenkinsfile simplificado usando herramientas del sistema"
git push origin dev
```

## 🔍 **Verificación de Configuración**

### **En Jenkins:**
1. Ir a **Manage Jenkins** → **Tools**
2. Verificar que todas las herramientas estén configuradas
3. Verificar que los nombres coincidan exactamente con los del Jenkinsfile

### **En el Pipeline:**
1. El stage "Verificar Herramientas" debe mostrar todas las rutas correctamente
2. El stage "Verificar Java y Maven" debe ejecutar los comandos sin errores

## 📝 **Comandos de Verificación en Jenkins**

```bash
# Verificar Java
${JAVA_HOME}/bin/java -version

# Verificar Maven
${MAVEN_HOME}/bin/mvn -version

# Verificar Node.js
${NODEJS_HOME}/bin/node --version

# Verificar npm
${NODEJS_HOME}/bin/npm --version
```

## ❌ **Errores Comunes y Soluciones**

### **Error: "No such property: JAVA_HOME"**
- **Causa**: La herramienta JDK17 no está configurada
- **Solución**: Configurar JDK17 en Jenkins Tools

### **Error: "Maven3.8.7 no está configurado en Jenkins"**
- **Causa**: El nombre de la herramienta no coincide
- **Solución**: Verificar que el nombre sea exactamente `Maven3.8.7`

### **Error: "NodeJS no está configurado en Jenkins"**
- **Causa**: El nombre de la herramienta no coincide
- **Solución**: Verificar que el nombre sea exactamente `NodeJS`

### **Error: "null/bin/java: not found"**
- **Causa**: JAVA_HOME es null
- **Solución**: Configurar correctamente la herramienta JDK en Jenkins

## 🔄 **Flujo de Trabajo Recomendado**

1. **Configurar todas las herramientas** en Jenkins Tools
2. **Verificar nombres exactos** de las herramientas
3. **Usar Jenkinsfile principal** con herramientas de Jenkins
4. **Ejecutar pipeline** y verificar que funcione
5. **Si hay problemas**, usar Jenkinsfile simplificado como fallback

## 📞 **Soporte**

Si continúas teniendo problemas:
1. Verificar que todas las herramientas estén instaladas en el servidor Jenkins
2. Verificar que los plugins necesarios estén instalados
3. Revisar los logs de Jenkins para más detalles
4. Usar el Jenkinsfile simplificado como alternativa temporal
