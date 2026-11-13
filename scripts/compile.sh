#!/usr/bin/env bash
set -euo pipefail
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

if ! command -v javac >/dev/null 2>&1; then
    echo ">> [ERROR] No se encontró 'javac' en el PATH. Se requiere JDK 21 o superior." >&2
    exit 1
fi

JAVAC_VER_RAW="$(javac -version 2>&1 | awk '{print $2}')"
JAVAC_MAJOR="${JAVAC_VER_RAW%%.*}"
JAVAC_MAJOR="${JAVAC_MAJOR%%-*}"
if ! [[ "$JAVAC_MAJOR" =~ ^[0-9]+$ ]] || [ "$JAVAC_MAJOR" -lt 21 ]; then
    echo ">> [ERROR] Se requiere JDK 21 o superior (versión detectada: ${JAVAC_VER_RAW:-desconocida})." >&2
    exit 1
fi

echo "=========================================="
echo "  COMPILANDO FITMANAGE PRO (JAVA 21)     "
echo "=========================================="
mkdir -p bin data
rm -rf bin/*

mapfile -t SOURCES < <(find src -name "*.java" | sort)
if [ "${#SOURCES[@]}" -eq 0 ]; then
    echo ">> [ERROR] No se encontraron archivos .java en src/" >&2
    exit 1
fi

javac --release 21 -Xlint:all -d bin -encoding UTF-8 "${SOURCES[@]}"
echo ">> Compilación finalizada exitosamente en bin/."
