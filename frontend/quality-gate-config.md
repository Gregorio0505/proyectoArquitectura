# Configuración del Quality Gate para Pharmacy Frontend (Angular)

## 🎯 Objetivos de Calidad para Frontend

### **1. Análisis Estático (Sin Tests)**
- **No cobertura**: No se requieren tests unitarios
- **Focus**: Calidad del código, duplicaciones, vulnerabilidades
- **Bloquear merge**: Si se detectan problemas críticos

### **2. Bugs y Vulnerabilidades**
- **Bugs críticos**: 0 permitidos
- **Bugs altos**: 0 permitidos
- **Vulnerabilidades críticas**: 0 permitidas
- **Vulnerabilidades altas**: 0 permitidas
- **Bloquear merge**: Si se detectan problemas críticos

### **3. Code Smells**
- **Máximo permitido**: 200 code smells
- **Bloquear merge**: Si se excede el límite significativamente

### **4. Duplicación de Código**
- **Máximo permitido**: 5% de código duplicado
- **Tokens mínimos**: 100 para considerar duplicación
- **Bloquear merge**: Si se excede el límite

### **5. Deuda Técnica**
- **Máximo permitido**: 0 días de deuda técnica nueva
- **Bloquear merge**: Si se introduce nueva deuda técnica

## 🔧 Configuración del Quality Gate

### **Criterios de Éxito (PASS)**
- ✅ Bugs críticos = 0
- ✅ Bugs altos = 0
- ✅ Vulnerabilidades críticas = 0
- ✅ Vulnerabilidades altas = 0
- ✅ Code smells ≤ 200
- ✅ Código duplicado ≤ 5%
- ✅ Deuda técnica nueva = 0 días
- ✅ Build exitoso (Angular)

### **Criterios de Fallo (FAIL)**
- ❌ Bugs críticos > 0
- ❌ Bugs altos > 0
- ❌ Vulnerabilidades críticas > 0
- ❌ Vulnerabilidades altas > 0
- ❌ Code smells > 200
- ❌ Código duplicado > 5%
- ❌ Deuda técnica nueva > 0 días
- ❌ Build falla

## 📊 Métricas de Calidad

### **Métricas Principales**
| Métrica | Objetivo | Estado |
|---------|----------|--------|
| **Bugs** | 0 críticos, 0 altos | ⚠️ Por verificar |
| **Vulnerabilidades** | 0 críticas, 0 altas | ⚠️ Por verificar |
| **Code Smells** | ≤ 200 | ⚠️ Por verificar |
| **Duplicación** | ≤ 5% | ⚠️ Por verificar |
| **Deuda Técnica** | 0 días nueva | ⚠️ Por verificar |

### **Métricas NO Aplicables**
- ❌ **Cobertura de código**: No hay tests unitarios
- ❌ **Cobertura de branches**: No hay tests unitarios
- ❌ **Tests pasando**: No hay tests unitarios

## 🚀 Configuración en SonarQube

### **1. Crear Quality Gate**
```bash
# En SonarQube Admin > Quality Gates
# Crear nuevo gate: "Pharmacy Frontend Quality Gate"
```

### **2. Configurar Métricas**
- **Reliability**: Bugs = 0 (críticos y altos)
- **Security**: Vulnerabilities = 0 (críticas y altas)
- **Maintainability**: Code Smells ≤ 200
- **Duplications**: Duplicated Lines ≤ 5%

### **3. Asignar al Proyecto**
- Proyecto: `pharmacy-frontend-dev`
- Quality Gate: `Pharmacy Frontend Quality Gate`

## 🔍 Exclusiones de Análisis

### **Archivos Excluidos**
- `**/dist/**` - Directorio de build
- `**/node_modules/**` - Dependencias
- `**/.angular/**` - Cache de Angular
- `**/coverage/**` - Reportes de cobertura (no aplicable)
- `**/e2e/**` - Tests end-to-end
- `**/cypress/**` - Tests de Cypress
- `**/assets/**` - Recursos estáticos
- `**/environments/**` - Configuraciones de entorno
- `**/*.config.js` - Archivos de configuración
- `**/*.config.ts` - Archivos de configuración

### **Razón de Exclusión**
- **dist/node_modules**: No son código fuente
- **.angular**: Cache del framework
- **assets**: Recursos estáticos
- **environments**: Configuraciones
- **config files**: Archivos de configuración

## 📋 Comandos de Ejecución

### **1. Instalar Dependencias**
```bash
npm ci
```

### **2. Construir Proyecto**
```bash
npm run build -- --configuration=production
```

### **3. Ejecutar SonarQube**
```bash
./run-sonar.sh
```

### **4. Análisis Manual (Alternativo)**
```bash
sonar-scanner \
  -Dsonar.projectKey=pharmacy-frontend-dev \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=admin \
  -Dsonar.password=admin \
  -Dsonar.projectVersion=1.0.1
```

## 🔄 Integración con CI/CD

### **Jenkins Pipeline**
```groovy
stage('Frontend SonarQube Analysis') {
    steps {
        script {
            dir('frontend') {
                // Instalar dependencias
                sh 'npm ci'
                
                // Construir proyecto
                sh 'npm run build -- --configuration=production'
                
                // Análisis de SonarQube
                sh './run-sonar.sh'
                
                // Esperar resultado del Quality Gate
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
    # Verificar Quality Gate
    # Bloquear merge si falla
```

## 🚨 Solución de Problemas

### **Angular CLI No Instalado**
```bash
npm install -g @angular/cli
```

### **SonarQube Scanner No Instalado**
```bash
npm install -g sonarqube-scanner
```

### **Build Falla**
```bash
# Verificar dependencias
npm ci

# Limpiar cache
rm -rf node_modules package-lock.json .angular

# Reinstalar
npm ci
```

### **SonarQube No Responde**
```bash
# Verificar estado
curl http://localhost:9000/api/system/status

# Reiniciar si es necesario
docker-compose restart sonarqube
```

## 📈 Mejoras de Calidad

### **Code Smells Comunes en Angular**
- **Componentes muy grandes**: Dividir en componentes más pequeños
- **Servicios con muchas responsabilidades**: Aplicar Single Responsibility
- **Duplicación de lógica**: Extraer a servicios compartidos
- **Imports no utilizados**: Limpiar imports innecesarios

### **Buenas Prácticas**
- **Lazy Loading**: Cargar módulos bajo demanda
- **OnPush Strategy**: Usar ChangeDetectionStrategy.OnPush
- **TrackBy Functions**: Optimizar *ngFor con trackBy
- **Async Pipe**: Usar async pipe en lugar de subscribe manual

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

---

## 🚀 ¡Frontend Listo para SonarQube!

El frontend de Pharmacy está configurado para análisis estático con SonarQube.

**Próximo paso**: Configurar Jenkins para automatizar el análisis de ambos proyectos.

