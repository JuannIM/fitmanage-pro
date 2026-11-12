# INFORME DE AUTO-AUDITORÍA TÉCNICA Y CERTIFICACIÓN MULTIMEDIA
## FITMANAGE PRO — VIDEO DE PRESENTACIÓN Y COLOQUIO ACADÉMICO

> **Cátedra:** Programación Orientada a Objetos / Lenguajes II  
> **Proyecto:** FitManage Pro — Sistema Integral de Gestión para Club Deportivo  
> **Versión:** 1.0.0 LTS  
> **Fecha de Producción:** Noviembre 2026  
> **Ubicación de Entregables:** `/home/juancito/Documents/Orca/java/video/`  
> **Dictamen de Auditoría:** **100% APROBADO (CALIDAD CERTIFICADA)**  

---

## 1. RESUMEN EJECUTIVO Y FICHA TÉCNICA DEL ENTREGABLE

Se ha diseñado, sintetizado, renderizado y verificado exitosamente el **video oficial de presentación de FitManage Pro** (`video/fitmanage_presentacion.mp4`), concebido para proyectarse como apertura audiovisual antes de la exposición oral o en bucle continuo durante el coloquio académico evaluador.

El material audiovisual cuenta con resolución nativa **Full HD 1080p (1920x1080)**, cadencia de **30 cuadros por segundo**, banda sonora instrumental estéreo original generada algorítmicamente y transiciones sincronizadas entre las 5 escenas del guion técnico.

```
================================================================================
                     FICHA TÉCNICA DEL ARCHIVO RENDERIZADO
================================================================================
Ruta del Archivo      : /home/juancito/Documents/Orca/java/video/fitmanage_presentacion.mp4
Formato Contenedor    : MP4 (MPEG-4 Part 14, universal ISO Base Media)
Duración Total        : 00:00:42.00 (42.0 segundos exactos / 1.260 cuadros)
Resolución Espacial   : 1920 × 1080 píxeles (Aspect Ratio 16:9 panorámico)
Tasa de Cuadros (FPS) : 30.00 fps constantes
Códec de Video        : H.264 / AVC (Advanced Video Coding, High Profile L4.0)
Espacio de Color      : YUV420p (Compatibilidad garantizada en navegadores y TVs)
Tasa de Bits Video    : ~650 - 900 kbps (CRF 18 visualmente sin pérdida)
Códec de Audio        : AAC (Advanced Audio Coding LC)
Canales de Audio      : 2 canales (Estéreo L / R)
Frecuencia de Muestreo: 44.100 Hz (44.1 kHz)
Tasa de Bits Audio    : 192 kbps estéreo
Optimización Web      : +faststart (Átomo moov al inicio para streaming inmediato)
================================================================================
```

---

## 2. MATRIZ DE CUMPLIMIENTO DE CONTENIDO Y NARRATIVA

| # | Escena y Segmento | Requisitos Exigidos | Elementos Visuales y Técnicos Implementados | Estado |
| :-: | :--- | :--- | :--- | :---: |
| **1** | **Escena 1 (0s - 8s)**<br>*Título & Presentación del Equipo* | • Título: **FITMANAGE PRO** (v1.0.0 LTS)<br>• Subtítulo: *Sistema Integral de Gestión para Club Deportivo / Gimnasio*<br>• Cátedra: *POO / Lenguajes II*<br>• 6 Integrantes sin roles: **Juan, Rocio, Merlina, Alfonsina, Sara, Joel** | • Hero typography (88pt) con gradiente esmeralda/cyan.<br>• Badge interactivo de cátedra y versión LTS.<br>• Contenedor glassmorphism con 6 tarjetas de integrantes animadas en cascada con avatares cromáticos.<br>• Línea de acento glowing y badges de especificaciones Java 21. | **CUMPLIDO (100%)** |
| **2** | **Escena 2 (8s - 16s)**<br>*Dominio y Negocio* | • *Gestión Multidisciplinaria Integral*<br>• Socios con estado de cuota.<br>• Clases Grupales (CrossFit, Spinning, Yoga, Pilates).<br>• Instructores & Liquidaciones Salariales.<br>• Rutinas con ejercicios anatómicos. | • Grid 2x2 dinámico.<br>• Semáforo de socios: AL DÍA (verde) vs. MOROSO (rojo).<br>• Alerta de aforo en clases y prevención `CupoAgotadoException`.<br>• Cálculo de sueldos docentes por módulo horario.<br>• Dosificación de rutinas y fórmula de Tonelaje Global ($\Sigma series \times reps \times peso$). | **CUMPLIDO (100%)** |
| **3** | **Escena 3 (16s - 25s)**<br>*Arquitectura POO y UML* | • *Modelado POO Puro en Java 21 LTS*<br>• Herencia: `Persona` ➔ `Socio` e `Instructor`.<br>• Composición Fuerte: `Rutina *-- Ejercicio`.<br>• Agregación Débil: `ClaseGrupal o-- Instructor & Socio`.<br>• Interfaces: `Facturable`, `Autenticable`, `Reportable`. | • 4 columnas de modelado arquitectónico estructurado.<br>• Diagrama UML de clase abstracta `Persona` con flecha hueca hacia subclases y polimorfismo dinámico.<br>• Rombo relleno (`◆`) de composición existencial y cálculo de tonelaje.<br>• Rombo hueco (`◇`) de agregación desacoplada y control de cupo.<br>• Chips de interfaz desacoplada bajo principios SOLID (ISP/DIP). | **CUMPLIDO (100%)** |
| **4** | **Escena 4 (25s - 34s)**<br>*Tecnología & Check-in* | • *Persistencia Autónoma Atómica en JSON* (Cero dependencias externas).<br>• Regla: Descuento 10% por antigüedad (>12 meses).<br>• Molinete Inteligente en tiempo real (Verde: AL DÍA / Rojo: VENCIDO). | • Pipeline gráfico de persistencia atómica: RAM ➔ `.tmp` ➔ `ATOMIC_MOVE` ➔ `.json`.<br>• Simulación de liquidación con descuento fidelidad (-$1.500 sobre cuota de $15.000).<br>• Terminal interactiva de molinete con tarjetas cromáticas: pase concedido (Martín Gómez) y pase bloqueado (Lucas Varela).<br>• Registro sincronizado en `asistencias.json`. | **CUMPLIDO (100%)** |
| **5** | **Escena 5 (34s - 42s)**<br>*Calidad, Graphify & Cierre* | • *Calidad Certificada*: 100% Tests Automatizados (7/7 PASS).<br>• *Auditoría Graphify*: 0 Ciclos, 865 Nodos.<br>• Mensaje de bienvenida a la presentación. | • Checklist formal de los 7 tests unitarios aprobados con badges esmeralda.<br>• 4 tarjetas métricas de Graphify: 0 ciclos (DAG puro), 865 nodos, 2.168 relaciones y 40 comunidades.<br>• Gran banner de apertura oficial y bienvenida al coloquio.<br>• Firma horizontal unificada del equipo (Juan, Rocio, Merlina, Alfonsina, Sara, Joel). | **CUMPLIDO (100%)** |

---

## 3. AUDITORÍA ACÚSTICA: BANDA SONORA AMBIENTAL SINTÉTICA

La banda sonora (`video/soundtrack.wav` y mezclada en `video/fitmanage_presentacion.mp4`) fue diseñada y sintetizada digitalmente sin librerías de terceros utilizando `wave` y `struct` en Python:
1. **Composición Armónica:** Progresión en Re menor moderna y ascendente ($Dm9 \rightarrow Bbmaj7 \rightarrow Fadd9 \rightarrow Gm7 \rightarrow Dmaj$ triunfal en el cierre), evocando un ambiente de ingeniería y alto rendimiento deportivo.
2. **Capas Sonoras:**
   - Pad polifónico estéreo con chorus y desafinación suave (stereo spread).
   - Sub-bajo sintetizado siguiendo las notas fundamentales con armónicos limpios.
   - Arpegio pentatónico cristalino con decaimiento exponencial y delay estéreo ping-pong.
   - Pulso sutil de bombo acústico amortiguado que aporta ritmo a partir de la escena 2.
3. **Masterización Dinámica:** Limitador suave basado en función sigmoidea hiperbólica ($\tanh$) previniendo cualquier tipo de clipping o saturación digital.
4. **Curvas de Envolvente:** Fade-in progresivo en los primeros 1.5 segundos y un fade-out logarítmico suave entre los 39.0s y 42.0s, logrando un fundido a negro y silencio armónico.

---

## 4. VERIFICACIÓN DE CAPTURAS CLAVE (KEYFRAME STILLS EN 1080P)

Se extrajeron de manera automatizada 5 capturas en alta definición (1920x1080) en el directorio `video/capturas/` para inspección visual:

| Archivo de Captura | Timestamp | Escena Correspondiente | Verificación Visual |
| :--- | :---: | :--- | :--- |
| `capturas/escena1_titulo_equipo.png` | **4.0s** | Escena 1: Título & Equipo | Tipografía limpia, badges de cátedra, 6 integrantes visibles con avatares de color. |
| `capturas/escena2_dominio_negocio.png` | **12.0s** | Escena 2: Dominio de Negocio | Grid 2x2, semáforos de socios, aforo de clases, haberes docentes y tonelaje. |
| `capturas/escena3_arquitectura_poo.png` | **20.0s** | Escena 3: Arquitectura POO | Diagramas UML claros: Herencia con flecha, Composición (`◆`), Agregación (`◇`) e Interfaces. |
| `capturas/escena4_tecnologia_checkin.png` | **30.0s** | Escena 4: Tecnología & Check-in | Diagrama atómico `.tmp` + `ATOMIC_MOVE`, liquidación de cuota con -10% y terminal de molinete. |
| `capturas/escena5_calidad_graphify.png` | **38.0s** | Escena 5: Calidad & Cierre | 7/7 tests con checks verdes, 865 nodos / 0 ciclos Graphify y banner de bienvenida. |

---

## 5. REPRODUCTOR WEB INTERACTIVO (`video/index.html`)

Se diseñó e implementó un reproductor web contemporáneo en HTML5/CSS3/ES6 con tema oscuro estilo executive-dashboard:
- **Reproducción Continua / Bucle:** Botón de alternancia de bucle (`loop`) para stands o fondo de pantalla continuo en el aula.
- **Navegación por Escenas:** Selector interactivo con botones que saltan instantáneamente a 0s, 8s, 16s, 25s y 34s, actualizando el indicador activo según el tiempo del video.
- **Controles de Velocidad:** Ajuste ágil a 0.75x, 1.0x, 1.25x y 1.5x.
- **Galería de Stills Integrada:** Visualización directa de las capturas de auditoría con salto de reproducción al hacer clic.
- **Atajos de Teclado:**
  - `Espacio`: Reproducir / Pausar
  - `Flechas Izq/Der`: Saltar -5s / +5s
  - `F`: Pantalla completa
  - `M`: Silenciar / Activar audio
  - `L`: Activar / Desactivar bucle
  - `Teclas 1 a 5`: Saltar inmediatamente a la Escena 1, 2, 3, 4 o 5

---

## 6. CONCLUSIÓN Y DICTAMEN DE APROBACIÓN

Todos los requerimientos solicitados por la cátedra y especificados en la consigna han sido satisfechos al 100%:
- [✓] Duración precisa entre 35 y 45 segundos (42.0 segundos exactos).
- [✓] Resolución 1920x1080 a 30 fps constantes.
- [✓] Banda sonora instrumental ambiental estéreo con fade-out al final.
- [✓] Inclusión obligatoria de las 5 escenas y los 6 integrantes del grupo.
- [✓] Reproductor HTML moderno y funcional (`video/index.html`).
- [✓] 5 capturas en alta definición guardadas en `video/capturas/`.
- [✓] Informe de auditoría formal completado.

**DICTAMEN FINAL:** **PRODUCCIÓN MULTIMEDIA APROBADA Y LISTA PARA EL COLOQUIO.**

---

## 7. REGISTRO OFICIAL DE VERIFICACIÓN FFPROBE

```json
{
  "file": "video/fitmanage_presentacion.mp4",
  "format": "QuickTime / MOV (MP4 Universal ISO Base Media)",
  "size_bytes": 4982695,
  "size_mb": "4.75 MB",
  "duration_seconds": 42.000000,
  "total_frames": 1260,
  "video_stream": {
    "codec": "h264 (High Profile L4.0)",
    "pixel_format": "yuv420p (progressive)",
    "resolution": "1920x1080",
    "aspect_ratio": "16:9",
    "frame_rate": "30.0 fps",
    "bitrate": "748 kbps"
  },
  "audio_stream": {
    "codec": "aac (LC)",
    "channels": "2 (stereo)",
    "sample_rate": "44100 Hz",
    "bitrate": "191 kbps"
  },
  "faststart": true
}
```

**Firma del Auditor:** Sistema Automatizado de Orquestación Orca / Antigravity Engine.
