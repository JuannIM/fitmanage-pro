# Graph Report - java  (2026-11-13)

## Corpus Check
- 68 files · ~64,024 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 1 file(s) not represented in the graph (top: (none) 1)

## Summary
- 1001 nodes · 2738 edges · 47 communities (27 shown, 20 thin omitted)
- Extraction: 78% EXTRACTED · 22% INFERRED · 0% AMBIGUOUS · INFERRED: 591 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7dce0f9f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Rutina
- ClaseGrupal
- Pago
- SocioRepository
- render_video.py
- ClasesPanel.java
- Instructor
- Ejercicio
- Membresia
- Socio
- javax.swing.event.DocumentEvent
- Persona
- .registrarCheckIn
- DashboardPanel
- PLAN DE EJECUCIÓN: SISTEMA INTEGRAL DE GESTIÓN PARA CLUB DEPORTIVO / GIMNASIO (FITMANAGE PRO)
- Disciplina
- EstadoAcceso
- INFORME DE AUTO-AUDITORÍA Y VERIFICACIÓN DE FITMANAGE PRO
- AsistenciaRepository
- JsonParser
- AccesoService
- Reportable
- TipoAcceso
- .actualizarMetricas
- CheckInDialog
- MainFrame
- Facturable
- TipoMembresia
- 10. PROTOCOLO DE AUTO-AUDITORÍA Y PRUEBAS
- 2. DESCRIPCIÓN DETALLADA DEL DOMINIO Y REGLAS DE NEGOCIO
- Detalle de Fases y Criterios de Aceptación
- SocioService
- ClasesPanel
- .initUI
- CupoAgotadoException
- Autenticable
- 🏋️‍♂️ FITMANAGE PRO
- compile.sh
- run.sh
- test.sh
- .getNombreCompleto
- .getSociosInscriptos
- EstadoCuota
- 3. EXPLICACIÓN DETALLADA DE CADA ELEMENTO DEL MODELO
- 🚀 Cómo Ejecutar el Proyecto
- PersistenceException

## God Nodes (most connected - your core abstractions)
1. `Socio` - 67 edges
2. `ClaseGrupal` - 52 edges
3. `Instructor` - 52 edges
4. `Rutina` - 45 edges
5. `SocioRepository` - 42 edges
6. `SocioService` - 37 edges
7. `Pago` - 35 edges
8. `InstructorRepository` - 34 edges
9. `Ejercicio` - 32 edges
10. `ClaseRepository` - 32 edges

## Surprising Connections (you probably didn't know these)
- `2. MATRIZ DE CUMPLIMIENTO DE REQUISITOS OBLIGATORIOS` --references--> `SistemaTest`  [INFERRED]
  AUDIT.md → src/com/clubdeportivo/test/SistemaTest.java
- `4. Ejecutar la Suite de Pruebas Automatizadas` --references--> `SistemaTest`  [INFERRED]
  README.md → src/com/clubdeportivo/test/SistemaTest.java
- `6.1 Distribución de Integrantes y Módulos de Trabajo` --references--> `SistemaTest`  [INFERRED]
  PLAN.md → src/com/clubdeportivo/test/SistemaTest.java
- `👥 Equipo de Desarrollo` --references--> `SistemaTest`  [INFERRED]
  README.md → src/com/clubdeportivo/test/SistemaTest.java
- `🧠 Principios POO Implementados` --references--> `Autenticable`  [INFERRED]
  README.md → src/com/clubdeportivo/model/Autenticable.java

## Import Cycles
- None detected.

## Communities (47 total, 20 thin omitted)

### Community 0 - "Rutina"
Cohesion: 0.07
Nodes (5): RutinasPanel, Rutina, RutinaRepository, RutinaService, 3.5 Relación de Composición Fuerte (`*--`): `Rutina` y `Ejercicio`

### Community 2 - "Pago"
Cohesion: 0.05
Nodes (12): PagosPanel, DefaultTableModel, DocumentListener, SocioItem, MetodoPago, EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO (+4 more)

### Community 3 - "SocioRepository"
Cohesion: 0.08
Nodes (7): ClaseRepository, InstructorRepository, IRepository, SocioRepository, DataSeedService, IdGenerator, ValidationUtils

### Community 4 - "render_video.py"
Cohesion: 0.20
Nodes (14): generate_track(), draw_background(), draw_badge(), draw_card(), draw_global_chrome(), draw_rounded_rect(), draw_text(), get_scene_alpha() (+6 more)

### Community 5 - "ClasesPanel.java"
Cohesion: 0.05
Nodes (7): ModernButton, SearchTextField, FocusAdapter, StatusBadgeCellRenderer, TableFilterHelper, ThemeConfig, Formatters

### Community 6 - "Instructor"
Cohesion: 0.06
Nodes (6): InstructorItem, InstructoresPanel, InstructorItem, Instructor, InstructorService, 3.4 Subclase: `Instructor`

### Community 7 - "Ejercicio"
Cohesion: 0.07
Nodes (8): Ejercicio, GrupoMuscular, BRAZOS, CORE, ESPALDA, HOMBROS, PECHO, PIERNAS

### Community 10 - "javax.swing.event.DocumentEvent"
Cohesion: 0.15
Nodes (6): DefaultTableModel, DocumentListener, DefaultTableModel, DocumentListener, DefaultTableModel, DocumentListener

### Community 12 - ".registrarCheckIn"
Cohesion: 0.07
Nodes (4): 10.2 Suite de Pruebas Unitarias y de Integración (`SistemaTest.java`), RunnableTest, SistemaTest, 3.3 Subclase: `Socio`

### Community 14 - "PLAN DE EJECUCIÓN: SISTEMA INTEGRAL DE GESTIÓN PARA CLUB DEPORTIVO / GIMNASIO (FITMANAGE PRO)"
Cohesion: 0.12
Nodes (15): 1.1 Resumen Ejecutivo, 1.2 Ficha Técnica, 1. RESUMEN EJECUTIVO Y FICHA TÉCNICA DEL PROYECTO, 4. DIAGRAMA DE CLASES UML COMPLETO (FORMATO MERMAID), 5.1 Diagrama de Capas, 5.2 Estructura Física del Proyecto en el Árbol de Directorios, 5. ARQUITECTURA DEL SOFTWARE Y ESTRUCTURA DE PAQUETES, 7.1 Filosofía de Cero Dependencias Externas (+7 more)

### Community 15 - "Disciplina"
Cohesion: 0.11
Nodes (7): Disciplina, BOXEO, CROSSFIT, FUNCIONAL, PILATES, SPINNING, YOGA

### Community 16 - "EstadoAcceso"
Cohesion: 0.18
Nodes (6): EstadoAcceso, DENEGADO_CUOTA_VENCIDA, DENEGADO_MEMBRESIA_INSUFICIENTE, DENEGADO_NO_ENCONTRADO, DENEGADO_SIN_CUPO, PERMITIDO

### Community 17 - "INFORME DE AUTO-AUDITORÍA Y VERIFICACIÓN DE FITMANAGE PRO"
Cohesion: 0.15
Nodes (12): 1. RESUMEN EJECUTIVO Y ESTADO DE APROBACIÓN, 2. MATRIZ DE CUMPLIMIENTO DE REQUISITOS OBLIGATORIOS, 3.1 Análisis de los Casos de Prueba, 3. RESULTADOS DE LA EJECUCIÓN DE PRUEBAS (`scripts/test.sh`), 4.1 Métricas del Grafo de Código, 4.2 Nodos Nucleares ("God Nodes" / Abstracciones Principales), 4.3 Verificación de Desacoplamiento por Capas (Path Analysis), 4. AUDITORÍA ARQUITECTÓNICA CON GRAPHIFY (+4 more)

### Community 20 - "AccesoService"
Cohesion: 0.19
Nodes (3): DashboardPanel, DefaultTableModel, AccesoService

### Community 21 - "Reportable"
Cohesion: 0.22
Nodes (5): 6.1 Distribución de Integrantes y Módulos de Trabajo, 6.2 Convención de Comentarios y Trazabilidad Colaborativa, 6. SIMULACIÓN DE TRABAJO EN EQUIPO (6 INTEGRANTES), 🧠 Principios POO Implementados, Reportable

### Community 22 - "TipoAcceso"
Cohesion: 0.20
Nodes (3): TipoAcceso, CLASE_GRUPAL, GENERAL

### Community 25 - "MainFrame"
Cohesion: 0.10
Nodes (4): MouseAdapter, MainFrame, NavButton, java.awt.event.MouseAdapter

### Community 26 - "Facturable"
Cohesion: 0.15
Nodes (10): Facturable, 1. INTRODUCCIÓN Y PROPÓSITO DEL MODELADO, 2. DIAGRAMA DE CLASES UML COMPLETO (SINTAXIS MERMAID), 4. GUÍA DE DEFENSA ORAL ANTE LA CÁTEDRA (PREGUNTAS FRECUENTES), 5. TRAZABILIDAD DE LOS INTEGRANTES DEL GRUPO, DIAGRAMA DE CLASES UML Y DOCUMENTACIÓN TÉCNICA DEL MODELO POO, FitManage Pro - Sistema Integral de Gestión para Club Deportivo / Gimnasio, P2: ¿Cuál es la diferencia concreta entre la composición de `Rutina`/`Ejercicio` y la agregación de `ClaseGrupal`/`Socio`? (+2 more)

### Community 27 - "TipoMembresia"
Cohesion: 0.20
Nodes (5): TipoMembresia, CLASES_GRUPALES, MUSCULACION, PASE_LIBRE, VIP_TOTAL

### Community 28 - "10. PROTOCOLO DE AUTO-AUDITORÍA Y PRUEBAS"
Cohesion: 0.29
Nodes (7): 10.1 Scripts de Automatización, 10.3 Estrategia de Auditoría Arquitectónica con Graphify, 10.4 Checklist de Cumplimiento de Requisitos de la Cátedra, 10. PROTOCOLO DE AUTO-AUDITORÍA Y PRUEBAS, `scripts/compile.sh`, `scripts/run.sh`, `scripts/test.sh`

### Community 29 - "2. DESCRIPCIÓN DETALLADA DEL DOMINIO Y REGLAS DE NEGOCIO"
Cohesion: 0.29
Nodes (7): 2.1 Módulo de Socios y Estado de Cuenta, 2.2 Módulo de Instructores y Liquidación, 2.3 Módulo de Clases Grupales y Gestión de Cupos, 2.4 Módulo de Rutinas y Ejercicios (Composición Fuerte), 2.5 Módulo de Membresías, Facturación y Pagos, 2.6 Módulo de Asistencias y Control de Check-in, 2. DESCRIPCIÓN DETALLADA DEL DOMINIO Y REGLAS DE NEGOCIO

### Community 30 - "Detalle de Fases y Criterios de Aceptación"
Cohesion: 0.29
Nodes (7): Detalle de Fases y Criterios de Aceptación, Fase 1: Infraestructura, Scripts y Utilidades Base, Fase 2: Dominio Core POO, Interfaces y Enums (Integrantes 1, 2, 3, 4), Fase 3: Motor de Persistencia y Capa de Repositorios (Integrante 6), Fase 4: Lógica de Servicios y Carga de Datos Semilla (Integrantes 3, 4, 6), Fase 5: Construcción de la Interfaz Gráfica Swing (Integrantes 5 y 3), Fase 6: Testing Automatizado, Auditoría Graphify y Cierre (Todos)

### Community 35 - "Autenticable"
Cohesion: 0.22
Nodes (5): 3.1 Jerarquía de Herencia, 3.2 Interfaces y Contratos de Negocio, 3.3 Relaciones de Agregación, Composición y Asociación Justificadas, 3. MODELO DE PROGRAMACIÓN ORIENTADA A OBJETOS (POO), Autenticable

### Community 36 - "🏋️‍♂️ FITMANAGE PRO"
Cohesion: 0.15
Nodes (11): 🏗️ Arquitectura del Sistema (4 Capas), 📸 Demostración Visual, 📌 Descripción del Proyecto, 👥 Equipo de Desarrollo, 🏋️‍♂️ FITMANAGE PRO, 🖥️ Interfaz Gráfica de Escritorio (Java Swing), 🎬 Material de Presentación y Defensa, 📐 Modelo Estructural UML (20 Tipos en `model/` & Relaciones Cardinales) (+3 more)

### Community 42 - "EstadoCuota"
Cohesion: 0.20
Nodes (5): EstadoCuota, AL_DIA, INACTIVO, PENDIENTE, VENCIDO

### Community 43 - "3. EXPLICACIÓN DETALLADA DE CADA ELEMENTO DEL MODELO"
Cohesion: 0.29
Nodes (6): 3.1 Interfaces (Contratos de Negocio), 3.2 Jerarquía de Herencia: `Persona` (Clase Base Abstracta), 3.6 Relación de Agregación Débil (`o--`): `ClaseGrupal` con `Instructor` y `Socio`, 3.7 Relaciones de Asociación (`-->`), 3. EXPLICACIÓN DETALLADA DE CADA ELEMENTO DEL MODELO, P1: ¿Por qué usaron una clase abstracta `Persona` en lugar de una interfaz?

### Community 44 - "🚀 Cómo Ejecutar el Proyecto"
Cohesion: 0.33
Nodes (6): 1. Requisitos Previos, 2. Clonar el Repositorio, 3. Compilación Limpia, 4. Ejecutar la Suite de Pruebas Automatizadas, 5. Iniciar la Aplicación (Interfaz Gráfica), 🚀 Cómo Ejecutar el Proyecto

## Knowledge Gaps
- **31 isolated node(s):** `BOXEO`, `CROSSFIT`, `FUNCIONAL`, `PILATES`, `SPINNING` (+26 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 266 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **20 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Socio` connect `Socio` to `ClasesPanel`, `ClaseGrupal`, `Pago`, `Autenticable`, `Rutina`, `ClasesPanel.java`, `SocioRepository`, `.getNombreCompleto`, `.getSociosInscriptos`, `EstadoCuota`, `Membresia`, `.registrarCheckIn`, `Persona`, `Reportable`, `.actualizarMetricas`, `Facturable`, `SocioService`?**
  _High betweenness centrality (0.190) - this node is a cross-community bridge._
- **What connects `BOXEO`, `CROSSFIT`, `FUNCIONAL` to the rest of the system?**
  _31 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Rutina` be split into smaller, more focused modules?**
  _Cohesion score 0.06829573934837092 - nodes in this community are weakly interconnected._
- **Why does `SistemaTest` connect `.registrarCheckIn` to `🏋️‍♂️ FITMANAGE PRO`, `ClasesPanel.java`, `Socio`, `🚀 Cómo Ejecutar el Proyecto`, `INFORME DE AUTO-AUDITORÍA Y VERIFICACIÓN DE FITMANAGE PRO`, `Reportable`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **Should `ClaseGrupal` be split into smaller, more focused modules?**
  _Cohesion score 0.1383399209486166 - nodes in this community are weakly interconnected._
- **Why does `ClaseGrupal` connect `ClaseGrupal` to `ClasesPanel`, `SocioRepository`, `ClasesPanel.java`, `Instructor`, `.getSociosInscriptos`, `Socio`, `.registrarCheckIn`, `Disciplina`, `CheckInDialog`?**
  _High betweenness centrality (0.063) - this node is a cross-community bridge._
- **Should `Pago` be split into smaller, more focused modules?**
  _Cohesion score 0.05052631578947368 - nodes in this community are weakly interconnected._