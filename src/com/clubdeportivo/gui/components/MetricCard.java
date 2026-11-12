package com.clubdeportivo.gui.components;

import com.clubdeportivo.gui.theme.ThemeConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tarjeta visual para métricas clave (KPI) del Dashboard con diseño minimalista.
 *
 * @author Sara
 * @version 1.0.0
 */
public class MetricCard extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblValor;
    private final JLabel lblSubtitulo;
    private Color accentColor = ThemeConfig.PRIMARY;

    public MetricCard(String titulo, String valorInicial, String subtitulo, Color accentColor) {
        this.accentColor = (accentColor != null) ? accentColor : ThemeConfig.PRIMARY;
        setLayout(new BorderLayout(5, 5));
        setBackground(ThemeConfig.BG_CARD);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(ThemeConfig.FONT_SMALL);
        lblTitulo.setForeground(ThemeConfig.TEXT_MUTED);

        lblValor = new JLabel(valorInicial);
        lblValor.setFont(ThemeConfig.FONT_METRIC);
        lblValor.setForeground(ThemeConfig.TEXT_MAIN);

        lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(ThemeConfig.FONT_SMALL);
        lblSubtitulo.setForeground(accentColor);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(lblTitulo);
        content.add(Box.createVerticalStrut(4));
        content.add(lblValor);
        content.add(Box.createVerticalStrut(4));
        content.add(lblSubtitulo);

        add(content, BorderLayout.CENTER);
    }

    public void setValor(String nuevoValor) {
        lblValor.setText(nuevoValor);
        repaint();
    }

    public void setSubtitulo(String sub) {
        lblSubtitulo.setText(sub);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo redondeado blanco
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        // Borde fino
        g2.setColor(ThemeConfig.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        // Tira de acento en el lateral izquierdo
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 5, getHeight() - 1, 4, 4);

        g2.dispose();
    }
}
