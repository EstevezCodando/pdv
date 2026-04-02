#!/usr/bin/env bash
set -euo pipefail
url="${1:-http://127.0.0.1:8080/actuator/health}"
tentativas="${2:-30}"
intervalo="${3:-5}"
for tentativa in $(seq 1 "$tentativas"); do
  echo "[PDV-PIPELINE] aguardando aplicacao: tentativa ${tentativa}/${tentativas} url=${url}"
  if curl --fail --silent --show-error "$url" > /tmp/pdv-health.json; then
    cat /tmp/pdv-health.json
    exit 0
  fi
  sleep "$intervalo"
done
echo "[PDV-PIPELINE] aplicacao nao ficou saudavel dentro do tempo esperado" >&2
exit 1
