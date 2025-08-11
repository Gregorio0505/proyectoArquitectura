# Configuración de Jenkins para Proyecto Pharmacy

## 🔧 **Problema Identificado**

El error `No jenkins.plugins.nodejs.tools.NodeJSInstallation named NodeJS found` indica que la herramienta Node.js no está configurada correctamente en Jenkins.

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

## 🚀 **Opciones de Configuración**

### **Opción 1: Jenkinsfile con Verificación de Herramientas (Recomendado para Debug)**
```bash
# Usar el Jenkinsfile principal que verifica todas las herramientas
git add Jenkinsfile
git commit -m "Jenkinsfile con verificación de herramientas"
git push origin dev
```

**Ventajas**: 
- ✅ Detecta problemas específicos de configuración
- ✅ Muestra qué herramientas están disponibles
- ✅ Ideal para diagnosticar problemas

### **Opción 2: Jenkinsfile Simplificado (Recomendado para Producción)**
```bash
# Usar el Jenkinsfile que usa herramientas del sistema
cp Jenkinsfile.system Jenkinsfile
git add Jenkinsfile
git commit -m "Jenkinsfile simplificado usando herramientas del sistema"
git push origin dev
```

**Ventajas**:
- ✅ No depende de configuración de Jenkins Tools
- ✅ Usa herramientas del sistema directamente
- ✅ Más confiable y simple

### **Opción 3: Jenkinsfile Alternativo (Para casos específicos)**
```bash
# Usar el Jenkinsfile alternativo
cp Jenkinsfile.alternative Jenkinsfile
git add Jenkinsfile
git commit -m "Jenkinsfile alternativo"
git push origin dev
```

## 🔍 **Verificación de Configuración**

### **En Jenkins:**
1. Ir a **Manage Jenkins** → **Tools**
2. Verificar que todas las herramientas estén configuradas
3. Verificar que los nombres coincidan exactamente con los del Jenkinsfile

### **En el Pipeline:**
1. El stage "Verificar Herramientas Disponibles" debe mostrar todas las herramientas
2. El stage "Verificar Herramientas" debe mostrar todas las rutas correctamente
3. El stage "Verificar Java y Maven" debe ejecutar los comandos sin errores

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

### **Error: "No jenkins.plugins.nodejs.tools.NodeJSInstallation named NodeJS found"**
- **Causa**: La herramienta Node.js no está configurada o el nombre no coincide
- **Solución**: 
  1. Verificar que el plugin NodeJS esté instalado
  2. Verificar que la herramienta se llame exactamente `NodeJS`
  3. Usar Jenkinsfile simplificado como alternativa

### **Error: "Maven3.8.7 no está configurado en Jenkins"**
- **Causa**: El nombre de la herramienta no coincide
- **Solución**: Verificar que el nombre sea exactamente `Maven3.8.7`

### **Error: "JDK17 no está configurado en Jenkins"**
- **Causa**: El nombre de la herramienta no coincide
- **Solución**: Verificar que el nombre sea exactamente `JDK17`

### **Error: "null/bin/java: not found"**
- **Causa**: JAVA_HOME es null
- **Solución**: Configurar correctamente la herramienta JDK en Jenkins

## 🔄 **Flujo de Trabajo Recomendado**

### **Para Debug y Configuración:**
1. **Usar Jenkinsfile principal** con verificación de herramientas
2. **Ejecutar pipeline** y revisar el stage "Verificar Herramientas Disponibles"
3. **Configurar herramientas faltantes** basándose en los errores
4. **Repetir** hasta que todas las herramientas estén disponibles

### **Para Producción:**
1. **Usar Jenkinsfile simplificado** que no depende de Jenkins Tools
2. **Verificar** que las herramientas del sistema estén disponibles
3. **Ejecutar pipeline** y confirmar que funcione correctamente

## 📞 **Soporte**

Si continúas teniendo problemas:
1. **Usar Jenkinsfile simplificado** como solución inmediata
2. **Verificar** que todas las herramientas estén instaladas en el servidor Jenkins
3. **Verificar** que los plugins necesarios estén instalados
4. **Revisar** los logs de Jenkins para más detalles
5. **Usar** el stage de verificación de herramientas para diagnosticar problemas
