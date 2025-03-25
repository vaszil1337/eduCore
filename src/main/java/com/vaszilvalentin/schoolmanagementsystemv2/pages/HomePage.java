package com.vaszilvalentin.schoolmanagementsystemv2.pages;

import com.vaszilvalentin.schoolmanagementsystemv2.auth.CurrentUser;
import com.vaszilvalentin.schoolmanagementsystemv2.users.User;
import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    public HomePage(WindowManager windowManager) {
        setLayout(new BorderLayout());

        // Check if a user is logged in
        if (CurrentUser.isLoggedIn()) {
            User currentUser = CurrentUser.getCurrentUser();
            JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);
        } else {
            JLabel messageLabel = new JLabel("No user logged in.", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            add(messageLabel, BorderLayout.CENTER);
        }

        // Add other content (e.g., buttons) for the home page
        // ...
    }
}
