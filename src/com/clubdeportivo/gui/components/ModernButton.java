package com.clubdeportivo.gui.components;

import com.clubdeportivo.gui.theme.ThemeConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón interactivo moderno con esquinas redondeadas, efectos de hover y soporte de paleta.
 *
 * @author Sara
 * @version 1.0.0
 */
public final class ModernButton extends JButton {

    private static final long serialVersionUID = 1L;
    private static final Color DISABLED_BG = new Color(226, 232, 240);
    private static final Color DISABLED_FG = new Color(71, 85, 105);

    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;
    private final Color enabledForeground;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public ModernButton(String text) {
        this(text, ThemeConfig.PRIMARY, ThemeConfig.PRIMARY_HOVER, ThemeConfig.TEXT_WHITE);
    }

    public ModernButton(String text, Color bg, Color hoverBg, Color fg) {
        super(text);
        this.normalColor = bg;
        this.hoverColor = hoverBg;
        this.pressedColor = hoverBg.darker();
        this.enabledForeground = fg;

        setFont(ThemeConfig.FONT_BOLD);
        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(8, 16, 8, 16));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isEnabled()) {
                    return;
                }
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) {
                    return;
                }
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public static ModernButton createPrimary(String text) {
        return new ModernButton(text, ThemeConfig.PRIMARY, ThemeConfig.PRIMARY_HOVER, ThemeConfig.TEXT_WHITE);
    }

    public static ModernButton createSuccess(String text) {
        return new ModernButton(text, ThemeConfig.STATUS_GREEN, new Color(20, 83, 45), ThemeConfig.TEXT_WHITE);
    }

    public static ModernButton createDanger(String text) {
        return new ModernButton(text, ThemeConfig.STATUS_RED, new Color(153, 27, 27), ThemeConfig.TEXT_WHITE);
    }

    public static ModernButton createSecondary(String text) {
        return new ModernButton(text, new Color(241, 245, 249), new Color(226, 232, 240), ThemeConfig.TEXT_MAIN);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            isHovered = false;
            isPressed = false;
        }
        setCursor(Cursor.getPredefinedCursor(enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        setForeground(enabled ? enabledForeground : DISABLED_FG);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color targetBg;
        if (!isEnabled()) {
            targetBg = DISABLED_BG;
        } else if (isPressed) {
            targetBg = pressedColor;
        } else if (isHovered) {
            targetBg = hoverColor;
        } else {
            targetBg = normalColor;
        }

        g2.setColor(targetBg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Si es secundario, dibujar un borde sutil
        if (normalColor.equals(new Color(241, 245, 249))) {
            g2.setColor(ThemeConfig.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}
