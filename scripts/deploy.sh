#!/usr/bin/env bash
set -euo pipefail

ENV_NAME="${1:-}"
if [[ -z "$ENV_NAME" ]]; then
  echo "Uso: $0 <dev|qa|prod>"
  exit 1
fi

FILE="docker-compose.${ENV_NAME}.yml"
if [[ ! -f "$FILE" ]]; then
  echo "No existe $FILE"
  exit 1
fi

echo "=== Deploy ${ENV_NAME} con ${FILE} ==="
echo "Deteniendo contenedores y removiendo orfanos..."
docker compose -f "$FILE" down --remove-orphans || true

echo "Limpiando imágenes y cache..."
docker system prune -f || true
docker image prune -f || true

echo "Reconstruyendo imágenes sin cache..."
docker compose -f "$FILE" build --no-cache

echo "Levantando servicios..."
docker compose -f "$FILE" up -d

echo "Limpiando imágenes no utilizadas..."
docker image prune -f || true
echo "=== Deploy ${ENV_NAME} OK ==="
