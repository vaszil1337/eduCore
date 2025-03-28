package com.vaszilvalentin.educore.pages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    public HomePage(WindowManager windowManager) {
        setLayout(new BorderLayout());

        // Check if a user is logged in
        if (CurrentUser.isLoggedIn()) {
            JButton settingsBtn = new JButton("Settings");
            settingsBtn.addActionListener(l -> {
                windowManager.switchToPage("Settings");
            });
            add(settingsBtn, BorderLayout.CENTER);
            
        } else {
            JLabel messageLabel = new JLabel("No user logged in.", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            add(messageLabel, BorderLayout.CENTER);
        }

        // Add other content (e.g., buttons) for the home page
        // ...
    }
}
