import cairo
import math
import subprocess
import os
import sys
import time

WIDTH = 1920
HEIGHT = 1080
FPS = 30
DURATION = 42.0
TOTAL_FRAMES = int(FPS * DURATION)

# Color Palette Definitions
C_BG_TOP = (0.04, 0.07, 0.14)
C_BG_BOT = (0.01, 0.02, 0.05)
C_EMERALD = (0.06, 0.72, 0.50)
C_CYAN = (0.02, 0.71, 0.83)
C_BLUE = (0.23, 0.51, 0.96)
C_PURPLE = (0.55, 0.36, 0.96)
C_ROSE = (0.94, 0.27, 0.40)
C_AMBER = (0.96, 0.62, 0.04)
C_TEXT_WHITE = (0.98, 0.98, 1.0)
C_TEXT_MUTED = (0.58, 0.64, 0.72)
C_TEXT_DIM = (0.40, 0.45, 0.53)
C_CARD_BG = (0.07, 0.10, 0.18, 0.88)
C_CARD_BORDER = (0.18, 0.25, 0.38, 0.65)

# Background ambient particles
PARTICLES = []
for p in range(40):
    PARTICLES.append({
        'x': (p * 47) % WIDTH,
        'y': (p * 29) % HEIGHT,
        'vx': ((p % 5) - 2) * 8.0,
        'vy': -15.0 - (p % 4) * 8.0,
        'r': 1.5 + (p % 3) * 1.5,
        'alpha': 0.15 + (p % 5) * 0.08,
        'color': C_CYAN if p % 2 == 0 else C_EMERALD
    })

def draw_rounded_rect(ctx, x, y, w, h, r):
    ctx.new_sub_path()
    ctx.arc(x + w - r, y + r, r, -math.pi/2, 0)
    ctx.arc(x + w - r, y + h - r, r, 0, math.pi/2)
    ctx.arc(x + r, y + h - r, r, math.pi/2, math.pi)
    ctx.arc(x + r, y + r, r, math.pi, 3*math.pi/2)
    ctx.close_path()

def draw_card(ctx, x, y, w, h, r=14, bg_rgba=C_CARD_BG, border_rgba=C_CARD_BORDER, glow=False, glow_color=C_CYAN):
    if glow:
        # Subtle glow
        draw_rounded_rect(ctx, x - 3, y - 3, w + 6, h + 6, r + 3)
        ctx.set_source_rgba(glow_color[0], glow_color[1], glow_color[2], 0.25)
        ctx.fill()
        
    draw_rounded_rect(ctx, x, y, w, h, r)
    ctx.set_source_rgba(*bg_rgba)
    ctx.fill_preserve()
    ctx.set_source_rgba(*border_rgba)
    ctx.set_line_width(1.5)
    ctx.stroke()

def draw_badge(ctx, x, y, text, font_size=13, text_color=(1,1,1), bg_color=C_EMERALD, r=6, border=None):
    ctx.select_font_face('Liberation Sans', cairo.FONT_SLANT_NORMAL, cairo.FONT_WEIGHT_BOLD)
    ctx.set_font_size(font_size)
    ext = ctx.text_extents(text)
    pad_h = 12
    pad_v = 6
    bw = ext.width + pad_h * 2
    bh = ext.height + pad_v * 2
    draw_rounded_rect(ctx, x, y - ext.height - pad_v, bw, bh, r)
    ctx.set_source_rgba(*bg_color)
    ctx.fill_preserve()
    if border:
        ctx.set_source_rgba(*border)
        ctx.set_line_width(1.2)
        ctx.stroke()
    else:
        ctx.new_path()
    ctx.set_source_rgb(*text_color)
    ctx.move_to(x + pad_h, y)
    ctx.show_text(text)
    return bw

def draw_text(ctx, x, y, text, size=18, color=C_TEXT_WHITE, bold=False, align="left", font="Liberation Sans"):
    weight = cairo.FONT_WEIGHT_BOLD if bold else cairo.FONT_WEIGHT_NORMAL
    ctx.select_font_face(font, cairo.FONT_SLANT_NORMAL, weight)
    ctx.set_font_size(size)
    ext = ctx.text_extents(text)
    tx = x
    if align == "center":
        tx = x - ext.width / 2.0
    elif align == "right":
        tx = x - ext.width
    ctx.set_source_rgb(*color)
    ctx.move_to(tx, y)
    ctx.show_text(text)
    return ext.width

def draw_background(ctx, t):
    # Base gradient
    pat = cairo.LinearGradient(0, 0, WIDTH, HEIGHT)
    pat.add_color_stop_rgb(0, *C_BG_TOP)
    pat.add_color_stop_rgb(1, *C_BG_BOT)
    ctx.set_source(pat)
    ctx.paint()

    # Radial ambient glow center-top
    rad = cairo.RadialGradient(WIDTH/2, 220, 50, WIDTH/2, 220, 850)
    rad.add_color_stop_rgba(0, 0.05, 0.45, 0.40, 0.18)
    rad.add_color_stop_rgba(1, 0.0, 0.0, 0.0, 0.0)
    ctx.set_source(rad)
    ctx.paint()

    # Subtle tech grid
    ctx.set_source_rgba(0.12, 0.18, 0.28, 0.18)
    ctx.set_line_width(1.0)
    grid_spacing = 80
    for gx in range(0, WIDTH, grid_spacing):
        ctx.move_to(gx, 0)
        ctx.line_to(gx, HEIGHT)
    for gy in range(0, HEIGHT, grid_spacing):
        ctx.move_to(0, gy)
        ctx.line_to(WIDTH, gy)
    ctx.stroke()

    # Ambient floating particles
    for p in PARTICLES:
        px = (p['x'] + p['vx'] * t) % WIDTH
        py = (p['y'] + p['vy'] * t) % HEIGHT
        ctx.arc(px, py, p['r'], 0, 2 * math.pi)
        ctx.set_source_rgba(p['color'][0], p['color'][1], p['color'][2], p['alpha'])
        ctx.fill()

def draw_global_chrome(ctx, t, current_scene_idx):
    # Top status bar
    ctx.set_source_rgba(0.04, 0.06, 0.11, 0.90)
    ctx.rectangle(0, 0, WIDTH, 54)
    ctx.fill()
    ctx.set_source_rgba(0.18, 0.25, 0.38, 0.60)
    ctx.set_line_width(1.0)
    ctx.move_to(0, 54)
    ctx.line_to(WIDTH, 54)
    ctx.stroke()

    # Brand Title Left
    draw_badge(ctx, 30, 36, "FITMANAGE PRO", font_size=13, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.8))
    draw_text(ctx, 190, 36, "Java 21 LTS SE • Arquitectura POO", size=14, color=C_TEXT_MUTED, bold=False)

    # Scene Breadcrumb Pills Center
    scenes = [
        ("01", "TÍTULO & EQUIPO"),
        ("02", "DOMINIO NEGOCIO"),
        ("03", "ARQUITECTURA POO"),
        ("04", "TECNOLOGÍA & CHECK-IN"),
        ("05", "CALIDAD & GRAPHIFY")
    ]
    start_x = 520
    for idx, (num, name) in enumerate(scenes):
        is_active = (idx == current_scene_idx)
        bg = (0.06, 0.72, 0.50, 0.35) if is_active else (0.1, 0.14, 0.22, 0.5)
        tc = C_TEXT_WHITE if is_active else C_TEXT_DIM
        border = (0.06, 0.72, 0.50, 0.9) if is_active else (0.18, 0.25, 0.38, 0.3)
        lbl = f"{num} {name}"
        draw_badge(ctx, start_x, 36, lbl, font_size=11, bg_color=bg, text_color=tc, border=border, r=12)
        start_x += 185

    # Right live clock & specs
    mins = int(t) // 60
    secs = int(t) % 60
    timecode = f"{mins:02d}:{secs:02d} / 00:42"
    draw_text(ctx, WIDTH - 220, 36, timecode, size=14, color=C_CYAN, bold=True)
    draw_badge(ctx, WIDTH - 100, 36, "FHD 1080p", font_size=11, bg_color=(0.15, 0.22, 0.35, 0.8), text_color=C_TEXT_MUTED)

    # Bottom Progress Bar
    bar_y = HEIGHT - 8
    ctx.set_source_rgba(0.08, 0.12, 0.20, 0.9)
    ctx.rectangle(0, bar_y, WIDTH, 8)
    ctx.fill()

    progress_w = max(4.0, (t / DURATION) * WIDTH)
    pat = cairo.LinearGradient(0, bar_y, progress_w, bar_y)
    pat.add_color_stop_rgb(0, *C_EMERALD)
    pat.add_color_stop_rgb(1, *C_CYAN)
    ctx.set_source(pat)
    ctx.rectangle(0, bar_y, progress_w, 8)
    ctx.fill()

    # Progress glow indicator
    ctx.arc(progress_w, bar_y + 4, 6, 0, 2 * math.pi)
    ctx.set_source_rgba(0.3, 0.9, 1.0, 0.85)
    ctx.fill()

    # Scene boundary tick marks
    ticks = [8.0, 16.0, 25.0, 34.0]
    ctx.set_source_rgba(0.4, 0.5, 0.65, 0.6)
    ctx.set_line_width(2.0)
    for tick in ticks:
        tx = (tick / DURATION) * WIDTH
        ctx.move_to(tx, bar_y - 3)
        ctx.line_to(tx, HEIGHT)
        ctx.stroke()

def get_scene_alpha(t, start_t, end_t, fade_in=0.6, fade_out=0.6):
    local_t = t - start_t
    scene_dur = end_t - start_t
    if local_t < fade_in:
        return max(0.0, min(1.0, local_t / fade_in))
    elif local_t > (scene_dur - fade_out):
        return max(0.0, min(1.0, (scene_dur - local_t) / fade_out))
    return 1.0

# ==========================================
# SCENE 1: TÍTULO & PRESENTACIÓN DEL EQUIPO
# ==========================================
def render_scene_1(ctx, t):
    alpha = get_scene_alpha(t, 0.0, 8.0, fade_in=0.5, fade_out=0.7)
    ctx.save()
    ctx.push_group()

    # Top Tag
    draw_badge(ctx, WIDTH/2 - 270, 130, "CÁTEDRA: PROGRAMACIÓN ORIENTADA A OBJETOS / LENGUAJES II", font_size=13,
               bg_color=(0.12, 0.20, 0.35, 0.85), text_color=C_CYAN, border=(0.02, 0.71, 0.83, 0.5), r=8)

    # Hero Main Title
    title = "FITMANAGE PRO"
    draw_text(ctx, WIDTH/2, 235, title, size=88, color=C_TEXT_WHITE, bold=True, align="center")

    # Version pill next to title
    draw_badge(ctx, WIDTH/2 + 370, 200, "v1.0.0 LTS", font_size=15,
               bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.8), r=12)

    # Subtitle
    sub = "Sistema Integral de Gestión para Club Deportivo / Gimnasio"
    draw_text(ctx, WIDTH/2, 290, sub, size=28, color=(0.80, 0.86, 0.94), bold=False, align="center")

    # Glowing Accent Rule
    pat_line = cairo.LinearGradient(WIDTH/2 - 400, 320, WIDTH/2 + 400, 320)
    pat_line.add_color_stop_rgba(0, 0.06, 0.72, 0.50, 0.0)
    pat_line.add_color_stop_rgba(0.5, 0.02, 0.71, 0.83, 0.9)
    pat_line.add_color_stop_rgba(1, 0.06, 0.72, 0.50, 0.0)
    ctx.set_source(pat_line)
    ctx.set_line_width(2.5)
    ctx.move_to(WIDTH/2 - 400, 320)
    ctx.line_to(WIDTH/2 + 400, 320)
    ctx.stroke()

    # Team Presentation Box
    box_w = 1500
    box_h = 390
    box_x = (WIDTH - box_w) / 2
    box_y = 350
    draw_card(ctx, box_x, box_y, box_w, box_h, r=20, bg_rgba=(0.06, 0.09, 0.16, 0.92), border_rgba=(0.20, 0.30, 0.46, 0.7))

    # Team Header
    draw_text(ctx, WIDTH/2, box_y + 48, "EQUIPO DE DESARROLLO (INTEGRACIÓN MULTIDISCIPLINARIA)", size=20, color=C_CYAN, bold=True, align="center")
    draw_text(ctx, WIDTH/2, box_y + 78, "Simulación colaborativa de 6 integrantes sin jerarquías rígidas", size=15, color=C_TEXT_MUTED, align="center")

    # 6 Team Members
    members = [
        {"name": "Juan", "init": "J", "color": C_EMERALD, "accent": (0.06, 0.72, 0.50, 0.3)},
        {"name": "Rocio", "init": "R", "color": C_PURPLE, "accent": (0.55, 0.36, 0.96, 0.3)},
        {"name": "Merlina", "init": "M", "color": C_CYAN, "accent": (0.02, 0.71, 0.83, 0.3)},
        {"name": "Alfonsina", "init": "A", "color": C_AMBER, "accent": (0.96, 0.62, 0.04, 0.3)},
        {"name": "Sara", "init": "S", "color": C_ROSE, "accent": (0.94, 0.27, 0.40, 0.3)},
        {"name": "Joel", "init": "J", "color": C_BLUE, "accent": (0.23, 0.51, 0.96, 0.3)},
    ]

    card_spacing = 22
    card_w = (box_w - 70 - (len(members) - 1) * card_spacing) / len(members)
    card_h = 220
    start_cx = box_x + 35
    card_y = box_y + 115

    for i, m in enumerate(members):
        # Staggered entrance animation
        stagger_t = 0.8 + i * 0.2
        card_alpha = max(0.0, min(1.0, (t - stagger_t) / 0.4)) if t > stagger_t else 0.0
        slide_offset = (1.0 - card_alpha) * 30.0

        cx = start_cx + i * (card_w + card_spacing)
        cy = card_y + slide_offset

        if card_alpha > 0.01:
            # Card background
            draw_card(ctx, cx, cy, card_w, card_h, r=16,
                      bg_rgba=(0.09, 0.13, 0.22, 0.95 * card_alpha),
                      border_rgba=(m['color'][0], m['color'][1], m['color'][2], 0.6 * card_alpha))

            # Avatar Circle
            center_x = cx + card_w / 2
            center_y = cy + 62
            ctx.arc(center_x, center_y, 36, 0, 2 * math.pi)
            ctx.set_source_rgba(m['color'][0], m['color'][1], m['color'][2], 0.25 * card_alpha)
            ctx.fill_preserve()
            ctx.set_source_rgba(m['color'][0], m['color'][1], m['color'][2], 0.85 * card_alpha)
            ctx.set_line_width(2.0)
            ctx.stroke()

            # Initial letter
            draw_text(ctx, center_x, center_y + 11, m['init'], size=28, color=C_TEXT_WHITE, bold=True, align="center")

            # Name
            draw_text(ctx, center_x, cy + 135, m['name'], size=22, color=C_TEXT_WHITE, bold=True, align="center")
            draw_text(ctx, center_x, cy + 162, "Desarrollador POO", size=13, color=C_TEXT_MUTED, align="center")

            # Status dot
            ctx.arc(center_x - 30, cy + 190, 4, 0, 2 * math.pi)
            ctx.set_source_rgba(0.06, 0.72, 0.50, 0.9 * card_alpha)
            ctx.fill()
            draw_text(ctx, center_x - 18, cy + 194, "Colaborador", size=12, color=C_TEXT_DIM, bold=False)

    # Bottom Architectural Badges
    pills = [
        "✓ Java 21 LTS Puro (Sin Maven/Gradle)",
        "✓ UI Swing Ejecutiva Moderna",
        "✓ Persistencia Atómica JSON",
        "✓ Cobertura 100% Tests"
    ]
    px = box_x + 70
    for p in pills:
        bw = draw_badge(ctx, px, 810, p, font_size=14,
                        bg_color=(0.10, 0.16, 0.26, 0.9), text_color=(0.85, 0.92, 1.0),
                        border=(0.25, 0.38, 0.55, 0.6), r=8)
        px += bw + 35

    # Footer note
    draw_text(ctx, WIDTH/2, 920, "Diseñado para Exposición Oral y Demostración en Vivo", size=15, color=C_TEXT_DIM, align="center")

    ctx.pop_group_to_source()
    ctx.paint_with_alpha(alpha)
    ctx.restore()

# ==========================================
# SCENE 2: DOMINIO Y PROBLEMÁTICA DEL NEGOCIO
# ==========================================
def render_scene_2(ctx, t):
    alpha = get_scene_alpha(t, 8.0, 16.0, fade_in=0.6, fade_out=0.6)
    ctx.save()
    ctx.push_group()

    # Scene header
    draw_badge(ctx, 100, 115, "ESCENA 02 • DOMINIO Y PROBLEMÁTICA DEL NEGOCIO", font_size=13,
               bg_color=(0.12, 0.20, 0.35, 0.9), text_color=C_CYAN, border=(0.02, 0.71, 0.83, 0.6), r=6)

    draw_text(ctx, 100, 165, "Gestión Multidisciplinaria Integral", size=38, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, 100, 202, "Administración sincronizada de socios, clases de alta demanda, liquidaciones docentes y biomecánica", size=18, color=C_TEXT_MUTED)

    # 4 Dynamic Domain Cards (2x2 Grid)
    grid_w = 830
    grid_h = 320
    col1_x = 100
    col2_x = 990
    row1_y = 240
    row2_y = 590

    # Card 1: Socios con estado de cuota
    draw_card(ctx, col1_x, row1_y, grid_w, grid_h, r=16, bg_rgba=(0.07, 0.11, 0.19, 0.92), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, col1_x + 30, row1_y + 48, "MÓDULO DE SOCIOS", font_size=12, bg_color=(0.15, 0.22, 0.35, 0.9), text_color=C_CYAN)
    draw_text(ctx, col1_x + 30, row1_y + 90, "Control de Padrón y Estado de Cuota", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, col1_x + 30, row1_y + 124, "Gestión de altas, bajas, información médica y validación de membresías.", size=15, color=C_TEXT_MUTED)

    # Dynamic status simulator inside Card 1
    sub_box_y = row1_y + 150
    draw_card(ctx, col1_x + 30, sub_box_y, grid_w - 60, 135, r=10, bg_rgba=(0.05, 0.08, 0.14, 0.95), border_rgba=(0.14, 0.20, 0.32, 0.6))
    draw_text(ctx, col1_x + 50, sub_box_y + 36, "Socio #102 - Martín Gómez (Plan Pro)", size=15, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, col1_x + 560, sub_box_y + 38, "● CUOTA AL DÍA", font_size=11, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.8))

    draw_text(ctx, col1_x + 50, sub_box_y + 82, "Socio #108 - Lucas Varela (Plan Estudiante)", size=15, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, col1_x + 560, sub_box_y + 84, "● MOROSO (VENCIDO)", font_size=11, bg_color=(0.94, 0.27, 0.40, 0.25), text_color=C_ROSE, border=(0.94, 0.27, 0.40, 0.8))

    draw_text(ctx, col1_x + 50, sub_box_y + 118, "Regla de Negocio: Detección automática de mora y bloqueo en molinete.", size=12, color=C_AMBER)

    # Card 2: Clases Grupales y Cupo
    draw_card(ctx, col2_x, row1_y, grid_w, grid_h, r=16, bg_rgba=(0.07, 0.11, 0.19, 0.92), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, col2_x + 30, row1_y + 48, "LOGÍSTICA Y AFORO", font_size=12, bg_color=(0.15, 0.22, 0.35, 0.9), text_color=C_PURPLE)
    draw_text(ctx, col2_x + 30, row1_y + 90, "Clases Grupales y Control de Cupo", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, col2_x + 30, row1_y + 124, "CrossFit • Spinning • Yoga • Pilates con límites estrictos de capacidad.", size=15, color=C_TEXT_MUTED)

    # Class chips & Exception alert
    chips_y = row1_y + 155
    cx_pos = col2_x + 30
    for cls_name, cap, full in [("CrossFit", "18/20", False), ("Spinning", "25/25", True), ("Yoga", "12/15", False), ("Pilates", "10/10", True)]:
        color = C_ROSE if full else C_EMERALD
        bg = (0.94, 0.27, 0.40, 0.2) if full else (0.06, 0.72, 0.50, 0.2)
        txt = f"{cls_name} ({cap})"
        bw = draw_badge(ctx, cx_pos, chips_y + 20, txt, font_size=12, bg_color=bg, text_color=color, border=(color[0], color[1], color[2], 0.7))
        cx_pos += bw + 15

    # Alert Box
    alert_y = row1_y + 205
    draw_card(ctx, col2_x + 30, alert_y, grid_w - 60, 80, r=8, bg_rgba=(0.20, 0.08, 0.10, 0.85), border_rgba=(0.94, 0.27, 0.40, 0.8))
    draw_text(ctx, col2_x + 50, alert_y + 34, "PREVENCIÓN ESTRICTA DE SOBREVENTA", size=14, color=C_ROSE, bold=True)
    draw_text(ctx, col2_x + 50, alert_y + 60, "Al intentar inscribir un socio con cupo completo: Lanza CupoAgotadoException", size=13, color=C_TEXT_WHITE)

    # Card 3: Instructores & Liquidaciones Salariales
    draw_card(ctx, col1_x, row2_y, grid_w, grid_h, r=16, bg_rgba=(0.07, 0.11, 0.19, 0.92), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, col1_x + 30, row2_y + 48, "RECURSOS HUMANOS", font_size=12, bg_color=(0.15, 0.22, 0.35, 0.9), text_color=C_AMBER)
    draw_text(ctx, col1_x + 30, row2_y + 90, "Instructores & Liquidación Salarial", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, col1_x + 30, row2_y + 124, "Gestión de cuerpo docente, especialidades asignadas y liquidación de haberes.", size=15, color=C_TEXT_MUTED)

    # Salary breakdown box
    sal_box_y = row2_y + 150
    draw_card(ctx, col1_x + 30, sal_box_y, grid_w - 60, 135, r=10, bg_rgba=(0.05, 0.08, 0.14, 0.95), border_rgba=(0.14, 0.20, 0.32, 0.6))
    draw_text(ctx, col1_x + 50, sal_box_y + 36, "Prof. Carlos Rossi (Especialidad: Musculación & HIIT)", size=15, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, col1_x + 560, sal_box_y + 38, "LIQUIDADO", font_size=11, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD)

    draw_text(ctx, col1_x + 50, sal_box_y + 75, "Fórmula: SalarioTotal = HorasMensuales × TarifaHora + BonificaciónEspecialidad", size=13, color=C_CYAN)
    draw_text(ctx, col1_x + 50, sal_box_y + 105, "Cálculo 100% automatizado, persistido en pagos.json con trazabilidad atómica.", size=13, color=C_TEXT_MUTED)

    # Card 4: Rutinas Personalizadas & Biomecánica
    draw_card(ctx, col2_x, row2_y, grid_w, grid_h, r=16, bg_rgba=(0.07, 0.11, 0.19, 0.92), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, col2_x + 30, row2_y + 48, "BIOMECÁNICA Y ENTRENAMIENTO", font_size=12, bg_color=(0.15, 0.22, 0.35, 0.9), text_color=C_EMERALD)
    draw_text(ctx, col2_x + 30, row2_y + 90, "Rutinas Personalizadas y Cargas", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, col2_x + 30, row2_y + 124, "Diseño de programas de ejercicios anatómicos con cálculo dinámico de tonelaje.", size=15, color=C_TEXT_MUTED)

    # Volume workout box
    vol_box_y = row2_y + 150
    draw_card(ctx, col2_x + 30, vol_box_y, grid_w - 60, 135, r=10, bg_rgba=(0.05, 0.08, 0.14, 0.95), border_rgba=(0.14, 0.20, 0.32, 0.6))
    draw_text(ctx, col2_x + 50, vol_box_y + 36, "Rutina Hipertrofia A (Pecho + Tríceps): 6 Ejercicios", size=15, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, col2_x + 560, vol_box_y + 38, "TONELAJE ACTIVO", font_size=11, bg_color=(0.23, 0.51, 0.96, 0.25), text_color=C_BLUE)

    draw_text(ctx, col2_x + 50, vol_box_y + 75, "Acumulador: TonelajeGlobal = Σ (Series × Repeticiones × CargaKg)", size=13, color=C_EMERALD, bold=True)
    draw_text(ctx, col2_x + 50, vol_box_y + 105, "Ejemplo: Press Banca 4x10x80kg = 3.200kg acumulados en la sesión.", size=13, color=C_TEXT_MUTED)

    ctx.pop_group_to_source()
    ctx.paint_with_alpha(alpha)
    ctx.restore()

# ==========================================
# SCENE 3: ARQUITECTURA POO Y MODELADO UML
# ==========================================
def render_scene_3(ctx, t):
    alpha = get_scene_alpha(t, 16.0, 25.0, fade_in=0.6, fade_out=0.6)
    ctx.save()
    ctx.push_group()

    # Scene header
    draw_badge(ctx, 100, 115, "ESCENA 03 • INGENIERÍA DE SOFTWARE & POO", font_size=13,
               bg_color=(0.12, 0.20, 0.35, 0.9), text_color=C_PURPLE, border=(0.55, 0.36, 0.96, 0.6), r=6)

    draw_text(ctx, 100, 165, "Modelado Orientado a Objetos Puro en Java 21 LTS", size=38, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, 100, 202, "Diseño estructural basado en principios SOLID, contratos por interfaz y bajo acoplamiento", size=18, color=C_TEXT_MUTED)

    # 4 Architectural Pillars
    pane_w = 405
    pane_h = 670
    spacing = 33
    pane_y = 240
    start_px = 100

    # PANE 1: HERENCIA
    p1_x = start_px + 0 * (pane_w + spacing)
    draw_card(ctx, p1_x, pane_y, pane_w, pane_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.20, 0.32, 0.50, 0.8))
    draw_badge(ctx, p1_x + 24, pane_y + 42, "HERENCIA POO", font_size=11, bg_color=(0.23, 0.51, 0.96, 0.3), text_color=C_BLUE)
    draw_text(ctx, p1_x + 24, pane_y + 82, "Jerarquía Persona", size=22, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, p1_x + 24, pane_y + 112, "Reutilización y polimorfismo", size=14, color=C_TEXT_MUTED)

    # UML Base Class Box
    uml_base_y = pane_y + 140
    draw_card(ctx, p1_x + 20, uml_base_y, pane_w - 40, 160, r=10, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.23, 0.51, 0.96, 0.7))
    draw_text(ctx, p1_x + 35, uml_base_y + 32, "«abstract» Persona", size=15, color=C_BLUE, bold=True)
    ctx.set_source_rgba(0.23, 0.51, 0.96, 0.4)
    ctx.set_line_width(1.0)
    ctx.move_to(p1_x + 20, uml_base_y + 44)
    ctx.line_to(p1_x + pane_w - 20, uml_base_y + 44)
    ctx.stroke()
    draw_text(ctx, p1_x + 35, uml_base_y + 70, "# dni: String", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p1_x + 35, uml_base_y + 94, "# nombre: String", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p1_x + 35, uml_base_y + 118, "# email: String", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p1_x + 35, uml_base_y + 145, "+ abstract mostrarDetalle()", size=13, color=C_CYAN, bold=True)

    # Inheritance Arrow
    arrow_y = uml_base_y + 185
    ctx.set_source_rgba(0.23, 0.51, 0.96, 0.9)
    ctx.set_line_width(2.0)
    ctx.move_to(p1_x + pane_w/2, uml_base_y + 160)
    ctx.line_to(p1_x + pane_w/2, arrow_y + 20)
    ctx.stroke()
    # Hollow triangle (UML extends)
    ctx.move_to(p1_x + pane_w/2, uml_base_y + 160)
    ctx.line_to(p1_x + pane_w/2 - 10, uml_base_y + 175)
    ctx.line_to(p1_x + pane_w/2 + 10, uml_base_y + 175)
    ctx.close_path()
    ctx.set_source_rgba(0.09, 0.14, 0.24, 1.0)
    ctx.fill_preserve()
    ctx.set_source_rgba(0.23, 0.51, 0.96, 1.0)
    ctx.stroke()

    # Subclasses
    sub_y = arrow_y + 35
    draw_card(ctx, p1_x + 20, sub_y, (pane_w - 50)/2, 110, r=8, bg_rgba=(0.08, 0.12, 0.20, 0.9), border_rgba=(0.06, 0.72, 0.50, 0.7))
    draw_text(ctx, p1_x + 30, sub_y + 28, "Socio", size=15, color=C_EMERALD, bold=True)
    draw_text(ctx, p1_x + 30, sub_y + 55, "- numSocio", size=12, color=C_TEXT_MUTED)
    draw_text(ctx, p1_x + 30, sub_y + 75, "- membresia", size=12, color=C_TEXT_MUTED)
    draw_text(ctx, p1_x + 30, sub_y + 98, "+ mostrarDetalle()", size=11, color=C_CYAN)

    draw_card(ctx, p1_x + 20 + (pane_w - 50)/2 + 10, sub_y, (pane_w - 50)/2, 110, r=8, bg_rgba=(0.08, 0.12, 0.20, 0.9), border_rgba=(0.96, 0.62, 0.04, 0.7))
    draw_text(ctx, p1_x + 30 + (pane_w - 50)/2 + 10, sub_y + 28, "Instructor", size=15, color=C_AMBER, bold=True)
    draw_text(ctx, p1_x + 30 + (pane_w - 50)/2 + 10, sub_y + 55, "- especialidad", size=12, color=C_TEXT_MUTED)
    draw_text(ctx, p1_x + 30 + (pane_w - 50)/2 + 10, sub_y + 75, "- sueldoHora", size=12, color=C_TEXT_MUTED)
    draw_text(ctx, p1_x + 30 + (pane_w - 50)/2 + 10, sub_y + 98, "+ mostrarDetalle()", size=11, color=C_CYAN)

    # Benefit description
    draw_text(ctx, p1_x + 24, pane_y + 535, "Sobreescritura polimórfica demostrada en colecciones genéricas de Persona.", size=13, color=C_TEXT_WHITE)
    draw_badge(ctx, p1_x + 24, pane_y + 610, "POLIMORFISMO DINÁMICO", font_size=11, bg_color=(0.23, 0.51, 0.96, 0.25), text_color=C_BLUE)

    # PANE 2: COMPOSICIÓN FUERTE
    p2_x = start_px + 1 * (pane_w + spacing)
    draw_card(ctx, p2_x, pane_y, pane_w, pane_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.20, 0.32, 0.50, 0.8))
    draw_badge(ctx, p2_x + 24, pane_y + 42, "COMPOSICIÓN FUERTE (*--)", font_size=11, bg_color=(0.06, 0.72, 0.50, 0.3), text_color=C_EMERALD)
    draw_text(ctx, p2_x + 24, pane_y + 82, "Rutina *-- Ejercicio", size=22, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, p2_x + 24, pane_y + 112, "Ciclo de vida fuertemente ligado", size=14, color=C_TEXT_MUTED)

    # UML Boxes for Composition
    draw_card(ctx, p2_x + 20, pane_y + 140, pane_w - 40, 110, r=10, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.06, 0.72, 0.50, 0.7))
    draw_text(ctx, p2_x + 35, pane_y + 172, "Rutina", size=16, color=C_EMERALD, bold=True)
    draw_text(ctx, p2_x + 35, pane_y + 200, "- ejercicios: List<Ejercicio>", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p2_x + 35, pane_y + 228, "+ calcularTonelajeTotal(): double", size=13, color=C_CYAN, bold=True)

    # Diamond Connector (Filled Diamond = Composition)
    dia_y = pane_y + 285
    ctx.set_source_rgba(0.06, 0.72, 0.50, 1.0)
    ctx.set_line_width(2.0)
    # Diamond
    ctx.move_to(p2_x + pane_w/2, pane_y + 250)
    ctx.line_to(p2_x + pane_w/2 - 12, pane_y + 265)
    ctx.line_to(p2_x + pane_w/2, pane_y + 280)
    ctx.line_to(p2_x + pane_w/2 + 12, pane_y + 265)
    ctx.close_path()
    ctx.fill_preserve()
    ctx.stroke()
    ctx.move_to(p2_x + pane_w/2, pane_y + 280)
    ctx.line_to(p2_x + pane_w/2, dia_y + 35)
    ctx.stroke()

    # Child class Ejercicio
    draw_card(ctx, p2_x + 20, dia_y + 35, pane_w - 40, 130, r=10, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.06, 0.72, 0.50, 0.7))
    draw_text(ctx, p2_x + 35, dia_y + 66, "Ejercicio", size=16, color=C_EMERALD, bold=True)
    draw_text(ctx, p2_x + 35, dia_y + 92, "- nombre: String", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p2_x + 35, dia_y + 115, "- series: int, reps: int, peso: double", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, p2_x + 35, dia_y + 140, "+ getVolumen(): double", size=13, color=C_CYAN)

    draw_text(ctx, p2_x + 24, pane_y + 520, "Si la Rutina se elimina, sus Ejercicios desaparecen con ella.", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p2_x + 24, pane_y + 550, "Cálculo acumulativo de tonelaje por composición directa.", size=13, color=C_TEXT_MUTED)
    draw_badge(ctx, p2_x + 24, pane_y + 610, "ACOPLAMIENTO EXISTENCIAL", font_size=11, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD)

    # PANE 3: AGREGACIÓN DÉBIL
    p3_x = start_px + 2 * (pane_w + spacing)
    draw_card(ctx, p3_x, pane_y, pane_w, pane_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.20, 0.32, 0.50, 0.8))
    draw_badge(ctx, p3_x + 24, pane_y + 42, "AGREGACIÓN DÉBIL (o--)", font_size=11, bg_color=(0.55, 0.36, 0.96, 0.3), text_color=C_PURPLE)
    draw_text(ctx, p3_x + 24, pane_y + 82, "ClaseGrupal o-- Socios", size=22, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, p3_x + 24, pane_y + 112, "Ciclos de vida independientes", size=14, color=C_TEXT_MUTED)

    # UML Boxes for Aggregation
    draw_card(ctx, p3_x + 20, pane_y + 140, pane_w - 40, 125, r=10, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.55, 0.36, 0.96, 0.7))
    draw_text(ctx, p3_x + 35, pane_y + 172, "ClaseGrupal", size=16, color=C_PURPLE, bold=True)
    draw_text(ctx, p3_x + 35, pane_y + 198, "- instructor: Instructor", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p3_x + 35, pane_y + 220, "- inscriptos: List<Socio>", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p3_x + 35, pane_y + 244, "+ inscribir(Socio) throws CupoAgotado", size=12, color=C_ROSE, bold=True)

    # Hollow Diamond Connector
    dia_agg_y = pane_y + 300
    ctx.set_source_rgba(0.55, 0.36, 0.96, 1.0)
    ctx.set_line_width(2.0)
    ctx.move_to(p3_x + pane_w/2, pane_y + 265)
    ctx.line_to(p3_x + pane_w/2 - 12, pane_y + 280)
    ctx.line_to(p3_x + pane_w/2, pane_y + 295)
    ctx.line_to(p3_x + pane_w/2 + 12, pane_y + 280)
    ctx.close_path()
    ctx.set_source_rgba(0.09, 0.14, 0.24, 1.0)
    ctx.fill_preserve()
    ctx.set_source_rgba(0.55, 0.36, 0.96, 1.0)
    ctx.stroke()
    ctx.move_to(p3_x + pane_w/2, pane_y + 295)
    ctx.line_to(p3_x + pane_w/2, dia_agg_y + 25)
    ctx.stroke()

    # Aggregated Class Box
    draw_card(ctx, p3_x + 20, dia_agg_y + 25, pane_w - 40, 115, r=10, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.55, 0.36, 0.96, 0.7))
    draw_text(ctx, p3_x + 35, dia_agg_y + 56, "Socio & Instructor", size=16, color=C_PURPLE, bold=True)
    draw_text(ctx, p3_x + 35, dia_agg_y + 82, "Entidades autónomas del club.", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p3_x + 35, dia_agg_y + 108, "Persisten aún si la clase se cancela.", size=12, color=C_CYAN)

    draw_text(ctx, p3_x + 24, pane_y + 520, "Si se cancela la ClaseGrupal, ni el Socio ni el Instructor desaparecen.", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p3_x + 24, pane_y + 560, "Control estricto de aforo sin acoplar ciclo de vida.", size=13, color=C_TEXT_MUTED)
    draw_badge(ctx, p3_x + 24, pane_y + 610, "CICLO DE VIDA INDEPENDIENTE", font_size=11, bg_color=(0.55, 0.36, 0.96, 0.25), text_color=C_PURPLE)

    # PANE 4: INTERFACES DE CONTRATO
    p4_x = start_px + 3 * (pane_w + spacing)
    draw_card(ctx, p4_x, pane_y, pane_w, pane_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.20, 0.32, 0.50, 0.8))
    draw_badge(ctx, p4_x + 24, pane_y + 42, "INTERFACES & CONTRATOS", font_size=11, bg_color=(0.02, 0.71, 0.83, 0.3), text_color=C_CYAN)
    draw_text(ctx, p4_x + 24, pane_y + 82, "Contratos Desacoplados", size=22, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, p4_x + 24, pane_y + 112, "Segregación de Interfaces (ISP)", size=14, color=C_TEXT_MUTED)

    # 3 Interfaces Boxes
    int_y = pane_y + 140
    # Facturable
    draw_card(ctx, p4_x + 20, int_y, pane_w - 40, 85, r=8, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.02, 0.71, 0.83, 0.7))
    draw_text(ctx, p4_x + 35, int_y + 28, "«interface» Facturable", size=14, color=C_CYAN, bold=True)
    draw_text(ctx, p4_x + 35, int_y + 52, "+ calcularMontoTotal(): double", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, p4_x + 35, int_y + 72, "Impl: Membresia, Pago, Cuota", size=11, color=C_TEXT_MUTED)

    # Autenticable
    draw_card(ctx, p4_x + 20, int_y + 100, pane_w - 40, 85, r=8, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.06, 0.72, 0.50, 0.7))
    draw_text(ctx, p4_x + 35, int_y + 128, "«interface» Autenticable", size=14, color=C_EMERALD, bold=True)
    draw_text(ctx, p4_x + 35, int_y + 152, "+ validarAcceso(TipoAcceso): boolean", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, p4_x + 35, int_y + 172, "Impl: Socio, Instructor, Personal", size=11, color=C_TEXT_MUTED)

    # Reportable
    draw_card(ctx, p4_x + 20, int_y + 200, pane_w - 40, 85, r=8, bg_rgba=(0.09, 0.14, 0.24, 0.95), border_rgba=(0.96, 0.62, 0.04, 0.7))
    draw_text(ctx, p4_x + 35, int_y + 228, "«interface» Reportable", size=14, color=C_AMBER, bold=True)
    draw_text(ctx, p4_x + 35, int_y + 252, "+ generarResumenEjecutivo(): String", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, p4_x + 35, int_y + 272, "Impl: Socios, Asistencias, Clases", size=11, color=C_TEXT_MUTED)

    draw_text(ctx, p4_x + 24, pane_y + 520, "Los controladores interactúan con contratos, jamás con implementaciones rígidas.", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, p4_x + 24, pane_y + 560, "Facilita sustitución de Liskov y testing unitario.", size=13, color=C_TEXT_MUTED)
    draw_badge(ctx, p4_x + 24, pane_y + 610, "CONTRATOS PUROS (SOLID)", font_size=11, bg_color=(0.02, 0.71, 0.83, 0.25), text_color=C_CYAN)

    ctx.pop_group_to_source()
    ctx.paint_with_alpha(alpha)
    ctx.restore()

# ==========================================
# SCENE 4: TECNOLOGÍA, FACTURACIÓN Y CHECK-IN
# ==========================================
def render_scene_4(ctx, t):
    alpha = get_scene_alpha(t, 25.0, 34.0, fade_in=0.6, fade_out=0.6)
    ctx.save()
    ctx.push_group()

    # Scene header
    draw_badge(ctx, 100, 115, "ESCENA 04 • TECNOLOGÍA & REGLAS DE NEGOCIO", font_size=13,
               bg_color=(0.12, 0.20, 0.35, 0.9), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.6), r=6)

    draw_text(ctx, 100, 165, "Persistencia Autónoma, Descuentos y Check-in Inteligente", size=38, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, 100, 202, "Mecanismos técnicos de nivel industrial construidos con JDK 21 estándar sin dependencias", size=18, color=C_TEXT_MUTED)

    # 3 Large Feature Columns
    col_w = 540
    col_h = 670
    col_spacing = 50
    start_cx = 100
    col_y = 240

    # COL 1: PERSISTENCIA ATÓMICA JSON
    c1_x = start_cx + 0 * (col_w + col_spacing)
    draw_card(ctx, c1_x, col_y, col_w, col_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, c1_x + 30, col_y + 44, "PERSISTENCIA AUTÓNOMA", font_size=12, bg_color=(0.02, 0.71, 0.83, 0.3), text_color=C_CYAN)
    draw_text(ctx, c1_x + 30, col_y + 88, "JsonPersistenceEngine", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c1_x + 30, col_y + 118, "Cero dependencias externas pesadas", size=14, color=C_TEXT_MUTED)

    # Atomic Save Pipeline Graphic
    pipe_y = col_y + 150
    draw_card(ctx, c1_x + 25, pipe_y, col_w - 50, 310, r=12, bg_rgba=(0.04, 0.07, 0.13, 0.95), border_rgba=(0.14, 0.20, 0.32, 0.7))
    draw_text(ctx, c1_x + 45, pipe_y + 35, "PIPELINE DE ESCRITURA ATÓMICA", size=14, color=C_CYAN, bold=True)

    steps = [
        ("1. Serialización Nativa", "Generación de JSON puro en memoria RAM", C_TEXT_WHITE),
        ("2. Archivo Temporal .tmp", "Escritura en disco: socios.json.tmp", C_AMBER),
        ("3. Reemplazo Atómico OS", "StandardCopyOption.ATOMIC_MOVE", C_EMERALD),
        ("4. Archivo Final Sellado", "socios.json garantizado sin corrupción", C_CYAN)
    ]
    sy = pipe_y + 70
    for title, desc, clr in steps:
        ctx.arc(c1_x + 55, sy + 6, 5, 0, 2 * math.pi)
        ctx.set_source_rgb(*clr)
        ctx.fill()
        draw_text(ctx, c1_x + 75, sy + 10, title, size=14, color=clr, bold=True)
        draw_text(ctx, c1_x + 75, sy + 30, desc, size=12, color=C_TEXT_DIM)
        sy += 55

    draw_text(ctx, c1_x + 30, col_y + 510, "Garantía de Integridad: Si el equipo sufre un corte eléctrico durante el guardado, el archivo anterior permanece 100% intacto.", size=13, color=C_TEXT_WHITE)
    draw_badge(ctx, c1_x + 30, col_y + 610, "ACID-LITE SIN FRAMEWORKS", font_size=12, bg_color=(0.02, 0.71, 0.83, 0.25), text_color=C_CYAN)

    # COL 2: REGLA DE FACTURACIÓN 10% ANTIGÜEDAD
    c2_x = start_cx + 1 * (col_w + col_spacing)
    draw_card(ctx, c2_x, col_y, col_w, col_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, c2_x + 30, col_y + 44, "REGLA DE NEGOCIO", font_size=12, bg_color=(0.96, 0.62, 0.04, 0.3), text_color=C_AMBER)
    draw_text(ctx, c2_x + 30, col_y + 88, "Descuento 10% Antigüedad", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c2_x + 30, col_y + 118, "Fidelización automática por permanencia", size=14, color=C_TEXT_MUTED)

    # Receipt Simulation Card
    rec_y = col_y + 150
    draw_card(ctx, c2_x + 25, rec_y, col_w - 50, 310, r=12, bg_rgba=(0.04, 0.07, 0.13, 0.95), border_rgba=(0.96, 0.62, 0.04, 0.6))
    draw_text(ctx, c2_x + 45, rec_y + 35, "LIQUIDACIÓN DE CUOTA MENSUAL", size=14, color=C_AMBER, bold=True)

    draw_text(ctx, c2_x + 45, rec_y + 75, "Socio: Martín Gómez (Antigüedad: 24 meses)", size=13, color=C_TEXT_WHITE)
    draw_text(ctx, c2_x + 45, rec_y + 100, "Condición: Antigüedad > 12 meses [CUMPLE ✓]", size=12, color=C_EMERALD)

    ctx.set_source_rgba(0.25, 0.35, 0.50, 0.5)
    ctx.set_line_width(1.0)
    ctx.move_to(c2_x + 45, rec_y + 115)
    ctx.line_to(c2_x + col_w - 45, rec_y + 115)
    ctx.stroke()

    draw_text(ctx, c2_x + 45, rec_y + 145, "Cuota Base Membresía Full:", size=14, color=C_TEXT_MUTED)
    draw_text(ctx, c2_x + col_w - 150, rec_y + 145, "$ 15.000,00", size=14, color=C_TEXT_WHITE, bold=True)

    draw_text(ctx, c2_x + 45, rec_y + 180, "Bonificación Antigüedad (-10%):", size=14, color=C_EMERALD)
    draw_text(ctx, c2_x + col_w - 150, rec_y + 180, "- $ 1.500,00", size=14, color=C_EMERALD, bold=True)

    ctx.set_source_rgba(0.25, 0.35, 0.50, 0.5)
    ctx.move_to(c2_x + 45, rec_y + 205)
    ctx.line_to(c2_x + col_w - 45, rec_y + 205)
    ctx.stroke()

    draw_text(ctx, c2_x + 45, rec_y + 245, "TOTAL A FACTURAR:", size=16, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c2_x + col_w - 165, rec_y + 245, "$ 13.500,00", size=18, color=C_CYAN, bold=True)

    draw_badge(ctx, c2_x + 45, rec_y + 288, "FACTURA GENERADA EXITOSAMENTE", font_size=11, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD)

    draw_text(ctx, c2_x + 30, col_y + 510, "Implementación del contrato Facturable. El motor aplica descuentos transparentes y emite comprobantes digitales instantáneos.", size=13, color=C_TEXT_WHITE)
    draw_badge(ctx, c2_x + 30, col_y + 610, "INTERFAZ FACTURABLE CUMPLIDA", font_size=12, bg_color=(0.96, 0.62, 0.04, 0.25), text_color=C_AMBER)

    # COL 3: MOLINETE INTELIGENTE (CHECK-IN)
    c3_x = start_cx + 2 * (col_w + col_spacing)
    draw_card(ctx, c3_x, col_y, col_w, col_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.18, 0.28, 0.44, 0.8))
    draw_badge(ctx, c3_x + 30, col_y + 44, "CONTROL DE ACCESOS", font_size=12, bg_color=(0.06, 0.72, 0.50, 0.3), text_color=C_EMERALD)
    draw_text(ctx, c3_x + 30, col_y + 88, "Molinete Inteligente", size=24, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c3_x + 30, col_y + 118, "Validación visual instantánea en puerta", size=14, color=C_TEXT_MUTED)

    # Live Turnstile Simulation Box
    turn_y = col_y + 150
    draw_card(ctx, c3_x + 25, turn_y, col_w - 50, 310, r=12, bg_rgba=(0.04, 0.07, 0.13, 0.95), border_rgba=(0.06, 0.72, 0.50, 0.6))
    draw_text(ctx, c3_x + 45, turn_y + 35, "SIMULACIÓN DE TERMINAL DE PASO", size=14, color=C_CYAN, bold=True)

    # Card Green Pass
    draw_card(ctx, c3_x + 40, turn_y + 55, col_w - 80, 100, r=8, bg_rgba=(0.03, 0.16, 0.10, 0.9), border_rgba=(0.06, 0.72, 0.50, 0.9), glow=True, glow_color=C_EMERALD)
    ctx.arc(c3_x + 70, turn_y + 105, 14, 0, 2 * math.pi)
    ctx.set_source_rgb(*C_EMERALD)
    ctx.fill()
    draw_text(ctx, c3_x + 64, turn_y + 111, "✓", size=18, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c3_x + 100, turn_y + 92, "ACCESO CONCEDIDO", size=16, color=C_EMERALD, bold=True)
    draw_text(ctx, c3_x + 100, turn_y + 115, "Socio #102: Martín Gómez • Cuota AL DÍA", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, c3_x + 100, turn_y + 135, "Molinete Destrabado | Registro en asistencias.json", size=11, color=(0.7, 0.95, 0.8))

    # Card Red Denied
    draw_card(ctx, c3_x + 40, turn_y + 175, col_w - 80, 100, r=8, bg_rgba=(0.20, 0.05, 0.08, 0.9), border_rgba=(0.94, 0.27, 0.40, 0.9), glow=True, glow_color=C_ROSE)
    ctx.arc(c3_x + 70, turn_y + 225, 14, 0, 2 * math.pi)
    ctx.set_source_rgb(*C_ROSE)
    ctx.fill()
    draw_text(ctx, c3_x + 64, turn_y + 231, "✗", size=18, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, c3_x + 100, turn_y + 212, "ACCESO BLOQUEADO", size=16, color=C_ROSE, bold=True)
    draw_text(ctx, c3_x + 100, turn_y + 235, "Socio #108: Lucas Varela • Cuota VENCIDA", size=12, color=C_TEXT_WHITE)
    draw_text(ctx, c3_x + 100, turn_y + 255, "Molinete Bloqueado | Regularizar en Administración", size=11, color=(1.0, 0.7, 0.7))

    draw_text(ctx, c3_x + 30, col_y + 510, "Modal interactivo en Swing con semáforo cromático y registro de ingreso seguro sin demoras.", size=13, color=C_TEXT_WHITE)
    draw_badge(ctx, c3_x + 30, col_y + 610, "SEGURIDAD & TRAZABILIDAD", font_size=12, bg_color=(0.06, 0.72, 0.50, 0.25), text_color=C_EMERALD)

    ctx.pop_group_to_source()
    ctx.paint_with_alpha(alpha)
    ctx.restore()

# ==========================================
# SCENE 5: CALIDAD DE SOFTWARE, GRAPHIFY Y CIERRE
# ==========================================
def render_scene_5(ctx, t):
    alpha = get_scene_alpha(t, 34.0, 42.0, fade_in=0.6, fade_out=1.2)
    ctx.save()
    ctx.push_group()

    # Scene header
    draw_badge(ctx, 100, 115, "ESCENA 05 • CALIDAD, AUDITORÍA & CONCLUSIÓN", font_size=13,
               bg_color=(0.12, 0.20, 0.35, 0.9), text_color=C_CYAN, border=(0.02, 0.71, 0.83, 0.6), r=6)

    draw_text(ctx, 100, 165, "Calidad Certificada, Auditoría Graphify y Cierre", size=38, color=C_TEXT_WHITE, bold=True)
    draw_text(ctx, 100, 202, "Validación exhaustiva de la solución y bienvenida a la presentación del equipo", size=18, color=C_TEXT_MUTED)

    # Two Main Quality Columns
    q_col_w = 835
    q_col_h = 440
    q_col1_x = 100
    q_col2_x = 985
    q_y = 235

    # COLUMN 1: TEST SUITE AUTOMATIZADA
    draw_card(ctx, q_col1_x, q_y, q_col_w, q_col_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.06, 0.72, 0.50, 0.8))
    draw_badge(ctx, q_col1_x + 30, q_y + 44, "TEST SUITE AUTOMATIZADA (scripts/test.sh)", font_size=12, bg_color=(0.06, 0.72, 0.50, 0.3), text_color=C_EMERALD)
    draw_text(ctx, q_col1_x + 30, q_y + 88, "100% Tests Automatizados Aprobados", size=24, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, q_col1_x + 590, q_y + 88, "7 / 7 PASS (100%)", font_size=14, bg_color=(0.06, 0.72, 0.50, 0.3), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.9))

    # 7 Tests Checklist
    tests = [
        "1. Herencia y Polimorfismo en Persona (Socio / Instructor)",
        "2. Composición Fuerte Rutina *-- Ejercicio (Cálculo de Tonelaje)",
        "3. Control de Cupo y Excepción CupoAgotadoException",
        "4. Regla de Negocio: Descuento 10% por Antigüedad (>12 meses)",
        "5. Motor JsonPersistenceEngine y Guardado Atómico OS",
        "6. Validación de Estados de Cuota y Check-In en Molinete",
        "7. Generación de Reportes Ejecutivos sin Dependencias Externas"
    ]
    ty = q_y + 130
    for test_desc in tests:
        # Green check circle
        ctx.arc(q_col1_x + 45, ty + 7, 9, 0, 2 * math.pi)
        ctx.set_source_rgb(*C_EMERALD)
        ctx.fill()
        draw_text(ctx, q_col1_x + 40, ty + 12, "✓", size=13, color=C_TEXT_WHITE, bold=True)
        draw_text(ctx, q_col1_x + 70, ty + 12, test_desc, size=14, color=C_TEXT_WHITE)
        draw_badge(ctx, q_col1_x + q_col_w - 95, ty + 12, "PASS", font_size=10, bg_color=(0.06, 0.72, 0.50, 0.2), text_color=C_EMERALD)
        ty += 40

    # COLUMN 2: AUDITORÍA ARCHITECTURE GRAPHIFY
    draw_card(ctx, q_col2_x, q_y, q_col_w, q_col_h, r=16, bg_rgba=(0.06, 0.09, 0.17, 0.94), border_rgba=(0.02, 0.71, 0.83, 0.8))
    draw_badge(ctx, q_col2_x + 30, q_y + 44, "AUDITORÍA FORMAL DE ARQUITECTURA", font_size=12, bg_color=(0.02, 0.71, 0.83, 0.3), text_color=C_CYAN)
    draw_text(ctx, q_col2_x + 30, q_y + 88, "Grafo de Conocimiento Graphify", size=24, color=C_TEXT_WHITE, bold=True)
    draw_badge(ctx, q_col2_x + 590, q_y + 88, "0 CICLOS (DAG)", font_size=14, bg_color=(0.02, 0.71, 0.83, 0.3), text_color=C_CYAN, border=(0.02, 0.71, 0.83, 0.9))

    # 4 Architecture Metric Cards
    m_w = (q_col_w - 80) / 2
    m_h = 135
    m_cards = [
        ("0 CICLOS", "Aciclicidad Estricta", "Grafo Acíclico Dirigido verificado en graphify-out/", C_EMERALD),
        ("865 NODOS", "Indexación Completa", "Clases, interfaces, métodos y atributos mapeados", C_CYAN),
        ("2.168 ARISTAS", "Relaciones Trazadas", "Herencia, composición, llamadas y agregaciones", C_PURPLE),
        ("40 COMUNIDADES", "Modularidad Pura", "Alta cohesión de paquetes con acoplamiento mínimo", C_AMBER)
    ]
    for mi, (val, tit, des, clr) in enumerate(m_cards):
        mx = q_col2_x + 30 + (mi % 2) * (m_w + 20)
        my = q_y + 120 + (mi // 2) * (m_h + 15)
        draw_card(ctx, mx, my, m_w, m_h, r=10, bg_rgba=(0.04, 0.07, 0.13, 0.95), border_rgba=(clr[0], clr[1], clr[2], 0.6))
        draw_text(ctx, mx + 20, my + 38, val, size=24, color=clr, bold=True)
        draw_text(ctx, mx + 20, my + 68, tit, size=15, color=C_TEXT_WHITE, bold=True)
        draw_text(ctx, mx + 20, my + 95, des, size=12, color=C_TEXT_DIM)

    # BOTTOM WELCOME & CLOSING HERO BANNER
    ban_y = 705
    ban_w = 1720
    ban_h = 240
    ban_x = 100
    draw_card(ctx, ban_x, ban_y, ban_w, ban_h, r=20,
              bg_rgba=(0.06, 0.12, 0.22, 0.95), border_rgba=(0.06, 0.72, 0.50, 0.8), glow=True, glow_color=C_CYAN)

    draw_badge(ctx, WIDTH/2 - 190, ban_y + 44, "APERTURA OFICIAL DE LA PRESENTACIÓN", font_size=13,
               bg_color=(0.06, 0.72, 0.50, 0.35), text_color=C_EMERALD, border=(0.06, 0.72, 0.50, 0.9))

    draw_text(ctx, WIDTH/2, ban_y + 95, "¡BIENVENIDOS A LA PRESENTACIÓN DE FITMANAGE PRO!", size=36, color=C_TEXT_WHITE, bold=True, align="center")
    draw_text(ctx, WIDTH/2, ban_y + 132, "Sistema verificado y listo para la demostración en vivo de la aplicación y el coloquio evaluador", size=18, color=C_CYAN, align="center")

    # Team members signature footer
    team_sig = "Juan  •  Rocio  •  Merlina  •  Alfonsina  •  Sara  •  Joel"
    draw_text(ctx, WIDTH/2, ban_y + 195, team_sig, size=22, color=C_TEXT_WHITE, bold=True, align="center")

    ctx.pop_group_to_source()
    ctx.paint_with_alpha(alpha)
    ctx.restore()

# ==========================================
# MAIN RENDER PIPELINE
# ==========================================
def render_all():
    out_video = "/home/juancito/Documents/Orca/java/video/fitmanage_presentacion.mp4"
    audio_wav = "/home/juancito/Documents/Orca/java/video/soundtrack.wav"

    if not os.path.exists(audio_wav):
        print(f"Error: audio file {audio_wav} not found! Run generate_audio.py first.")
        sys.exit(1)

    print(f"Starting render: {WIDTH}x{HEIGHT} @ {FPS}fps, {TOTAL_FRAMES} frames ({DURATION}s)...")
    start_time = time.time()

    # Launch ffmpeg process receiving rawvideo from stdin and audio from soundtrack.wav
    ffmpeg_cmd = [
        'ffmpeg', '-y',
        '-f', 'rawvideo',
        '-vcodec', 'rawvideo',
        '-s', f'{WIDTH}x{HEIGHT}',
        '-pix_fmt', 'bgra',
        '-r', str(FPS),
        '-i', '-',                 # Video stream from stdin
        '-i', audio_wav,           # Audio stream from WAV
        '-c:v', 'libx264',
        '-preset', 'fast',
        '-crf', '18',
        '-pix_fmt', 'yuv420p',
        '-c:a', 'aac',
        '-b:a', '192k',
        '-movflags', '+faststart',
        '-shortest',
        out_video
    ]

    proc = subprocess.Popen(ffmpeg_cmd, stdin=subprocess.PIPE, stderr=subprocess.PIPE)

    surface = cairo.ImageSurface(cairo.FORMAT_ARGB32, WIDTH, HEIGHT)
    ctx = cairo.Context(surface)

    # Frame extraction checkpoints for self-audit stills
    capture_frames = {
        int(4.0 * FPS): "/home/juancito/Documents/Orca/java/video/capturas/escena1_titulo_equipo.png",
        int(12.0 * FPS): "/home/juancito/Documents/Orca/java/video/capturas/escena2_dominio_negocio.png",
        int(20.0 * FPS): "/home/juancito/Documents/Orca/java/video/capturas/escena3_arquitectura_poo.png",
        int(30.0 * FPS): "/home/juancito/Documents/Orca/java/video/capturas/escena4_tecnologia_checkin.png",
        int(38.0 * FPS): "/home/juancito/Documents/Orca/java/video/capturas/escena5_calidad_graphify.png"
    }

    for frame_idx in range(TOTAL_FRAMES):
        t = frame_idx / FPS

        # Determine current scene
        if t < 8.0:
            scene_idx = 0
        elif t < 16.0:
            scene_idx = 1
        elif t < 25.0:
            scene_idx = 2
        elif t < 34.0:
            scene_idx = 3
        else:
            scene_idx = 4

        # Draw Frame
        draw_background(ctx, t)

        # Draw Scene Content
        if scene_idx == 0 or t < 8.7:
            render_scene_1(ctx, t)
        if scene_idx == 1 or (t >= 7.3 and t < 16.7):
            render_scene_2(ctx, t)
        if scene_idx == 2 or (t >= 15.3 and t < 25.7):
            render_scene_3(ctx, t)
        if scene_idx == 3 or (t >= 24.3 and t < 34.7):
            render_scene_4(ctx, t)
        if scene_idx == 4 or t >= 33.3:
            render_scene_5(ctx, t)

        # Draw Global Navigation Chrome & Progress Bar
        draw_global_chrome(ctx, t, scene_idx)

        surface.flush()

        # Capture key stills for visual audit
        if frame_idx in capture_frames:
            cap_path = capture_frames[frame_idx]
            surface.write_to_png(cap_path)
            print(f"Saved keyframe capture [{frame_idx} / t={t:.1f}s]: {cap_path}")

        # Pipe raw buffer to ffmpeg
        proc.stdin.write(surface.get_data())

        if frame_idx % 150 == 0 or frame_idx == TOTAL_FRAMES - 1:
            elapsed = time.time() - start_time
            fps_render = (frame_idx + 1) / max(0.001, elapsed)
            print(f"Render progress: {frame_idx + 1}/{TOTAL_FRAMES} frames ({(frame_idx + 1)/TOTAL_FRAMES*100:.1f}%) | {fps_render:.1f} fps")

    proc.stdin.close()
    proc.wait()
    stderr = proc.stderr.read()

    if proc.returncode != 0:
        print("FFmpeg error:", stderr.decode('utf-8', errors='ignore'))
        sys.exit(proc.returncode)

    elapsed_total = time.time() - start_time
    file_size = os.path.getsize(out_video)
    print(f"\nRender completed successfully in {elapsed_total:.1f}s!")
    print(f"Output video: {out_video} ({file_size / (1024*1024):.2f} MB)")

if __name__ == "__main__":
    render_all()
