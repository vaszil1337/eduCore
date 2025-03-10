package com.vaszilvalentin.schoolmanagementsystemv2.pages;

import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    public HomePage(WindowManager windowManager) {
        setLayout(new GridBagLayout()); // Középre igazítás

        // Központi panel létrehozása (függőleges elrendezéssel)
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));

        // Címke (title)
        JLabel titleLabel = new JLabel("MySchoolSystem", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(titleLabel);

        // Gombok létrehozása
        JButton loginButton = new JButton("Bejelentkezés");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> windowManager.switchToPage("Login"));
        centerBox.add(Box.createVerticalStrut(20)); // Térköz
        centerBox.add(loginButton);

        JButton settingsButton = new JButton("Beállítások");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.addActionListener(e -> windowManager.switchToPage("Settings"));
        centerBox.add(Box.createVerticalStrut(10)); // Térköz
        centerBox.add(settingsButton);

        // Központi panel középre helyezése a fő panelen belül
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerBox, gbc);
    }
}
