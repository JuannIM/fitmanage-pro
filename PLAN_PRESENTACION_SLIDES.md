# PLAN DE EJECUCIÓN: GUION DE EXPOSICIÓN ORAL Y SLIDESHOW INTERACTIVO
## FitManage Pro - Sistema Integral de Gestión para Club Deportivo (Java 21 SE)

> **Documento Estratégico y Técnico de Planificación**  
> **Cátedra:** Programación Orientada a Objetos / Laboratorio de Software  
> **Equipo Colaborativo:** Juan, Rocio, Merlina, Alfonsina, Sara, Joel  
> **Versión del Plan:** 1.0.0 LTS  
> **Fecha:** Noviembre 2026  
> **Ubicación:** `/home/juancito/Documents/Orca/java/PLAN_PRESENTACION_SLIDES.md`

---

## 1. RESUMEN EJECUTIVO Y OBJETIVOS ESTRATÉGICOS

### 1.1 Contexto y Situación de Partida
El proyecto **FitManage Pro** ha concluido con éxito su fase de desarrollo y auditoría arquitectónica (ver `AUDIT.md` y `UML_DIAGRAM.md`). Cuenta con una base de código de 52 archivos Java compilables sin frameworks pesados ni dependencias externas bajo **Java 21 LTS**, persistencia atómica en JSON, interfaz gráfica moderna en **Java Swing (CardLayout)**, una suite de 7 tests automatizados con 100% de aprobación (`scripts/test.sh`) y un análisis de grafo arquitectónico libre de ciclos con Graphify (852 nodos).

El objetivo supremo de esta etapa es preparar el **material de defensa académica y divulgación ejecutiva** para la instancia evaluadora final, asegurando una nota sobresaliente y demostrando solvencia teórica y práctica en Programación Orientada a Objetos.

### 1.2 Entregables Comprometidos
El plan contempla la construcción coordinada de **dos artefactos complementarios**:

1. **`guion_presentacion.md` & `guion_presentacion.pdf`**:
   - Guion oral sincronizado de **duración máxima de 20 minutos** (18 minutos cronometrados de discurso neto + 2 minutos de colchón para apertura y preguntas).
   - Distribución perfectamente equitativa entre los **6 integrantes** (~3 minutos exactos por expositor: 180s ± 15s).
   - Rigor técnico absoluto: justificación del modelado UML (herencia, agregación vs composición, polimorfismo, interfaces, excepciones), referencias directas a clases Java y preparación de respuestas a preguntas trampa docentes ("Defensa de Cátedra").
   - Pipeline automatizado de compilación a PDF académico vía **Google Chrome Headless** (`--headless=new --print-to-pdf`).

2. **`presentacion_fitmanage.html`**:
   - Diapositivas web interactivas desarrolladas con la skill **`frontend-slides`**.
   - **Estricto cumplimiento de principios no negociables**: Canvas 16:9 fijo (1920×1080), single HTML autónomo con CSS y JS inline (cero dependencias externas npm/webpack), visibilidad basada en clases `.active`/`.visible`, escalado uniforme a la ventana del navegador (`deckStage.style.transform`), navegación por teclado/táctil y hotzone de edición inline (`E`).
   - **Slide Estrella (Requisito Obligatorio)**: Diagrama de Clases UML completo renderizado en formato **SVG vectorial nativo**, nítido, legible e hiper-definido, con simbología estándar formal (rombo lleno, rombo vacío, flechas punteadas y generalización).
   - Estética visual personalizada *"Dark Executive Athletic / Obsidian Slate"*, evitando plantillas genéricas o "AI-slop".

### 1.3 Métricas de Éxito y Parámetros Cuantitativos

| Métrica | Meta Cuantitativa | Mecanismo de Verificación |
| :--- | :--- | :--- |
| **Tiempo Total de Exposición** | $\le 20\text{ min}$ (Rango óptimo: 18:00 - 19:30) | Ensayo cronometrado con marcas de tiempo minuto a minuto. |
| **Equidad de Participación** | $3\text{ min } \pm 15\text{ s}$ por integrante | Conteo de palabras (~350 a 400 palabras por estudiante) y reloj. |
| **Cobertura Conceptual POO** | 100% de conceptos cardinales cubiertos | Matriz de trazabilidad: Herencia, Polimorfismo, Composición, Agregación, Interfaces, Excepciones, Persistencia Atómica. |
| **Fidelidad al Modelo UML** | 100% de clases y relaciones de `UML_DIAGRAM.md` | Diagrama SVG completo integrado en la Slide 3. |
| **Arquitectura Frontend-Slides** | 1920×1080, Zero-Deps, Standalone HTML | Verificación en navegador y exportación a PDF sin dependencias de red. |
| **Compilación PDF del Guion** | Generación autónoma sin errores en Linux | Script `scripts/generate_speech_pdf.py` usando `google-chrome --headless`. |

---

## 2. DESGLOSE MINUCIOSO DEL GUION ORAL POR INTEGRANTE

### 2.1 Cronograma Temporal Maestro (20 Minutos Máximo)

```
[00:00 - 03:15] JUAN       : Apertura, Visión FitManage, Arquitectura en Capas y Jerarquía Persona
[03:15 - 06:15] ROCIO      : Módulo Rutinas, Composición Fuerte y Tonelaje Acumulado
[06:15 - 09:15] MERLINA    : Clases Grupales, Agregación Débil, Cupo y Excepciones
[09:15 - 12:15] ALFONSINA  : Membresías, Facturable, Regla 10% Antigüedad y Recibos
[12:15 - 15:15] SARA       : Arquitectura GUI Swing, CardLayout, Semáforos y Molinete Virtual
[15:15 - 18:30] JOEL       : Persistencia Atómica JSON, Tests (7/7), Grafo Graphify y Cierre
[18:30 - 20:00] EQUIPO     : Buffer de Contingencia y Ronda Abierta de Preguntas del Jurado
```

---

### 2.2 Especificación Detallada por Integrante

#### 1. JUAN (Minuto 00:00 a 03:15 — Duración: 3 min 15 seg)
* **Rol:** Portavoz Inicial, Arquitectura de Software y Jerarquía de Herencia.
* **Slides Asociadas:** 
  - `Slide 01: Portada Ejecutiva FitManage Pro`
  - `Slide 02: Dominio, Propósito y Arquitectura en 4 Capas`
  - `Slide 03: Diagrama de Clases UML Completo`
  - `Slide 04: Jerarquía de Herencia & Abstracción (Persona, Socio, Instructor)`
* **Objetivos Pedagógicos:**
  - Saludo formal, presentación del equipo y contextualización del problema del gimnasio moderno.
  - Explicación de la arquitectura desacoplada en 4 capas: `GUI -> SERVICE -> REPOSITORY -> MODEL/JSON`.
  - Fundamentación teórica de la clase base abstracta `Persona` y atributos protegidos (`#`).
  - Polimorfismo forzado en métodos abstractos `mostrarDetalle()` y `validarIngreso(TipoAcceso)`.
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/model/Persona.java`
  - `src/com/clubdeportivo/model/Socio.java`
  - `src/com/clubdeportivo/model/Autenticable.java`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Por qué declararon `Persona` como clase `abstract` en lugar de una interfaz o una clase concreta? ¿Por qué no usar composición para los roles de Socio e Instructor?"  
  > *Respuesta Juan:* "Declaramos `Persona` abstracta porque en el dominio ontológico de un club deportivo no existe una 'persona genérica'; todo individuo que ingresa al sistema tiene un rol concreto e intransferible. Instanciar `new Persona()` carece de semántica de negocio. La herencia nos permite reutilizar estado común fuertemente tipado (`id`, `dni`, `nombre`, `email`, `fechaNacimiento`) y comportamiento concreto compartido como `calcularEdad()`, mientras forzamos polimórficamente a través de métodos abstractos como `mostrarDetalle()` que cada subclase exhiba sus particularidades. Si bien el patrón State o Decorator permitiría mutar de rol en tiempo de ejecución, en este alcance académico la especialización rígida garantiza integridad referencial y tipado seguro en tiempo de compilación."
* **Pase de Palabra:** *"Para comprender cómo se gestiona el entrenamiento y la dosificación de cargas deportivas, le cedo la palabra a mi compañera Rocio con el módulo de Rutinas."*

---

#### 2. ROCIO (Minuto 03:15 a 06:15 — Duración: 3 min 00 seg)
* **Rol:** Módulo Deportivo, Dosificación de Cargas y Relación de Composición.
* **Slides Asociadas:**
  - `Slide 05: Composición Fuerte: Rutina, Ejercicios y Cálculo de Tonelaje`
* **Objetivos Pedagógicos:**
  - Definición rigurosa de **Composición Fuerte** (`Rutina *-- Ejercicio`, rombo negro lleno).
  - Justificación del ciclo de vida existencialmente dependiente: un `Ejercicio` con series, repeticiones y peso sugerido no tiene sentido fuera de su rutina asignada.
  - Explicación del algoritmo de negocio: cálculo del volumen global o tonelaje ($\sum series \times reps \times pesoKg$).
  - Demostración de recálculo dinámico en `Rutina.java` (incorporación y baja de ejercicios).
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/model/Rutina.java`
  - `src/com/clubdeportivo/model/Ejercicio.java`
  - `src/com/clubdeportivo/model/GrupoMuscular.java`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Qué diferencia práctica hay entre agregación y composición en este caso? Si elimino una Rutina en el sistema, ¿qué ocurre con sus ejercicios?"  
  > *Respuesta Rocio:* "La composición modela una relación 'parte-todo' donde las partes no tienen ciclo de vida independiente. En nuestro sistema, la `Rutina` es la raíz del agregado (DDD). Un `Ejercicio` no es un catálogo estático; es una prescripción personalizada (ej: 4 series de 10 reps con 80 kg para Pecho). Si la `Rutina` se destruye, sus instancias de `Ejercicio` se desreferencian y son reclamadas por el Garbage Collector, y a nivel de persistencia en `rutinas.json` sus nodos anidados desaparecen. En cambio, si fuera agregación, el ejercicio tendría una identidad global propia y persistiría en una tabla separada, lo cual aquí sobrecargaría el modelo innecesariamente."
* **Pase de Palabra:** *"A continuación, Merlina explicará la contracara de esta relación: la agregación débil en nuestras Clases Grupales y el control de cupo."*

---

#### 3. MERLINA (Minuto 06:15 a 09:15 — Duración: 3 min 00 seg)
* **Rol:** Logística de Clases Grupales, Agregación Débil y Manejo de Excepciones.
* **Slides Asociadas:**
  - `Slide 06: Agregación Débil, Control de Aforo y Manejo de Excepciones`
* **Objetivos Pedagógicos:**
  - Definición rigurosa de **Agregación Débil** (`ClaseGrupal o-- Instructor` y `ClaseGrupal o-- Socio`, rombo blanco vacío).
  - Justificación de la independencia de ciclo de vida: si se cancela una clase de Spinning, ni el profesor ni los socios son dados de baja del club.
  - Regla de aforo y capacidad máxima (`cupoMaximo`).
  - Creación y lanzamiento de `CupoAgotadoException` como excepción de negocio (`checked exception`).
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/model/ClaseGrupal.java`
  - `src/com/clubdeportivo/model/CupoAgotadoException.java`
  - `src/com/clubdeportivo/model/Disciplina.java`
  - `src/com/clubdeportivo/service/ClaseService.java`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Por qué implementaron `CupoAgotadoException` como una excepción propia en lugar de devolver simplemente `false` o usar `IllegalStateException`?"  
  > *Respuesta Merlina:* "Optamos por una excepción verificada de dominio (`checked exception`) para aplicar el principio de Fallo Rápido (*Fail-Fast*) y hacer explícito el contrato en la firma del método `inscribirSocio(Socio s) throws CupoAgotadoException`. Si devolviéramos un booleano `false`, la capa que llama podría ignorar silenciosamente el resultado por descuido del programador. La excepción obliga a la capa visual (`ClasesPanel`) a capturar el evento y desplegar una advertencia semafórica inmediata al usuario en la UI, garantizando trazabilidad y robustez."
* **Pase de Palabra:** *"Ahora Alfonsina nos presentará el corazón financiero del club: la facturación polimórfica y las políticas de membresía."*

---

#### 4. ALFONSINA (Minuto 09:15 a 12:15 — Duración: 3 min 00 seg)
* **Rol:** Módulo Administrativo, Interfaz Facturable y Beneficios por Antigüedad.
* **Slides Asociadas:**
  - `Slide 07: Polimorfismo de Facturación, Antigüedad (10%) y Comprobantes`
* **Objetivos Pedagógicos:**
  - Contrato formal mediante la interfaz `Facturable` (`calcularMontoTotal()`, `generarComprobante()`, `estaAlDia()`, `aplicarDescuento()`).
  - Entidades `Membresia`, `Pago` y enums de dominio restringido (`TipoMembresia`, `EstadoCuota`, `MetodoPago`).
  - Regla de negocio de fidelización: 10% de descuento automático por antigüedad si el socio supera los 12 meses activo en el club.
  - Emisión de recibo formal correlativo y control de mora.
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/model/Facturable.java`
  - `src/com/clubdeportivo/model/Membresia.java`
  - `src/com/clubdeportivo/model/Pago.java`
  - `src/com/clubdeportivo/service/FacturacionService.java`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Por qué `Facturable` es una interfaz y no una clase abstracta? ¿Qué beneficio aporta en el diseño?"  
  > *Respuesta Alfonsina:* "El uso de la interfaz responde al Principio de Segregación de Interfaces (ISP). `Socio` ya hereda de `Persona`. Java no admite herencia múltiple de clases, pero sí implementación múltiple de interfaces. Además, el módulo de cobranzas solo necesita interactuar con entidades que sepan responder `calcularMontoTotal()`. Mañana el club podría vender suplementos deportivos, indumentaria o alquilar una cancha de fútbol; todas esas entidades implementarán `Facturable` sin necesidad de ser personas, permitiendo al servicio de facturación tratarlas de manera 100% polimórfica."
* **Pase de Palabra:** *"Para mostrar cómo se traduce toda esta lógica a los ojos del usuario final, Sara presentará la arquitectura de la Interfaz Gráfica."*

---

#### 5. SARA (Minuto 12:15 a 15:15 — Duración: 3 min 00 seg)
* **Rol:** Interfaz Gráfica de Usuario (Java Swing), UX/UI y Simulación de Molinete.
* **Slides Asociadas:**
  - `Slide 08: Interfaz Gráfica Swing: CardLayout, ThemeConfig y Badges`
  - `Slide 09: Check-In en Tiempo Real: Molinete Virtual y Control de Acceso`
* **Objetivos Pedagógicos:**
  - Arquitectura Swing desacoplada: `MainFrame` con sidebar y gestor `CardLayout` para navegabilidad fluida sin parpadeo de ventanas múltiples.
  - Centralización estética con `ThemeConfig.java`: paleta corporativa Slate/Blue, fuentes tipográficas y constantes métricas.
  - Componentes custom: `StatusBadgeCellRenderer` para semaforización cromática de cuotas (verde al día, rojo vencido, ámbar pendiente) y tarjetas métricas `MetricCard`.
  - Simulador de Molinete en tiempo real: `CheckInDialog` con evaluación instantánea de credenciales, feedback visual y registro de asistencias.
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/gui/MainFrame.java`
  - `src/com/clubdeportivo/gui/theme/ThemeConfig.java`
  - `src/com/clubdeportivo/gui/components/StatusBadgeCellRenderer.java`
  - `src/com/clubdeportivo/gui/CheckInDialog.java`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Por qué eligieron `CardLayout` en lugar de abrir ventanas `JDialog` o `JFrame` separadas para cada menú? ¿Cómo garantizan que la GUI no acceda directamente a los datos?"  
  > *Respuesta Sara:* "Utilizar `CardLayout` dentro de un único `MainFrame` ofrece una experiencia de usuario profesional, moderna y cohesionada similar a una SPA (Single Page Application), evitando la saturación de ventanas flotantes en el escritorio del operador. Respecto al desacoplamiento, la GUI jamás lee ni escribe archivos JSON. Cada panel (`SociosPanel`, `PagosPanel`) se comunica exclusivamente con la capa de Servicios (`SocioService`, `FacturacionService`). La herramienta Graphify comprobó que el camino más corto entre `SociosPanel` y `SocioRepository` requiere 2 saltos (`SociosPanel -> FacturacionService -> SocioRepository`), demostrando que se respeta estrictamente el principio de responsabilidad única."
* **Pase de Palabra:** *"Finalmente, Joel nos explicará cómo garantizamos la persistencia sin bases de datos pesadas y cómo se certificó la calidad del sistema."*

---

#### 6. JOEL (Minuto 15:15 a 18:30 — Duración: 3 min 15 seg)
* **Rol:** Persistencia Atómica JSON, Suite de Pruebas, Graphify y Cierre Grupal.
* **Slides Asociadas:**
  - `Slide 10: Persistencia Atómica en JSON: JsonPersistenceEngine`
  - `Slide 11: Calidad de Software: Suite Automatizada (7/7) y Graphify`
  - `Slide 12: Matriz de Trazabilidad Colaborativa y Defensa Docente`
  - `Slide 13: Conclusiones, Logros Técnicos y Ronda de Preguntas`
* **Objetivos Pedagógicos:**
  - Persistencia autónoma en `JsonPersistenceEngine.java` sin Jackson ni Gson, cumpliendo con la exigencia de Java SE puro.
  - Mecanismo de guardado seguro mediante **escritura atómica** (`.tmp` + `StandardCopyOption.ATOMIC_MOVE`) para protección contra caídas de tensión o cierres abruptos.
  - Presentación de los resultados de la suite automatizada `SistemaTest.java` (7/7 tests aprobados en consola).
  - Certificación arquitectónica con Graphify: 852 nodos, 2.168 relaciones y cero ciclos de dependencia.
  - Conclusión integradora y apertura a las preguntas del tribunal evaluador.
* **Citas al Código Fuente:**
  - `src/com/clubdeportivo/util/JsonPersistenceEngine.java`
  - `src/com/clubdeportivo/repository/IRepository.java`
  - `src/com/clubdeportivo/test/SistemaTest.java`
  - `scripts/test.sh` y `scripts/compile.sh`
* **Defensa Docente (Pregunta Típica del Jurado):**
  > *Profesor:* "¿Qué garantiza la persistencia atómica en su motor JSON? ¿Qué pasaría si la computadora se apaga exactamente mientras se escribe un archivo?"  
  > *Respuesta Joel:* "El método `saveToFileAtomic()` escribe primero todo el contenido en un archivo temporal con sufijo `.tmp` invocando `channel.force(true)` para volcar el búfer de memoria a disco físico. Únicamente si esa escritura concluye con éxito, se ejecuta `Files.move(..., ATOMIC_MOVE)`. En sistemas de archivos compatibles con POSIX como Linux ext4, el renombre atómico es una operación del inodo que tarda microsegundos y es indivisible: o queda el archivo previo intacto o queda el nuevo. Es imposible que el archivo de datos (`socios.json`, `pagos.json`) quede truncado a la mitad o corrupto."
* **Cierre Formal del Grupo (Minuto 18:15 a 18:30):**  
  *"En síntesis, FitManage Pro no es solo un software que funciona; es un ejemplo vivo de ingeniería orientada a objetos pura, mantenible, extensible y testeada. Quedamos a total disposición del tribunal para responder a sus preguntas y proceder con la demostración en vivo de la aplicación."*

---

## 3. ESTRUCTURA Y DISEÑO DEL SLIDESHOW INTERACTIVO ('frontend-slides')

### 3.1 Adherencia Irrestricta a la Skill `frontend-slides`
1. **Single File Standalone:** Todo el código HTML, CSS (`<style>`) y JS (`<script>`) residirá en un único archivo: `presentacion_fitmanage.html` en la raíz del proyecto.
2. **Fixed 16:9 Stage Invariant:** Canvas fijo de 1920×1080 píxeles dentro de un contenedor `.deck-viewport` y `.deck-stage`, escalado matemáticamente a cualquier resolución mediante transformaciones CSS uniformes:
   $$\text{factor} = \min\left(\frac{window.innerWidth}{1920}, \frac{window.innerHeight}{1080}\right)$$
   $$\text{offset}_X = \frac{window.innerWidth - 1920 \times \text{factor}}{2}, \quad \text{offset}_Y = \frac{window.innerHeight - 1080 \times \text{factor}}{2}$$
3. **Visibilidad Segura:** Uso estricto de `.slide { visibility: hidden; opacity: 0; pointer-events: none; }` y activación con `.slide.active, .slide.visible { visibility: visible; opacity: 1; pointer-events: auto; }`. **Prohibido el uso de `display: none`** para evitar colisiones con layouts flexbox.
4. **Modo Edición Inline ('E'):** Inclusión de la hotzone interactiva (área superior izquierda de 80×80 px con retraso de 400 ms) y atajo de teclado tecla `E` para modificar textos en caliente durante ensayos.

### 3.2 Sistema de Diseño Visual: "Obsidian Athletic & Electric Slate"
Para superar la mediocridad del "AI-slop", se adopta una estética audaz, atlética y corporativa que replica la identidad visual de FitManage Pro:

* **Paleta de Colores Tokenizada (CSS Variables):**
  ```css
  :root {
      /* Superficies y Fondos */
      --bg-stage: #030712;         /* Abismo ultra-oscuro para letterboxing */
      --slide-bg: #0b1120;         /* Slate obsidian profundo */
      --card-bg: rgba(30, 41, 59, 0.7); /* Slate-800 con translucidez de cristal */
      --card-border: #334155;      /* Borde sutil slate-700 */
      --card-glow: rgba(37, 99, 235, 0.15);

      /* Acentos Atléticos y Corporativos */
      --accent-primary: #2563eb;   /* Azul Royal Corporativo (FitManage Blue) */
      --accent-blue-glow: #60a5fa;
      --accent-cyan: #06b6d4;      /* Cyan eléctrico para interfaces */
      --accent-emerald: #10b981;   /* Verde esmeralda para estados al día y tests */
      --accent-amber: #f59e0b;     /* Ámbar para advertencias de cupo */
      --accent-rose: #f43f5e;      /* Rojo frambuesa para deudas y excepciones */
      --accent-violet: #8b5cf6;    /* Violeta para clases abstractas */

      /* Textos y Jerarquía */
      --text-white: #f8fafc;
      --text-muted: #94a3b8;
      --text-dim: #64748b;

      /* Tipografía (Google Fonts) */
      --font-display: 'Plus Jakarta Sans', system-ui, sans-serif;
      --font-body: 'Outfit', system-ui, sans-serif;
      --font-mono: 'JetBrains Mono', monospace;
  }
  ```

* **Tipografía Expresiva:**
  - `Plus Jakarta Sans` (pesos 700 y 800) para titulares masivos, directos y limpios.
  - `Outfit` (pesos 400, 500 y 600) para cuerpos de texto, fichas y descripciones técnicas.
  - `JetBrains Mono` (pesos 500 y 700) para diagramas UML, firmas de métodos y fragmentos de código.

* **Animaciones y Revelado Escalonado:**
  - Clases `.reveal` con curvas bezier cúbicas suaves: `cubic-bezier(0.16, 1, 0.3, 1)`.
  - Retardo escalonado por elemento (`transition-delay: 0.1s, 0.2s, 0.3s...`) para acompañar la oratoria de cada estudiante sin saturar visualmente.

---

### 3.3 Catálogo y Maquetación de las 13 Diapositivas

```
+---------------------------------------------------------------------------------------------------+
| #  | Título Diapositiva                       | Orador     | Contenido Visual Clave               |
+----+------------------------------------------+------------+--------------------------------------+
| 01 | Portada Ejecutiva FitManage Pro          | Juan       | Logo tipográfico, 6 tarjetas equipo  |
| 02 | Dominio y Arquitectura 4 Capas           | Juan       | Diagrama horizontal de capas         |
| 03 | DIAGRAMA DE CLASES UML INTEGRAL (SVG)    | Juan/Todos | SVG vectorial 1920x1080 interactivo  |
| 04 | Herencia & Abstracción: Persona          | Juan       | Jerarquía Persona, Socio, Instructor |
| 05 | Composición Fuerte: Rutina y Ejercicio   | Rocio      | Rombo negro, fórmula de tonelaje     |
| 06 | Agregación Débil y CupoAgotadoException  | Merlina    | Rombo blanco, aforo y excepción      |
| 07 | Facturable y Descuento Antigüedad (10%)  | Alfonsina  | Interfaz, liquidación y recibo       |
| 08 | GUI Swing: CardLayout y Badges           | Sara       | Captura de UI, paleta ThemeConfig    |
| 09 | Molinete en Vivo: Check-In Dialog        | Sara       | Flujo semafórico de molinete         |
| 10 | Persistencia Atómica en JSON             | Joel       | Motor nativo, .tmp + ATOMIC_MOVE     |
| 11 | Calidad de Software: Tests y Graphify    | Joel       | Badge 7/7 tests, grafo 0 ciclos      |
| 12 | Trazabilidad del Equipo & Defensa POO    | Equipo     | Matriz colaborativa y preguntas clave|
| 13 | Conclusiones y Demostración en Vivo      | Equipo     | Logros, enlaces y llamado a preguntas|
+---------------------------------------------------------------------------------------------------+
```

---

### 3.4 Especificación Técnica de la Slide 03: Diagrama UML Gráfico (SVG Nativo)

La **Slide 03** es el componente visual central de la defensa. Se construirá como un **SVG vectorial puro embebido directamente en el HTML**, con las siguientes características técnicas:

1. **Resolución y Escalado:**
   - Atributos: `viewBox="0 0 1800 920"` preservando la relación de aspecto sin distorsión.
   - Posicionamiento dentro de la diapositiva en un canvas oscuro de alto contraste con grilla de fondo milimétrica sutil (`pattern` SVG).
2. **Cromática Semántica por Estereotipo:**
   - `<<interface>>` (`Facturable`, `Autenticable`, `Reportable`): Encabezado en Cyan Eléctrico (`#06b6d4`), fondo `#083344`, borde `#0e7490`.
   - `<<abstract>>` (`Persona`): Encabezado en Violeta Púrpura (`#a855f7`), fondo `#3b0764`, borde `#7e22ce`.
   - `<<class>>` (`Socio`, `Instructor`, `Rutina`, `Ejercicio`, `ClaseGrupal`, `Membresia`, `Pago`, `Asistencia`): Encabezado en Azul Corporativo (`#2563eb`), fondo `#1e293b`, borde `#3b82f6`.
   - `<<enumeration>>` (`EstadoCuota`, `TipoMembresia`, `Disciplina`, etc.): Encabezado en Verde Esmeralda (`#10b981`), fondo `#064e3b`, borde `#059669`.
   - `<<exception>>` (`CupoAgotadoException`): Encabezado en Rojo Coral (`#f43f5e`), fondo `#4c0519`, borde `#e11d48`.
3. **Simbología Formal Estándar de Conectores UML:**
   - **Herencia (Generalización):** Línea continua sólida con punta triangular hueca cerrada (`marker-end="url(#triangle-hollow)"`).
   - **Realización de Interfaz:** Línea discontinua (`stroke-dasharray="6,4"`) con punta triangular hueca (`marker-end="url(#triangle-hollow)"`).
   - **Composición Fuerte:** Línea continua con rombo relleno sólido negro/azul en el extremo del todo (`marker-start="url(#diamond-filled)"`).
   - **Agregación Débil:** Línea continua con rombo hueco blanco/transparente en el extremo del contenedor (`marker-start="url(#diamond-hollow)"`).
   - **Asociación Dirigida:** Línea continua con flecha abierta (`marker-end="url(#arrow-open)"`).
   - **Dependencia / Lanza Excepción:** Línea punteada con flecha abierta (`marker-end="url(#arrow-open-dashed)"`).
4. **Zonificación Espacial del Diagrama:**
   - **Banda Superior:** Interfaces de Contrato (`Facturable`, `Autenticable`, `Reportable`).
   - **Zona Central Izquierda:** Jerarquía de Personas (`Persona` abstracta arriba, `Socio` e `Instructor` abajo).
   - **Zona Central Media:** Dominio de Asistencia y Facturación (`Asistencia`, `Membresia`, `Pago`).
   - **Zona Central Derecha:** Núcleo de Entrenamiento (`Rutina *-- Ejercicio`).
   - **Zona Inferior Derecha:** Módulo Logístico (`ClaseGrupal o-- Instructor`, `ClaseGrupal o-- Socio` y `CupoAgotadoException`).
   - **Pie de Slide:** Micro-leyenda cromática y gráfica explicativa para guiar visualmente al jurado docente.

---

## 4. PIPELINE DE GENERACIÓN Y EXPORTACIÓN A PDF CON GOOGLE CHROME HEADLESS

### 4.1 Estrategia del Artefacto 1: `guion_presentacion.md` $\longrightarrow$ `guion_presentacion.pdf`
Para garantizar que el guion pueda imprimirse o distribuirse a los docentes en un formato PDF de maquetación editorial impecable, se implementa una cadena de procesamiento automatizada:

```
+------------------------+      +---------------------------+      +--------------------------+
| guion_presentacion.md  | ---> | scripts/generate_speech_  | ---> | guion_presentacion.html  |
| (Texto, Tiempos, Q&A)  |      | pdf.py (Plantilla CSS)    |      | (Estilizado para Print)  |
+------------------------+      +---------------------------+      +--------------------------+
                                                                                 |
                                                                                 v
                                                                   +--------------------------+
                                                                   | Google Chrome Headless   |
                                                                   | --print-to-pdf           |
                                                                   +--------------------------+
                                                                                 |
                                                                                 v
                                                                   +--------------------------+
                                                                   | guion_presentacion.pdf   |
                                                                   | (Entregable Imprimible)  |
                                                                   +--------------------------+
```

#### Parámetros del Convertidor `scripts/generate_speech_pdf.py`:
1. Utiliza Python 3 con el módulo `markdown` nativo (`/usr/lib/python3/dist-packages/markdown`).
2. Aplica un template HTML con hoja de estilo específica `@media print`:
   - Tipografía serif académica pulida (`Georgia`, `Merriweather` o `DejaVu Serif`).
   - Reglas de salto de página estrictas: `page-break-before: always;` al inicio de cada integrante para que cada uno posea su hoja de oratoria independiente.
   - Recuadros distintivos con bordes de color para:
     - ⏱️ **Cronometraje y Acotación Escénica** (fondo gris suave con badge temporal).
     - 🎯 **Objetivo POO y Código** (cajas técnicas en monoespaciado).
     - 🛡️ **Defensa Docente / Preguntas Trampa** (recuadro destacado con fondo marfil y borde ámbar).
   - Numeración de páginas dinámica al pie: `"Página X de Y - FitManage Pro (POO 2026)"`.
3. Invocación de Google Chrome Headless comprobado en el sistema:
   ```bash
   google-chrome --headless=new \
     --disable-gpu \
     --no-sandbox \
     --print-to-pdf="guion_presentacion.pdf" \
     --print-to-pdf-margins=0.4 \
     --no-pdf-header-footer \
     "file://$(pwd)/guion_presentacion.html"
   ```
4. Control de calidad post-compilación:
   - Verificación de código de retorno `$? == 0`.
   - Confirmación de tamaño de archivo (`ls -lh guion_presentacion.pdf` > 50 KB).

---

### 4.2 Estrategia del Artefacto 2: `presentacion_fitmanage.html`
1. Creación directa del archivo interactivo en la raíz del proyecto.
2. Comprobación de integridad mediante ejecución de headless Chrome para renderizar capturas de pantalla de validación a 1920×1080:
   ```bash
   google-chrome --headless=new \
     --screenshot="slide_preview_check.png" \
     --window-size=1920,1080 \
     "file://$(pwd)/presentacion_fitmanage.html"
   ```
3. Opción de exportación de las diapositivas completas a PDF de diapositivas mediante el script de la skill `bash ~/.agents/skills/frontend-slides/scripts/export-pdf.sh presentacion_fitmanage.html presentacion_slides.pdf`.

---

## 5. PIPELINE DE AUTO-AUDITORÍA Y VERIFICACIÓN CONTINUA

Antes de considerar ambos entregables finalizados, se aplicará una batería de comprobaciones automáticas y manuales estructuradas en una **Matriz de Control de Calidad**:

### 5.1 Checklist del Guion Oral (`guion_presentacion.md` y `.pdf`)
- [ ] **Auditoría de Tiempo:** La suma de tiempos netos de los 6 estudiantes se ubica entre 18:00 y 19:30 min.
- [ ] **Auditoría de Equidad:** Ningún integrante habla menos de 2m 45s ni más de 3m 30s.
- [ ] **Auditoría Conceptual UML:** Se explican explícitamente:
  - [ ] Clase abstracta vs concreta (`Persona`).
  - [ ] Atributos `# protected` y encapsulamiento.
  - [ ] Métodos abstractos y polimorfismo dinámico (`mostrarDetalle()`, `validarIngreso()`).
  - [ ] Composición fuerte `*--` (`Rutina` - `Ejercicio`) y cálculo de tonelaje.
  - [ ] Agregación débil `o--` (`ClaseGrupal` - `Instructor` / `Socio`).
  - [ ] Excepción personalizada de negocio (`CupoAgotadoException`).
  - [ ] Interfaz `Facturable` y regla del 10% de antigüedad.
  - [ ] Interfaz gráfica Swing con `CardLayout` y badges de estado.
  - [ ] Persistencia atómica en JSON (`.tmp` + `ATOMIC_MOVE`).
  - [ ] Suite de 7 pruebas unitarias sin frameworks externos.
- [ ] **Auditoría de Defensa Docente:** Cada expositor tiene al menos una pregunta trampa con respuesta técnica justificada.
- [ ] **Compilación PDF:** El archivo `guion_presentacion.pdf` se genera sin alertas ni desbordes de página.

### 5.2 Checklist del Slideshow (`presentacion_fitmanage.html`)
- [ ] **Fixed Stage 16:9:** El canvas interior mide exactamente 1920×1080 px y escala sin romperse ni generar scrollbars.
- [ ] **Zero Dependencies:** No se requieren llamados a CDNs bloqueantes ni paquetes npm; funciona 100% offline o en red académica local.
- [ ] **Visibilidad Robusta:** Las transiciones operan con `.slide.active` y opacidades, sin selectores destructivos `display: none`.
- [ ] **Navegación:** Operan las teclas `Flecha Derecha`, `Flecha Izquierda`, `Barra Espaciadora`, `AvPág`, `RePág` y clicks en botones de control.
- [ ] **Slide UML (SVG):** El Diagrama de Clases UML se renderiza completo, con todas las entidades legibles, tipos de línea diferenciados y leyenda al pie.
- [ ] **Estética No-Slop:** Fondos oscuros con atmósfera slate/azul, tarjetas de cristal translúcido, sin gradientes púrpuras genéricos ni tipografía de sistema aburrida.
- [ ] **Inline Editing:** Al pulsar `E` o pasar el ratón por la esquina superior izquierda se despliega el botón de edición.

---

## 6. CRITERIOS DE ACEPTACIÓN PARA EL VISTO BUENO DEL COORDINADOR

Para que el coordinador de orquestación emita la aprobación formal del plan y autorice el inicio de la fase de construcción de los archivos, se establecen los siguientes criterios mandatorios:

1. **Alineación Total de Roles:** Los 6 estudiantes (Juan, Rocio, Merlina, Alfonsina, Sara, Joel) están asignados exactamente a los módulos que programaron en el repositorio, reflejados en `UML_DIAGRAM.md` y `README.md`.
2. **Definición Clara del Diagrama SVG:** La Slide 03 contiene la especificación explícita de diagramado vectorial para garantizar que el jurado pueda leer los métodos y atributos sin recurrir a zoom externo.
3. **Factibilidad del Pipeline PDF:** El script de compilación utiliza herramientas nativas del entorno Linux disponible (`python3-markdown` y `google-chrome --headless`), sin requerir librerías pesadas no instaladas como Pandoc o LaTeX.
4. **Estructura Modular del Plan:** El documento `PLAN_PRESENTACION_SLIDES.md` queda persistido en el root del repositorio, versionado bajo Git, listo para ser auditado por cualquier agente supervisor.

---

## 7. CRONOGRAMA DE EJECUCIÓN PROPUESTO

Una vez recibido el visto bueno del coordinador, la ejecución se dividirá en dos fases consecutivas inmediatas:

* **Fase 1: Redacción del Guion y Compilación PDF**
  - Generación de `guion_presentacion.md` con las 6 secciones, tiempos y preguntas de defensa.
  - Implementación del script `scripts/generate_speech_pdf.py`.
  - Compilación de `guion_presentacion.pdf` con Chrome headless y verificación visual.
* **Fase 2: Construcción y Verificación del Slideshow HTML**
  - Generación del archivo standalone `presentacion_fitmanage.html`.
  - Construcción del diagrama de clases SVG completo en la Slide 03.
  - Incorporación del controlador JS de escala 16:9, atajos de teclado y modo edición.
  - Verificación mediante captura headless a 1920×1080 y reporte de auto-auditoría.
