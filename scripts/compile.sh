#!/usr/bin/env bash
set -e
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "=========================================="
echo "  COMPILANDO FITMANAGE PRO (JAVA 21)     "
echo "=========================================="
mkdir -p bin data
rm -rf bin/*

SOURCES=$(find src -name "*.java")
if [ -z "$SOURCES" ]; then
    echo ">> [ERROR] No se encontraron archivos .java en src/"
    exit 1
fi

javac -d bin -encoding UTF-8 $SOURCES
echo ">> Compilación finalizada exitosamente en bin/."
