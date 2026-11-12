#!/usr/bin/env python3
"""
Convertidor de guion_presentacion.md a HTML estilizado y compilación a PDF
utilizando Google Chrome Headless.
FitManage Pro - Cátedra POO 2026.
"""

import os
import sys
import subprocess
import markdown

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)

MD_PATH = os.path.join(PROJECT_ROOT, "guion_presentacion.md")
HTML_PATH = os.path.join(PROJECT_ROOT, "guion_presentacion.html")
PDF_PATH = os.path.join(PROJECT_ROOT, "guion_presentacion.pdf")

HTML_TEMPLATE = """<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>FitManage Pro - Guion de Exposición Oral y Defensa Académica</title>
<style>
  @import url('https://fonts.googleapis.com/css2?family=Merriweather:ital,wght@0,300;0,400;0,700;1,300;1,400&family=Plus+Jakarta+Sans:wght@500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap');

  @page {
    size: A4 portrait;
    margin: 1.6cm 1.5cm 1.8cm 1.5cm;
    @bottom-right {
      content: "FitManage Pro | POO 2026";
      font-size: 8pt;
      color: #64748b;
      font-family: 'Plus Jakarta Sans', sans-serif;
    }
  }

  * {
    box-sizing: border-box;
    -webkit-print-color-adjust: exact !important;
    print-color-adjust: exact !important;
  }

  body {
    font-family: 'Merriweather', Georgia, serif;
    font-size: 10pt;
    line-height: 1.6;
    color: #1e293b;
    background-color: #ffffff;
    margin: 0;
    padding: 0;
  }

  h1, h2, h3, h4 {
    font-family: 'Plus Jakarta Sans', system-ui, sans-serif;
    color: #0f172a;
    margin-top: 1.2em;
    margin-bottom: 0.5em;
    line-height: 1.25;
  }

  h1 {
    font-size: 18pt;
    font-weight: 800;
    color: #1e3a8a;
    border-bottom: 2.5px solid #2563eb;
    padding-bottom: 6px;
    margin-top: 0;
  }

  h2 {
    font-size: 13pt;
    font-weight: 700;
    color: #1e40af;
    background: #f1f5f9;
    border-left: 5px solid #2563eb;
    padding: 6px 12px;
    margin-top: 1.2em;
    border-radius: 0 4px 4px 0;
  }

  h3 {
    font-size: 11pt;
    font-weight: 700;
    color: #334155;
    margin-top: 1em;
  }

  p {
    margin-top: 0.4em;
    margin-bottom: 0.6em;
    text-align: justify;
  }

  blockquote {
    margin: 1em 0;
    padding: 10px 16px;
    background: #fefce8;
    border-left: 4px solid #f59e0b;
    border-radius: 4px;
    color: #78350f;
    font-size: 9.5pt;
    font-style: italic;
  }

  blockquote strong {
    color: #b45309;
    font-style: normal;
  }

  /* Acotaciones Escénicas */
  ul li em {
    color: #475569;
  }

  ul {
    margin: 0.4em 0 0.8em 1.4em;
    padding: 0;
  }

  li {
    margin-bottom: 0.3em;
  }

  /* Tablas de cronograma */
  table {
    width: 100%;
    border-collapse: collapse;
    margin: 1em 0;
    font-family: 'Plus Jakarta Sans', system-ui, sans-serif;
    font-size: 8.5pt;
  }

  th, td {
    padding: 6px 10px;
    border: 1px solid #cbd5e1;
    text-align: left;
  }

  th {
    background: #1e293b;
    color: #ffffff;
    font-weight: 600;
    text-transform: uppercase;
    font-size: 7.5pt;
    letter-spacing: 0.5px;
  }

  tr:nth-child(even) {
    background: #f8fafc;
  }

  /* Código */
  code {
    font-family: 'JetBrains Mono', Consolas, monospace;
    font-size: 8.5pt;
    background: #f1f5f9;
    color: #0f172a;
    padding: 2px 4px;
    border-radius: 3px;
    border: 1px solid #e2e8f0;
  }

  pre {
    background: #0f172a;
    color: #f8fafc;
    padding: 10px 14px;
    border-radius: 6px;
    overflow-x: auto;
    font-size: 8.5pt;
    line-height: 1.4;
    margin: 0.8em 0;
  }

  pre code {
    background: transparent;
    color: inherit;
    padding: 0;
    border: none;
  }

  hr {
    border: none;
    border-top: 1px solid #e2e8f0;
    margin: 1.2em 0;
  }

  /* Saltos de página por expositor */
  .page-break {
    page-break-before: always;
    break-before: page;
    height: 0;
    margin: 0;
    padding: 0;
  }

  /* Header y Footer impresos */
  .doc-header {
    border-bottom: 1px solid #cbd5e1;
    padding-bottom: 4px;
    margin-bottom: 12px;
    display: flex;
    justify-content: space-between;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 8pt;
    color: #64748b;
  }

  .doc-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    border-top: 1px solid #e2e8f0;
    padding-top: 4px;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 7.5pt;
    color: #94a3b8;
    text-align: center;
  }
</style>
</head>
<body>

<div class="doc-header">
  <span>FitManage Pro &bull; Sistema Integral de Gestión para Club Deportivo</span>
  <span>Cátedra POO 2026 &bull; Java 21 SE</span>
</div>

<div class="content">
__BODY_CONTENT__
</div>

</body>
</html>
"""

def generate_html():
    if not os.path.exists(MD_PATH):
        print(f"Error: No se encuentra {MD_PATH}")
        sys.exit(1)
        
    with open(MD_PATH, "r", encoding="utf-8") as f:
        md_text = f.read()

    # Extensiones de markdown útiles
    html_body = markdown.markdown(
        md_text,
        extensions=["tables", "fenced_code", "sane_lists"]
    )

    full_html = HTML_TEMPLATE.replace("__BODY_CONTENT__", html_body)

    with open(HTML_PATH, "w", encoding="utf-8") as f:
        f.write(full_html)
    print(f">> HTML generado exitosamente en: {HTML_PATH}")

def compile_pdf():
    cmd = [
        "google-chrome",
        "--headless=new",
        "--disable-gpu",
        "--no-sandbox",
        f"--print-to-pdf={PDF_PATH}",
        "--print-to-pdf-margins=0.4",
        "--no-pdf-header-footer",
        f"file://{HTML_PATH}"
    ]
    print(f">> Compilando PDF con Google Chrome Headless...")
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"Error al compilar PDF: {result.stderr}")
        sys.exit(result.returncode)

    if not os.path.exists(PDF_PATH):
        print("Error: El archivo PDF no fue creado.")
        sys.exit(1)

    pdf_size = os.path.getsize(PDF_PATH)
    print(f">> PDF generado con éxito en: {PDF_PATH} ({pdf_size / 1024:.1f} KB)")
    if pdf_size < 50 * 1024:
        print("Advertencia: El tamaño del PDF es menor a 50 KB.")
    else:
        print(">> [VALIDACIÓN APROBADA] El PDF supera el umbral de 50 KB.")

if __name__ == "__main__":
    generate_html()
    compile_pdf()
