package com.vaszilvalentin.educore.pages;

import com.vaszilvalentin.educore.window.WindowManager;

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
        JLabel titleLabel = new JLabel("eduCore", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Increased title font size
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align the title to the center
        centerBox.add(titleLabel);

        Dimension buttonSize = new Dimension(250, 60); // Increased button size

        // Create login button and add action listener to switch to login page
        JButton loginButton = new JButton("Bejelentkezés");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Increased font size
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        loginButton.setMaximumSize(buttonSize);
        loginButton.setMinimumSize(buttonSize);
        loginButton.addActionListener(e -> windowManager.switchToPage("Login")); // Switch to login page when clicked
        centerBox.add(Box.createVerticalStrut(30)); // Increased vertical space
        centerBox.add(loginButton);

        // Create settings button and add action listener to switch to settings page
        JButton settingsButton = new JButton("Beállítások");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Increased font size
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button
        settingsButton.setMinimumSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);
        settingsButton.addActionListener(e -> windowManager.switchToPage("Settings")); // Switch to settings page when clicked
        centerBox.add(Box.createVerticalStrut(20)); // Increased vertical space
        centerBox.add(settingsButton);

        // Add the central panel to the main panel and center it using GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // Center the components in the grid
        add(centerBox, gbc);
    }
}