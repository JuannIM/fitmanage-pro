#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

if ! command -v java >/dev/null 2>&1; then
    echo ">> [ERROR] No se encontró 'java' en el PATH. Se requiere JRE/JDK 21 o superior." >&2
    exit 1
fi

bash scripts/compile.sh
echo ">> Ejecutando Suite de Pruebas Lógicas..."
java -cp bin com.clubdeportivo.test.SistemaTest "$@"
