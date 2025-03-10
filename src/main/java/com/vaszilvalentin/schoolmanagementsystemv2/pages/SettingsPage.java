package com.vaszilvalentin.schoolmanagementsystemv2.pages;
import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;
import javax.swing.*;
import java.awt.*;

public class SettingsPage extends JPanel {
    public SettingsPage(WindowManager windowManager) {
        setBackground(Color.CYAN);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Settings Page", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JButton aboutButton = new JButton("Go to Home");
        aboutButton.addActionListener(e -> windowManager.switchToPage("Home"));
        add(aboutButton, BorderLayout.CENTER);
    }
}