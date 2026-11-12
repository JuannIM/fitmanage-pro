#!/usr/bin/env bash
set -e
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

if [ ! -d "bin/com" ]; then
    bash scripts/compile.sh
fi

echo ">> Iniciando Interfaz Gráfica FitManage Pro..."
java -cp bin com.clubdeportivo.gui.MainFrame "$@"
