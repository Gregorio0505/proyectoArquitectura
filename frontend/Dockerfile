# Etapa 1: Build de Angular
FROM node:18-alpine as build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci --only=production

# Copiar el código fuente
COPY . .

# Argumento para la configuración (dev, qa, prod)
ARG CONFIG=production

# Construir la aplicación con la configuración especificada
RUN npm run build -- --configuration=$CONFIG

# Etapa 2: Servir la app con NGINX
FROM nginx:alpine

# Copiar los archivos construidos
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copiar configuración personalizada de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer el puerto 80
EXPOSE 80

# Comando por defecto
CMD ["nginx", "-g", "daemon off;"]