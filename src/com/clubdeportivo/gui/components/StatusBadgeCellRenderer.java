package com.clubdeportivo.gui.components;

import com.clubdeportivo.gui.theme.ThemeConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renderer personalizado para celdas de JTable que dibuja etiquetas (badges/pills)
 * con colores semánticos (Verde, Amarillo, Rojo, Gris) según el estado de la fila.
 *
 * @author Sara
 * @version 1.0.0
 */
public class StatusBadgeCellRenderer extends DefaultTableCellRenderer {

    private Color badgeBg = ThemeConfig.STATUS_GRAY_BG;
    private Color badgeFg = ThemeConfig.STATUS_GRAY;

    public StatusBadgeCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String text = (value != null) ? value.toString().trim() : "";
        setText(text);
        setFont(ThemeConfig.FONT_BOLD);

        String upper = text.toUpperCase();

        if (upper.contains("AL DIA") || upper.contains("AL_DIA") || upper.contains("PERMITIDO") || upper.contains("OK")) {
            badgeBg = ThemeConfig.STATUS_GREEN_BG;
            badgeFg = ThemeConfig.STATUS_GREEN;
        } else if (upper.contains("PENDIENTE") || upper.contains("GRACIA")) {
            badgeBg = ThemeConfig.STATUS_AMBER_BG;
            badgeFg = ThemeConfig.STATUS_AMBER;
        } else if (upper.contains("VENCIDO") || upper.contains("DENEGADO") || upper.contains("BLOQUEADO")) {
            badgeBg = ThemeConfig.STATUS_RED_BG;
            badgeFg = ThemeConfig.STATUS_RED;
        } else {
            badgeBg = ThemeConfig.STATUS_GRAY_BG;
            badgeFg = ThemeConfig.STATUS_GRAY;
        }

        setForeground(badgeFg);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int paddingH = 6;
        int paddingV = 4;
        int w = getWidth() - (paddingH * 2);
        int h = getHeight() - (paddingV * 2);

        g2.setColor(badgeBg);
        g2.fillRoundRect(paddingH, paddingV, w, h, 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }
}
