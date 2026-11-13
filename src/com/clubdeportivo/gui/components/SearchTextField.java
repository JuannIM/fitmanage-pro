package com.clubdeportivo.gui.components;

import com.clubdeportivo.gui.theme.ThemeConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Campo de texto estilizado con placeholder y bordes curvos para búsquedas en vivo.
 *
 * @author Sara
 * @version 1.0.0
 */
public final class SearchTextField extends JTextField {

    private static final long serialVersionUID = 1L;

    private final String placeholder;
    private boolean focused = false;

    public SearchTextField(String placeholder) {
        this.placeholder = (placeholder != null) ? placeholder : "Buscar...";
        setFont(ThemeConfig.FONT_REGULAR);
        setForeground(ThemeConfig.TEXT_MAIN);
        setBackground(Color.WHITE);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 30, 8, 12));
        setPreferredSize(new Dimension(280, 36));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        // Borde
        g2.setColor(focused ? ThemeConfig.BORDER_FOCUS : ThemeConfig.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

        // Icono de lupa dibujado con primitivas
        g2.setColor(ThemeConfig.TEXT_MUTED);
        int iconX = 10;
        int iconY = (getHeight() - 12) / 2;
        g2.drawOval(iconX, iconY, 8, 8);
        g2.drawLine(iconX + 6, iconY + 7, iconX + 11, iconY + 12);

        // Placeholder si está vacío
        if (getText().isEmpty() && !focused) {
            g2.setFont(ThemeConfig.FONT_REGULAR);
            FontMetrics fm = g2.getFontMetrics();
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(placeholder, 30, textY);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
