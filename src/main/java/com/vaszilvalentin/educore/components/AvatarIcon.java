package com.vaszilvalentin.educore.components;

import javax.swing.*;
import java.awt.*;

/**
 * Custom Icon implementation for circular user avatars with initials.
 * Displays a circular background with centered text initials.
 */
public class AvatarIcon implements Icon {
    private final String text;
    private final int size;

    /**
     * Creates a new AvatarIcon with the specified text and size.
     * 
     * @param text The initials to display (typically 1-2 characters)
     * @param size The diameter of the avatar circle in pixels
     */
    public AvatarIcon(String text, int size) {
        this.text = text;
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Get colors from current look and feel
        Color bgColor = UIManager.getColor("Panel.background");
        Color fgColor = UIManager.getColor("Panel.foreground");
        
        // Fallback colors if not defined in theme
        if (bgColor == null) bgColor = Color.LIGHT_GRAY;
        if (fgColor == null) fgColor = Color.BLACK;

        // Draw circle background
        g2d.setColor(bgColor);
        g2d.fillOval(x, y, size, size);

        // Draw centered text
        g2d.setColor(fgColor);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2d.drawString(text, x + (size - textWidth) / 2, y + (size + textHeight) / 2 - 10);
        
        g2d.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}