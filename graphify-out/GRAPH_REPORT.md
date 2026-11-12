# Graph Report - java  (2026-11-11)

## Corpus Check
- 64 files · ~30,592 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 1 file(s) not represented in the graph (top: (none) 1)

## Summary
- 865 nodes · 2181 edges · 42 communities (23 shown, 19 thin omitted)
- Extraction: 81% EXTRACTED · 19% INFERRED · 0% AMBIGUOUS · INFERRED: 410 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `273091b3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Rutina
- ClaseGrupal
- Pago
- SocioRepository
- JsonPersistenceEngine
- ClasesPanel.java
- Instructor
- Ejercicio
- Membresia
- Socio
- javax.swing.event.DocumentEvent
- Persona
- SistemaTest
- DashboardPanel
- PLAN DE EJECUCIÓN: SISTEMA INTEGRAL DE GESTIÓN PARA CLUB DEPORTIVO / GIMNASIO (FITMANAGE PRO)
- Disciplina
- EstadoAcceso
- INFORME DE AUTO-AUDITORÍA Y VERIFICACIÓN DE FITMANAGE PRO
- AsistenciaRepository
- JsonParser
- Asistencia
- Reportable
- TipoAcceso
- CheckInDialog
- MainFrame
- Facturable
- TipoMembresia
- 10. PROTOCOLO DE AUTO-AUDITORÍA Y PRUEBAS
- 2. DESCRIPCIÓN DETALLADA DEL DOMINIO Y REGLAS DE NEGOCIO
- Detalle de Fases y Criterios de Aceptación
- .registrarSocio
- ClasesPanel
- CupoAgotadoException
- Autenticable
- ClaseService
- compile.sh
- run.sh
- test.sh
- .keyPressed

## God Nodes (most connected - your core abstractions)
1. `Socio` - 64 edges
2. `ClaseGrupal` - 50 edges
3. `Instructor` - 47 edges
4. `Rutina` - 42 edges
5. `Asistencia` - 36 edges
6. `SocioRepository` - 36 edges
7. `Pago` - 33 edges
8. `SocioService` - 31 edges
9. `Ejercicio` - 30 edges
10. `Persona` - 26 edges

## Surprising Connections (you probably didn't know these)
- `2. MATRIZ DE CUMPLIMIENTO DE REQUISITOS OBLIGATORIOS` --references--> `SistemaTest`  [INFERRED]
  AUDIT.md → src/com/clubdeportivo/test/SistemaTest.java
- `10.4 Checklist de Cumplimiento de Requisitos de la Cátedra` --references--> `Autenticable`  [INFERRED]
  PLAN.md → src/com/clubdeportivo/model/Autenticable.java
- `6.1 Distribución de Integrantes y Módulos de Trabajo` --references--> `Autenticable`  [INFERRED]
  PLAN.md → src/com/clubdeportivo/model/Autenticable.java
- `Fase 2: Dominio Core POO, Interfaces y Enums (Integrantes 1, 2, 3, 4)` --references--> `Autenticable`  [INFERRED]
  PLAN.md → src/com/clubdeportivo/model/Autenticable.java
- `1. DESCRIPCIÓN DEL PROYECTO` --references--> `Autenticable`  [INFERRED]
  README.md → src/com/clubdeportivo/model/Autenticable.java

## Import Cycles
- None detected.

## Communities (42 total, 19 thin omitted)

### Community 0 - "Rutina"
Cohesion: 0.06
Nodes (4): RutinasPanel, Rutina, RutinaRepository, RutinaService

### Community 2 - "Pago"
Cohesion: 0.06
Nodes (8): PagosPanel, DefaultTableModel, MetodoPago, EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, Pago

### Community 3 - "SocioRepository"
Cohesion: 0.06
Nodes (9): ClaseRepository, InstructorRepository, IRepository, PagoRepository, SocioRepository, AccesoService, DataSeedService, FacturacionService (+1 more)

### Community 5 - "ClasesPanel.java"
Cohesion: 0.09
Nodes (6): ModernButton, SearchTextField, FocusAdapter, StatusBadgeCellRenderer, ThemeConfig, Formatters

### Community 6 - "Instructor"
Cohesion: 0.09
Nodes (4): InstructorItem, InstructoresPanel, Instructor, InstructorService

### Community 7 - "Ejercicio"
Cohesion: 0.07
Nodes (8): Ejercicio, GrupoMuscular, BRAZOS, CORE, ESPALDA, HOMBROS, PECHO, PIERNAS

### Community 9 - "Socio"
Cohesion: 0.05
Nodes (13): SocioItem, SocioItem, DefaultTableModel, DefaultTableModel, SocioItem, SociosPanel, EstadoCuota, AL_DIA (+5 more)

### Community 10 - "javax.swing.event.DocumentEvent"
Cohesion: 0.12
Nodes (7): DefaultTableModel, DocumentListener, DefaultTableModel, DocumentListener, DocumentListener, DefaultTableModel, DocumentListener

### Community 12 - "SistemaTest"
Cohesion: 0.22
Nodes (3): 10.2 Suite de Pruebas Unitarias y de Integración (`SistemaTest.java`), RunnableTest, SistemaTest

### Community 13 - "DashboardPanel"
Cohesion: 0.15
Nodes (3): MetricCard, DashboardPanel, DefaultTableModel

### Community 14 - "PLAN DE EJECUCIÓN: SISTEMA INTEGRAL DE GESTIÓN PARA CLUB DEPORTIVO / GIMNASIO (FITMANAGE PRO)"
Cohesion: 0.12
Nodes (15): 1.1 Resumen Ejecutivo, 1.2 Ficha Técnica, 1. RESUMEN EJECUTIVO Y FICHA TÉCNICA DEL PROYECTO, 4. DIAGRAMA DE CLASES UML COMPLETO (FORMATO MERMAID), 5.1 Diagrama de Capas, 5.2 Estructura Física del Proyecto en el Árbol de Directorios, 5. ARQUITECTURA DEL SOFTWARE Y ESTRUCTURA DE PAQUETES, 7.1 Filosofía de Cero Dependencias Externas (+7 more)

### Community 15 - "Disciplina"
Cohesion: 0.14
Nodes (7): Disciplina, BOXEO, CROSSFIT, FUNCIONAL, PILATES, SPINNING, YOGA

### Community 16 - "EstadoAcceso"
Cohesion: 0.14
Nodes (6): EstadoAcceso, DENEGADO_CUOTA_VENCIDA, DENEGADO_MEMBRESIA_INSUFICIENTE, DENEGADO_NO_ENCONTRADO, DENEGADO_SIN_CUPO, PERMITIDO

### Community 17 - "INFORME DE AUTO-AUDITORÍA Y VERIFICACIÓN DE FITMANAGE PRO"
Cohesion: 0.15
Nodes (12): 1. RESUMEN EJECUTIVO Y ESTADO DE APROBACIÓN, 2. MATRIZ DE CUMPLIMIENTO DE REQUISITOS OBLIGATORIOS, 3.1 Análisis de los Casos de Prueba, 3. RESULTADOS DE LA EJECUCIÓN DE PRUEBAS (`scripts/test.sh`), 4.1 Métricas del Grafo de Código, 4.2 Nodos Nucleares ("God Nodes" / Abstracciones Principales), 4.3 Verificación de Desacoplamiento por Capas (Path Analysis), 4. AUDITORÍA ARQUITECTÓNICA CON GRAPHIFY (+4 more)

### Community 21 - "Reportable"
Cohesion: 0.14
Nodes (10): 1. DESCRIPCIÓN DEL PROYECTO, 2. SIMULACIÓN DE EQUIPO COLABORATIVO (6 INTEGRANTES), 3. ESTRUCTURA DEL PROYECTO, 4.1 Compilación, 4.2 Ejecución de la Suite de Pruebas (7 Tests Automatizados), 4.3 Ejecución de la Interfaz Gráfica (Swing), 4. INSTRUCCIONES DE COMPILACIÓN Y EJECUCIÓN, 5. GENERACIÓN DEL GRAFO DE ARQUITECTURA (GRAPHIFY) (+2 more)

### Community 22 - "TipoAcceso"
Cohesion: 0.18
Nodes (3): TipoAcceso, CLASE_GRUPAL, GENERAL

### Community 25 - "MainFrame"
Cohesion: 0.12
Nodes (4): MouseAdapter, MainFrame, NavButton, java.awt.event.MouseAdapter

### Community 26 - "Facturable"
Cohesion: 0.22
Nodes (4): 6.1 Distribución de Integrantes y Módulos de Trabajo, 6.2 Convención de Comentarios y Trazabilidad Colaborativa, 6. SIMULACIÓN DE TRABAJO EN EQUIPO (6 INTEGRANTES), Facturable

### Community 27 - "TipoMembresia"
Cohesion: 0.18
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

## Knowledge Gaps
- **31 isolated node(s):** `SPINNING`, `CROSSFIT`, `YOGA`, `FUNCIONAL`, `BOXEO` (+26 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 244 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **19 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Socio` connect `Socio` to `Rutina`, `ClaseGrupal`, `.serializarSocio`, `SocioRepository`, `Autenticable`, `ClasesPanel.java`, `Membresia`, `Persona`, `SistemaTest`, `Asistencia`, `Reportable`, `Facturable`, `.registrarSocio`?**
  _High betweenness centrality (0.288) - this node is a cross-community bridge._
- **What connects `SPINNING`, `CROSSFIT`, `YOGA` to the rest of the system?**
  _31 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Rutina` be split into smaller, more focused modules?**
  _Cohesion score 0.0625694187338023 - nodes in this community are weakly interconnected._
- **Why does `ClaseGrupal` connect `ClaseGrupal` to `ClasesPanel`, `SocioRepository`, `JsonPersistenceEngine`, `ClaseService`, `Instructor`, `.mostrarFichaClase`, `Socio`, `SistemaTest`, `Disciplina`, `CheckInDialog`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **Should `ClaseGrupal` be split into smaller, more focused modules?**
  _Cohesion score 0.13438735177865613 - nodes in this community are weakly interconnected._
- **Why does `Instructor` connect `Instructor` to `ClaseGrupal`, `.serializarSocio`, `SocioRepository`, `JsonPersistenceEngine`, `ClasesPanel.java`, `Socio`, `Persona`, `Disciplina`, `Reportable`, `.registrarSocio`?**
  _High betweenness centrality (0.084) - this node is a cross-community bridge._
- **Should `Pago` be split into smaller, more focused modules?**
  _Cohesion score 0.06290471785383904 - nodes in this community are weakly interconnected._