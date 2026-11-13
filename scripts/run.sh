#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

if ! command -v java >/dev/null 2>&1; then
    echo ">> [ERROR] No se encontró 'java' en el PATH. Se requiere JRE/JDK 21 o superior." >&2
    exit 1
fi

if [ "$(uname -s)" = "Linux" ]; then
    if [ -z "${DISPLAY:-}" ] && [ -z "${WAYLAND_DISPLAY:-}" ]; then
        echo ">> [ERROR] No hay servidor gráfico X11/Wayland activo (DISPLAY y WAYLAND_DISPLAY están vacíos)." >&2
        exit 1
    fi
fi

bash scripts/compile.sh

echo ">> Iniciando Interfaz Gráfica FitManage Pro..."
java -cp bin com.clubdeportivo.gui.MainFrame "$@"
