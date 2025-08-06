# farmacias-db2adfs
GH Repo para farmacias, proyecto DB2/ADFS
Estos pasos están mucho más fáciles.
Mucha parte de la configuración ya está hecha.

## Instrucciones
1. clonan el repo
2. Ingresan al folder:
```
cd farmacias-db2adfs
```
3. A partir de acá, corren, en diferentes sesiones (pestañas o ventanas) de su terminal los comandos para backend y frontend:
**Backend**
```
cd pharmacy # osea que estarán en farmacias-db2adfs/pharmacy
mvn spring-boot:run
```

**Frontend**
```
# asegurense de estar en el root de farmacias, es decir, en farmacias-db2adfs/
cd frontend # osea que estarán en farmacias-db2adfs/frontend
ng serve
```

4. Consultar `http//localhost:4300`

Listo, ya tienen el sistema corriendo


#ARQUITECTURA DE SISTEMAS:

# Farmacias - Arquitectura de Sistemas

## Descripción

Proyecto de farmacias

## Estructura del Proyecto
/
├── db-scripts/ # Scripts SQL para inicializar y poblar la base de datos
├── frontend/ # Proyecto Angular (código fuente y Dockerfile)
├── pharmacy/ # Proyecto Java Spring Boot (código fuente y Dockerfile)
├── .gitignore
├── README.md
└── docker-compose.yml # (a crear después, para levantar todos los servicios)


---

## Ambientes

- **dev:** Desarrollo
- **qa:** Pruebas/QA
- **prod:** Producción

Cada ambiente tiene su propia configuración de base de datos y variables.

---

## ¿Cómo correr el proyecto localmente?

### Backend (Spring Boot)

```bash
cd pharmacy
./mvnw spring-boot:run --spring.profiles.active=dev

##Frontend
cd frontend
npm install
ng serve

## Cómo construir las imágenes Docker?

cd pharmacy
mvn clean package
docker build -t pharmacy-backend .

cd frontend
npm install
npm run build --prod
docker build -t pharmacy-frontend .

Variables de Entorno
Backend:

SPRING_PROFILES_ACTIVE (dev, qa, prod)

Configuración por ambiente:
/pharmacy/src/main/resources/application-dev.properties
/pharmacy/src/main/resources/application-qa.properties
/pharmacy/src/main/resources/application-prod.properties

Frontend:

Configuración por ambiente en:
/frontend/src/environments/

Scripts de Base de Datos
Los scripts para inicialización y datos de prueba están en /db-scripts/.