/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.educore.pages;

/**
 *
 * @author vaszilvalentin
 */

import com.vaszilvalentin.educore.auth.AuthManager;
import com.vaszilvalentin.educore.window.WindowManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JLabel statusLabel;

    public LoginPage(WindowManager windowManager) {
        setLayout(new GridBagLayout()); // Center alignment for the panel

        // Create the central panel with vertical layout (BoxLayout)
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));

        // Title label (center-aligned)
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(titleLabel);

        // Email field
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align left
        emailPanel.add(new JLabel("Username:"));
        emailField = new JTextField(15);
        emailPanel.add(emailField);
        centerBox.add(emailPanel);

        // Password field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);
        centerBox.add(passwordPanel);

        // Show password checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheckBox.setBackground(new Color(240, 240, 240));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        centerBox.add(showPasswordCheckBox);

        // Status label (for error/success messages)
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerBox.add(Box.createVerticalStrut(10)); // Space before status label
        centerBox.add(statusLabel);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(100, 149, 237)); // Blue button
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> login(windowManager));
        centerBox.add(Box.createVerticalStrut(20)); // Space before button
        centerBox.add(loginButton);

        // Create GridBagConstraints to center the panel in the window
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(centerBox, gbc);
    }

    // Toggle password visibility (show/hide)
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0); // Show password
        } else {
            passwordField.setEchoChar('•'); // Hide password
        }
    }

    // Login action handler
    private void login(WindowManager windowManager) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Authenticate using AuthManager
        boolean isAuthenticated = AuthManager.authenticate(email, password);

        if (isAuthenticated) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            // Switch to the next page (HomePage)
            windowManager.switchToPage("Home");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }
}

