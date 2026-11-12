# FITMANAGE PRO - SISTEMA INTEGRAL DE GESTIÓN PARA CLUB DEPORTIVO

> **Plataforma de Escritorio en Java 21 SE (Swing)**  
> **Cátedra:** Programación Orientada a Objetos / Laboratorio de Software  
> **Versión:** 1.0.0  
> **Año:** Noviembre 2026  

---

## 1. DESCRIPCIÓN DEL PROYECTO

**FitManage Pro** es una solución integral para la administración deportiva, logística y contable de un club o gimnasio de alto rendimiento. Desarrollada en **Java 21 LTS** bajo el paradigma de **Programación Orientada a Objetos Pura (POO)** y arquitectura en capas desacoplada.

El sistema cuenta con:
* **Jerarquía de Herencia:** Clase base abstracta `Persona` con especializaciones `Socio` e `Instructor`.
* **Polimorfismo:** Métodos `mostrarDetalle()`, `validarIngreso(TipoAcceso)` y `calcularMontoTotal()`.
* **Interfaces de Contrato:** `Facturable`, `Autenticable` y `Reportable`.
* **Composición Fuerte:** `Rutina *-- Ejercicio` (ciclo de vida ligado, cálculo dinámico de tonelaje global).
* **Agregación Débil:** `ClaseGrupal o-- Instructor` y `ClaseGrupal o-- Socio` (control estricto de cupo y lanzamiento de `CupoAgotadoException`).
* **Persistencia Autónoma en JSON:** Motor `JsonPersistenceEngine` nativo con reemplazo atómico sin dependencias externas.
* **Interfaz Gráfica Swing:** Dashboard ejecutivo moderno con `CardLayout`, tablas estilizadas, badges de estado semafóricos y modal interactivo de molinete (Check-In).

---

## 2. SIMULACIÓN DE EQUIPO COLABORATIVO (6 INTEGRANTES)

| Integrante | Rol en el Proyecto | Responsabilidad Principal |
| :--- | :--- | :--- |
| **1. Juan Pérez** | *Líder Técnico & Arquitecto Core POO* | Jerarquía de herencia `Persona`, `Socio`, interfaces `Autenticable` y `Reportable`. |
| **2. María Gomez** | *Especialista en Entrenamiento Deportivo* | Composición `Rutina *-- Ejercicio`, dosificación de cargas y volumen global. |
| **3. Lucas Rodríguez** | *Gestor de Logística, Clases & Horarios* | `ClaseGrupal`, cronograma, cupos máximos y excepción `CupoAgotadoException`. |
| **4. Sofia Fernández** | *Ingeniera de Finanzas & Facturación* | `Membresia`, `Pago`, interface `Facturable`, 10% descuento por antigüedad y recibos. |
| **5. Nicolás Martínez** | *Diseñador UI/UX & Especialista Swing* | `MainFrame`, `DashboardPanel`, CardLayout, Badges de estado y Check-In Dialog. |
| **6. Camila Díaz** | *Ingeniera de Persistencia, QA & Auditoría* | Motor `JsonPersistenceEngine`, repositorios, suite de tests y scripts de compilación. |

---

## 3. ESTRUCTURA DEL PROYECTO

```
/home/juancito/Documents/Orca/java/
├── bin/                          # Clases compiladas (.class)
├── data/                         # Archivos de persistencia local JSON
│   ├── socios.json
│   ├── instructores.json
│   ├── clases.json
│   ├── rutinas.json
│   ├── pagos.json
│   └── asistencias.json
├── graphify-out/                 # Grafo arquitectónico y visualización
├── scripts/                      # Automatización bash
│   ├── compile.sh                # Compilación limpia con javac
│   ├── run.sh                    # Ejecución de la GUI
│   └── test.sh                   # Ejecución de la suite de pruebas
├── src/                          # Código fuente Java 21
│   └── com/clubdeportivo/
│       ├── gui/                  # Ventana principal, paneles y componentes
│       ├── model/                # Entidades, enums e interfaces POO
│       ├── repository/           # Repositorios con persistencia JSON
│       ├── service/              # Lógica de negocio y semillero
│       ├── test/                 # Suite automatizada SistemaTest
│       └── util/                 # JsonPersistenceEngine, Formatters, Validaciones
├── PLAN.md                       # Especificación técnica aprobada
├── AUDIT.md                      # Informe final de auto-auditoría
└── README.md                     # Guía de usuario (este archivo)
```

---

## 4. INSTRUCCIONES DE COMPILACIÓN Y EJECUCIÓN

El proyecto no requiere Maven ni Gradle. Se compila y ejecuta directamente con el JDK 21 estándar de Linux.

### 4.1 Compilación
```bash
bash scripts/compile.sh
```

### 4.2 Ejecución de la Suite de Pruebas (7 Tests Automatizados)
```bash
bash scripts/test.sh
```

### 4.3 Ejecución de la Interfaz Gráfica (Swing)
```bash
bash scripts/run.sh
```

---

## 5. GENERACIÓN DEL GRAFO DE ARQUITECTURA (GRAPHIFY)

El proyecto incluye la extracción y análisis de dependencias mediante Graphify:
```bash
# Actualizar el grafo de dependencias
graphify update /home/juancito/Documents/Orca/java

# Exportar diagrama interactivo
graphify export callflow-html --output graphify-out/arquitectura.html
```
