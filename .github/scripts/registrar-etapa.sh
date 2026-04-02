#!/usr/bin/env bash
set -euo pipefail
mensagem="${1:-Etapa sem descricao}"
echo "::group::${mensagem}"
echo "[PDV-PIPELINE] ${mensagem}"
echo "::endgroup::"
