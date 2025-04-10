package com.vaszilvalentin.educore.components;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

public class ResizableImage extends JPanel {
    private Image img;

    public ResizableImage(String imgPath) {
        img = new ImageIcon(imgPath).getImage();
        this.setOpaque(false);
        
        // ComponentListener for listening JPanel changes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint(); // Repaint if changes occured
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            // Resizing image according to panel height
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
