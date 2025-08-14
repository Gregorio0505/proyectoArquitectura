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
docker compose -f "$FILE" down --remove-orphans || true
# Si usas registry (imágenes en un repo remoto), descomenta esta línea:
# docker compose -f "$FILE" pull || true
docker compose -f "$FILE" up -d --build
docker image prune -f || true
echo "=== Deploy ${ENV_NAME} OK ==="
