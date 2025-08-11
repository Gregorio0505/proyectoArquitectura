# 🚀 Configuración de SonarQube para Pharmacy Frontend (Angular)

## ✅ Estado Actual

### 1. **Archivos de Configuración Creados**
- ✅ `sonar-project.properties` - Configuración del proyecto
- ✅ `run-sonar.sh` - Script de ejecución automatizada
- ✅ `quality-gate-config.md` - Documentación del Quality Gate

### 2. **Configuración Aplicada**
- ✅ **Análisis estático** (sin tests unitarios)
- ✅ **Exclusiones inteligentes** para archivos no relevantes
- ✅ **Focus en calidad** del código TypeScript/JavaScript
- ✅ **Script automatizado** para build y análisis

### 3. **Exclusiones Configuradas**
- `**/dist/**` - Directorio de build
- `**/node_modules/**` - Dependencias
- `**/.angular/**` - Cache de Angular
- `**/coverage/**` - Reportes de cobertura
- `**/e2e/**` - Tests end-to-end
- `**/cypress/**` - Tests de Cypress
- `**/assets/**` - Recursos estáticos
- `**/environments/**` - Configuraciones

## 🎯 Próximos Pasos

### **Paso 1: Verificar Dependencias**
```bash
# Verificar Node.js y npm
node --version
npm --version

# Instalar Angular CLI si no está
npm install -g @angular/cli

# Instalar sonar-scanner si no está
npm install -g sonarqube-scanner
```

### **Paso 2: Configurar SonarQube**
```bash
# 1. Iniciar SonarQube
docker-compose up -d sonarqube

# 2. Esperar que esté listo (http://localhost:9000)
# 3. Login: admin/admin
# 4. Crear proyecto: pharmacy-frontend-dev
```

### **Paso 3: Crear Quality Gate**
```bash
# En SonarQube Admin > Quality Gates
# Nombre: "Pharmacy Frontend Quality Gate"
# Métricas:
# - Bugs = 0 (críticos y altos)
# - Vulnerabilities = 0 (críticas y altas)
# - Code Smells ≤ 200
# - Duplicated Lines ≤ 5%
```

### **Paso 4: Ejecutar Análisis**
```bash
# Desde el directorio frontend/
./run-sonar.sh
```

### **Paso 5: Verificar Quality Gate**
```bash
# En SonarQube: http://localhost:9000
# Proyecto: pharmacy-frontend-dev
# Verificar que pase el Quality Gate
```

## 📊 Métricas de Calidad Objetivo

| Métrica | Objetivo | Estado |
|---------|----------|--------|
| **Bugs Críticos** | 0 | ⚠️ Por verificar |
| **Bugs Altos** | 0 | ⚠️ Por verificar |
| **Vulnerabilidades Críticas** | 0 | ⚠️ Por verificar |
| **Vulnerabilidades Altas** | 0 | ⚠️ Por verificar |
| **Code Smells** | ≤ 200 | ⚠️ Por verificar |
| **Código Duplicado** | ≤ 5% | ⚠️ Por verificar |
| **Deuda Técnica Nueva** | 0 días | ⚠️ Por verificar |
| **Build Angular** | ✅ Exitoso | ⚠️ Por verificar |

## 🔧 Configuración Técnica

### **sonar-project.properties**
```properties
sonar.projectKey=pharmacy-frontend-dev
sonar.projectName=pharmacy-frontend-dev
sonar.projectVersion=1.0.1

sonar.sources=src
sonar.exclusions=**/dist/**,**/node_modules/**,**/.angular/**,**/coverage/**

# No cobertura (sin tests)
# sonar.tests=
# sonar.test.inclusions=
```

### **Script de Ejecución**
```bash
#!/bin/bash
# run-sonar.sh
# - Verifica dependencias
# - Instala dependencias (npm ci)
# - Construye proyecto (ng build)
# - Ejecuta SonarQube
# - Maneja credenciales
```

## 🚨 Solución de Problemas

### **Error: Angular CLI No Instalado**
```bash
npm install -g @angular/cli
```

### **Error: SonarQube Scanner No Instalado**
```bash
npm install -g sonarqube-scanner
```

### **Error: Build Falla**
```bash
# Limpiar e instalar
rm -rf node_modules package-lock.json .angular
npm ci
npm run build -- --configuration=production
```

### **Error: SonarQube No Responde**
```bash
# Verificar estado
curl http://localhost:9000/api/system/status

# Reiniciar si es necesario
docker-compose restart sonarqube
```

## 📈 Mejoras de Calidad Esperadas

### **Code Smells Comunes en Angular**
- **Componentes muy grandes**: Dividir en componentes más pequeños
- **Servicios con muchas responsabilidades**: Aplicar Single Responsibility
- **Duplicación de lógica**: Extraer a servicios compartidos
- **Imports no utilizados**: Limpiar imports innecesarios

### **Buenas Prácticas a Implementar**
- **Lazy Loading**: Cargar módulos bajo demanda
- **OnPush Strategy**: Usar ChangeDetectionStrategy.OnPush
- **TrackBy Functions**: Optimizar *ngFor con trackBy
- **Async Pipe**: Usar async pipe en lugar de subscribe manual

## 🔄 Integración con CI/CD

### **Jenkins Pipeline**
```groovy
stage('Frontend SonarQube Analysis') {
    steps {
        script {
            dir('frontend') {
                sh 'npm ci'
                sh 'npm run build -- --configuration=production'
                sh './run-sonar.sh'
                
                timeout(time: 1, unit: 'HOURS') {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Frontend Quality Gate failed: ${qg.status}"
                    }
                }
            }
        }
    }
}
```

### **GitHub Actions**
```yaml
- name: Frontend SonarQube Analysis
  run: |
    cd frontend
    npm ci
    npm run build -- --configuration=production
    ./run-sonar.sh
```

## 🎉 Resultado Esperado

### **En SonarQube**
- ✅ Proyecto `pharmacy-frontend-dev` creado
- ✅ Métricas de calidad disponibles
- ✅ **NO** métricas de cobertura (esperado)
- ✅ Quality Gate configurado
- ✅ Análisis estático funcionando

### **Métricas Disponibles**
- 🐛 **Bugs**: Cantidad y severidad
- 🔒 **Vulnerabilidades**: Problemas de seguridad
- 🧹 **Code Smells**: Problemas de mantenibilidad
- 📋 **Duplicaciones**: Código duplicado
- ⏱️ **Deuda Técnica**: Tiempo para arreglar problemas

## 🔍 Verificación del Análisis

### **1. Proyecto Creado**
- Buscar `pharmacy-frontend-dev` en SonarQube
- Verificar que aparezca en la lista de proyectos

### **2. Métricas Generadas**
- **Reliability**: Bugs encontrados
- **Security**: Vulnerabilidades detectadas
- **Maintainability**: Code smells identificados
- **Duplications**: Código duplicado

### **3. Quality Gate**
- Verificar que esté asignado al proyecto
- Comprobar que las métricas estén configuradas
- Ejecutar análisis y verificar resultado

## 📚 Recursos Adicionales

- [Documentación de SonarQube](https://docs.sonarqube.org/)
- [Angular Style Guide](https://angular.io/guide/styleguide)
- [TypeScript Best Practices](https://www.typescriptlang.org/docs/)
- [SonarQube Scanner](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner/)

---

## 🚀 ¡Frontend Configurado para SonarQube!

El frontend de Pharmacy está listo para análisis estático con SonarQube.

**Próximo paso**: Configurar Jenkins para automatizar el análisis de ambos proyectos (backend + frontend).

