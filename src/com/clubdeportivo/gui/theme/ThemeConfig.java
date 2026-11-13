package com.clubdeportivo.gui.theme;

import java.awt.Color;
import java.awt.Font;

/**
 * Paleta de colores, tipografías y métricas visuales del sistema FitManage Pro.
 * Diseñado con una estética limpia, corporativa y moderna para Java Swing.
 *
 * @author Sara
 * @version 1.0.0
 */
public final class ThemeConfig {

    private ThemeConfig() {
    }

    // Fondos y Superficies
    public static final Color BG_MAIN = new Color(248, 250, 252);       // #f8fafc
    public static final Color BG_CARD = Color.WHITE;                    // #ffffff
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);       // #0f172a
    public static final Color BG_SIDEBAR_HOVER = new Color(30, 41, 59); // #1e293b
    public static final Color BG_SIDEBAR_ACTIVE = new Color(51, 65, 85);// #334155

    // Colores Principales y Acentos
    public static final Color PRIMARY = new Color(37, 99, 235);         // #2563eb
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);   // #1d4ed8
    public static final Color PRIMARY_LIGHT = new Color(239, 246, 255); // #eff6ff

    // Textos
    public static final Color TEXT_MAIN = new Color(15, 23, 42);        // #0f172a
    public static final Color TEXT_MUTED = new Color(71, 85, 105);      // #475569
    public static final Color TEXT_SIDEBAR_MUTED = new Color(148, 163, 184); // #94a3b8
    public static final Color TEXT_LIGHT = new Color(241, 245, 249);    // #f1f5f9
    public static final Color TEXT_WHITE = Color.WHITE;

    // Bordes
    public static final Color BORDER_COLOR = new Color(226, 232, 240);  // #e2e8f0
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);   // #3b82f6

    // Semáforos / Estados
    public static final Color STATUS_GREEN = new Color(22, 101, 52);    // #166534
    public static final Color STATUS_GREEN_BG = new Color(220, 252, 231);// #dcfce7

    public static final Color STATUS_AMBER = new Color(146, 64, 14);    // #92400e
    public static final Color STATUS_AMBER_BG = new Color(254, 243, 199);// #fef3c7

    public static final Color STATUS_RED = new Color(185, 28, 28);      // #b91c1c
    public static final Color STATUS_RED_BG = new Color(254, 226, 226);  // #fee2e2

    public static final Color STATUS_GRAY = new Color(71, 85, 105);     // #475569
    public static final Color STATUS_GRAY_BG = new Color(241, 245, 249); // #f1f5f9

    // Tipografías
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("SansSerif", Font.BOLD, 26);
}
