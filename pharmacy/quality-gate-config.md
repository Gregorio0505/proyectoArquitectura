# Configuración del Quality Gate para Pharmacy Backend

## Objetivos de Calidad

### 1. Cobertura de Código
- **Mínimo requerido**: 80% de cobertura de líneas
- **Mínimo requerido**: 70% de cobertura de branches
- **Excluir**: DTOs, modelos, excepciones, configuraciones e integraciones

### 2. Tests Unitarios
- **Mínimo requerido**: 100% de tests pasando
- **Bloquear merge**: Si fallan los tests
- **Cobertura de tests**: Mínimo 80% en servicios de negocio

### 3. Deuda Técnica
- **Máximo permitido**: 0 días de deuda técnica nueva
- **Bloquear merge**: Si se introduce nueva deuda técnica

### 4. Duplicación de Código
- **Máximo permitido**: 3% de código duplicado
- **Tokens mínimos**: 100 para considerar duplicación

### 5. Vulnerabilidades de Seguridad
- **Críticas**: 0 permitidas
- **Altas**: 0 permitidas
- **Bloquear merge**: Si se detectan vulnerabilidades críticas o altas

### 6. Code Smells
- **Máximo permitido**: 100 code smells
- **Bloquear merge**: Si se excede el límite

## Configuración del Quality Gate

### Criterios de Éxito (PASS)
- ✅ Cobertura de líneas ≥ 80%
- ✅ Cobertura de branches ≥ 70%
- ✅ Tests pasando 100%
- ✅ Deuda técnica nueva = 0 días
- ✅ Código duplicado ≤ 3%
- ✅ Vulnerabilidades críticas = 0
- ✅ Vulnerabilidades altas = 0
- ✅ Code smells ≤ 100

### Criterios de Fallo (FAIL)
- ❌ Cobertura de líneas < 80%
- ❌ Cobertura de branches < 70%
- ❌ Tests fallando
- ❌ Deuda técnica nueva > 0 días
- ❌ Código duplicado > 3%
- ❌ Vulnerabilidades críticas > 0
- ❌ Vulnerabilidades altas > 0
- ❌ Code smells > 100

## Configuración en SonarQube

### 1. Crear Quality Gate
```bash
# En SonarQube Admin > Quality Gates
# Crear nuevo gate: "Pharmacy Backend Quality Gate"
```

### 2. Configurar Métricas
- **Reliability**: Bugs = 0
- **Security**: Vulnerabilities = 0
- **Maintainability**: Code Smells ≤ 100
- **Coverage**: Line Coverage ≥ 80%
- **Coverage**: Branch Coverage ≥ 70%
- **Duplications**: Duplicated Lines ≤ 3%

### 3. Asignar al Proyecto
- Proyecto: `pharmacy-backend`
- Quality Gate: `Pharmacy Backend Quality Gate`

## Exclusiones de Cobertura

### Archivos Excluidos
- `**/dto/**` - DTOs de transferencia
- `**/model/**` - Modelos de datos
- `**/exception/**` - Clases de excepción
- `**/config/**` - Configuraciones
- `**/integration/**` - Clientes de integración
- `**/util/**` - Utilidades
- `**/PharmacyApplication.java` - Clase principal

### Razón de Exclusión
- **DTOs/Models**: No contienen lógica de negocio
- **Exceptions**: Solo manejo de errores
- **Config**: Configuración del framework
- **Integration**: Clientes externos
- **Util**: Funciones auxiliares

## Comandos de Ejecución

### 1. Generar Cobertura
```bash
mvn clean test
```

### 2. Ejecutar SonarQube
```bash
./run-sonar.sh
```

### 3. Verificar Quality Gate
```bash
# En SonarQube: http://localhost:9000
# Proyecto: pharmacy-backend
# Quality Gate: Pharmacy Backend Quality Gate
```

## Integración con CI/CD

### Jenkins Pipeline
```groovy
stage('SonarQube Analysis') {
    steps {
        script {
            // Ejecutar análisis
            sh './run-sonar.sh'
            
            // Esperar resultado del Quality Gate
            timeout(time: 1, unit: 'HOURS') {
                def qg = waitForQualityGate()
                if (qg.status != 'OK') {
                    error "Quality Gate failed: ${qg.status}"
                }
            }
        }
    }
}
```

### GitHub Actions
```yaml
- name: SonarQube Analysis
  run: |
    ./run-sonar.sh
    # Verificar Quality Gate
    # Bloquear merge si falla
```
