#!/usr/bin/env bash
set -e
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

bash scripts/compile.sh
echo ">> Ejecutando Suite de Pruebas Lógicas..."
java -cp bin com.clubdeportivo.test.SistemaTest "$@"
