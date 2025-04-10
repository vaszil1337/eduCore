/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.vaszilvalentin.educore.pages;

import com.vaszilvalentin.educore.auth.AuthManager;
import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.window.WindowManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author vaszilvalentin
 */

/**
 * Home page panel, dynamically shows buttons depending on user role.
 */
public class HomePage extends javax.swing.JPanel {

    /**
     * Creates new form HomePage
     */
    WindowManager windowManager;
    User user;

    public HomePage(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.user = CurrentUser.getCurrentUser();

        initComponents();

        // Listen for theme changes to update component styling accordingly
        UIManager.addPropertyChangeListener(new ThemeChangeListener());

        // Set the initial background and border color based on the current theme
        updateContainerColor();

        // Prepare layout for navigation buttons
        initButtonLayout();

        // Create navigation buttons based on user role
        generateButtonsForRole(user.getRole());
    }

    /**
     * Initializes layout manager for the button panel.
     */
    private void initButtonLayout() {
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
    }

    /**
     * Adds a navigation button to the button panel.
     *
     * @param label      Text shown on the button
     * @param targetPage Target page to navigate to
     * @param row        Grid row position for layout
     */
    private void addNavButton(String label, String targetPage, int row) {
        JButton button = new JButton(label);
        button.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        button.setPreferredSize(new Dimension(250, 40));
        button.addActionListener(e -> windowManager.switchToPage(targetPage));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(10, 0, 10, 0); // Vertical spacing between buttons
        gbc.anchor = GridBagConstraints.CENTER;

        buttonPanel.add(button, gbc);
    }

    /**
     * Creates and adds navigation buttons based on the role of the current user.
     *
     * @param role Current user's role
     */
    private void generateButtonsForRole(String role) {
        int row = 0;

        if (role.equals("student")) {
            addNavButton("Homework", "StudentHomework", row++);
            addNavButton("Absences", "StudentAbsences", row++);
            addNavButton("My Profile", "Profile", row++);

        } else if (role.equals("teacher")) {
            addNavButton("Assignments", "TeacherHomework", row++);
            addNavButton("Enter Absences", "AbsenceEntry", row++);
            addNavButton("Student List", "StudentList", row++);
            addNavButton("My Profile", "Profile", row++);

        } else if (role.equals("admin")) {
            addNavButton("New User", "CreateUser", row++);
            addNavButton("Manage Users", "ManageUsers", row++);
            addNavButton("My Profile", "Profile", row++);
        }
    }

    /**
     * Updates the background and border of the container to match the current theme.
     */
    private void updateContainerColor() {
        container.setBackground(UIManager.getColor("LoginForm.background"));
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        UIManager.getColor("Container.borderColor"),
                        1,
                        true
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }

    /**
     * Listener that reacts to changes in Look and Feel (themes).
     */
    private class ThemeChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName())
                    || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> {
                    updateContainerColor();
                });
            }
        }
    }

    /**
     * Called when the component is removed. Cleans up UI listeners to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        PropertyChangeListener[] listeners = UIManager.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            if (listener instanceof ThemeChangeListener) {
                UIManager.removePropertyChangeListener(listener);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        container = new javax.swing.JPanel();
        mid = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        welcomePanel = new javax.swing.JPanel();
        welcomeLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        bot = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        logoutButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        container.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(92, 158, 173)));
        container.setPreferredSize(new java.awt.Dimension(800, 300));
        container.setLayout(new java.awt.GridBagLayout());

        mid.setOpaque(false);
        mid.setLayout(new java.awt.GridBagLayout());

        centerPanel.setOpaque(false);
        centerPanel.setLayout(new java.awt.GridBagLayout());

        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new java.awt.BorderLayout());

        welcomeLabel.setText("Welcome, "+ user.getName());
        welcomeLabel.setFont(new java.awt.Font("Helvetica Neue", 0, 48)); // NOI18N
        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        centerPanel.add(welcomePanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        centerPanel.add(buttonPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weighty = 1.0;
        mid.add(centerPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        container.add(mid, gridBagConstraints);

        bot.setOpaque(false);
        bot.setLayout(new java.awt.BorderLayout());

        controlPanel.setOpaque(false);

        logoutButton.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        logoutButton.setText("Log out");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutButton)
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logoutButton)
                .addGap(27, 27, 27))
        );

        bot.add(controlPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        container.add(bot, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(container, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        JOptionPane.showMessageDialog(this, "Logout successful!");
        AuthManager.logout();
    }//GEN-LAST:event_logoutButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bot;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel container;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JPanel mid;
    private javax.swing.JLabel welcomeLabel;
    private javax.swing.JPanel welcomePanel;
    // End of variables declaration//GEN-END:variables
}
