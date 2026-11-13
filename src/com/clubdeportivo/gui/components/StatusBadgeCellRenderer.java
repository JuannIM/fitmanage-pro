package com.clubdeportivo.gui.components;

import com.clubdeportivo.gui.theme.ThemeConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Renderer personalizado para celdas de JTable que dibuja etiquetas (badges/pills)
 * con colores semánticos (Verde, Amarillo, Rojo, Gris) según el estado de la fila.
 *
 * @author Sara
 * @version 1.0.0
 */
public final class StatusBadgeCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private Color badgeBg = ThemeConfig.STATUS_GRAY_BG;
    private Color badgeFg = ThemeConfig.STATUS_GRAY;
    private boolean rowSelected = false;
    private Color selectionBg = ThemeConfig.PRIMARY_LIGHT;

    public StatusBadgeCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        this.rowSelected = isSelected;
        this.selectionBg = (table != null && table.getSelectionBackground() != null)
                ? table.getSelectionBackground()
                : ThemeConfig.PRIMARY_LIGHT;

        String text = (value != null) ? value.toString().trim() : "";
        setText(text);
        setFont(ThemeConfig.FONT_BOLD);

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT);

        if (normalized.contains("AL DIA") || normalized.contains("AL_DIA")
                || normalized.contains("PERMITIDO") || normalized.contains("OK")
                || normalized.contains("DISPONIBLE")) {
            badgeBg = ThemeConfig.STATUS_GREEN_BG;
            badgeFg = ThemeConfig.STATUS_GREEN;
        } else if (normalized.contains("COMPLETO") || normalized.contains("SIN CUPO")
                || normalized.contains("VENCIDO") || normalized.contains("DENEGADO")
                || normalized.contains("BLOQUEADO")) {
            badgeBg = ThemeConfig.STATUS_RED_BG;
            badgeFg = ThemeConfig.STATUS_RED;
        } else if (normalized.contains("PENDIENTE") || normalized.contains("GRACIA")) {
            badgeBg = ThemeConfig.STATUS_AMBER_BG;
            badgeFg = ThemeConfig.STATUS_AMBER;
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

        if (rowSelected) {
            g2.setColor(selectionBg);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

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
