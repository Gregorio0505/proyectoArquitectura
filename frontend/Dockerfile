# Etapa 1: Build de Angular
FROM node:18-alpine as build

WORKDIR /app
COPY . .
RUN npm install
RUN npm run build --prod

# Etapa 2: Servir la app con NGINX
FROM nginx:alpine
COPY --from=build /app/dist/frontend /usr/share/nginx/html

# Exponer el puerto 80
EXPOSE 80
