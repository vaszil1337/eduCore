package com.vaszilvalentin.schoolmanagementsystemv2.pages;

import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;

import javax.swing.*;
import java.awt.*;

public class LandingPage extends JPanel {

    // Constructor for LandingPage
    public LandingPage(WindowManager windowManager) {
        setLayout(new GridBagLayout()); // Center-align the layout

        // Create a central panel with a vertical layout
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));

        // Title label
        JLabel titleLabel = new JLabel("MySchoolSystem", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set title font
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align the title to the center
        centerBox.add(titleLabel);

        Dimension buttonSize = new Dimension(200, 40); // Width: 200px, Height: 40px

        // Create login button and add action listener to switch to login page
        JButton loginButton = new JButton("Bejelentkezés");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        loginButton.setMaximumSize(buttonSize);
        loginButton.setMinimumSize(buttonSize);
        loginButton.addActionListener(e -> windowManager.switchToPage("Login")); // Switch to login page when clicked
        centerBox.add(Box.createVerticalStrut(20)); // Add vertical space between elements
        centerBox.add(loginButton);

        // Create settings button and add action listener to switch to settings page
        JButton settingsButton = new JButton("Beállítások");
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        settingsButton.setMinimumSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);
        settingsButton.addActionListener(e -> windowManager.switchToPage("Settings")); // Switch to settings page when clicked
        centerBox.add(Box.createVerticalStrut(10)); // Add vertical space between elements
        centerBox.add(settingsButton);

        // Add the central panel to the main panel and center it using GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the components in the grid
        add(centerBox, gbc);
    }
}
