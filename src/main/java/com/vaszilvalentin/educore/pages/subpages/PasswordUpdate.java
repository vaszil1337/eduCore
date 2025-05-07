package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.AuthManager;
import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This panel allows the user to update their password.
 * It includes old/new password fields, validation, and theme updates.
 */
public class PasswordUpdate extends JPanel {

    private final WindowManager windowManager;
    private JPanel cardPanel;
    private JPasswordField oldField, newpassField, confirmField;
    private JButton sendBtn;
    private JCheckBox showpassBox;

    // Listener to detect theme (LookAndFeel) changes
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    /**
     * Constructs the PasswordUpdate panel and registers the theme listener.
     */
    public PasswordUpdate(WindowManager windowManager) {
        this.windowManager = windowManager;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes and lays out the main panel content.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        cardPanel = createCardPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(cardPanel, gbc);

        revalidate();
        repaint();
    }

    /**
     * Creates and returns the central card containing form elements.
     */
    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 2),
                new EmptyBorder(30, 40, 30, 40)
        ));
        card.setBackground(UIManager.getColor("Table.background"));
        card.setPreferredSize(new Dimension(500, 530));

        // Title
        JLabel titleLabel = new JLabel("Update your password");
        titleLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // Password fields
        oldField = createPasswordField("Old Password", card);
        newpassField = createPasswordField("New Password", card);
        confirmField = createPasswordField("Confirm New Password", card);

        // Show password checkbox
        showpassBox = new JCheckBox("Show Password");
        showpassBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showpassBox.setForeground(oldField.getForeground());
        showpassBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        showpassBox.setOpaque(false);
        showpassBox.addActionListener(e -> togglePasswordVisibility());
        card.add(showpassBox);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Submit button
        sendBtn = new JButton("Change Password");
        sendBtn.setEnabled(false);
        sendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendBtn.setPreferredSize(new Dimension(160, 35));
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.addActionListener(e -> handlePasswordChange());
        card.add(sendBtn);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        card.add(Box.createVerticalGlue());

        // Back button (sticks to bottom of panel visually)
        JButton backBtn = new JButton("Back");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setPreferredSize(new Dimension(145, 40));
        backBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 16));
        backBtn.addActionListener(e -> windowManager.switchToPage("Profile"));
        card.add(backBtn);

        return card;
    }

    /**
     * Creates a labeled password field and adds it to the container.
     */
    private JPasswordField createPasswordField(String labelText, JPanel container) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(200, 30));
        field.setMaximumSize(new Dimension(250, 30));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.getDocument().addDocumentListener(documentListener);

        container.add(label);
        container.add(Box.createRigidArea(new Dimension(0, 5)));
        container.add(field);
        container.add(Box.createRigidArea(new Dimension(0, 15)));

        return field;
    }

    /**
     * Toggles visibility of password characters based on checkbox state.
     */
    private void togglePasswordVisibility() {
        char echoChar = showpassBox.isSelected() ? 0 : '•';
        oldField.setEchoChar(echoChar);
        newpassField.setEchoChar(echoChar);
        confirmField.setEchoChar(echoChar);
    }

    /**
     * Validates password fields and enables/disables the submit button accordingly.
     */
    private void checkFields() {
        String oldPassword = new String(oldField.getPassword());
        String newPassword = new String(newpassField.getPassword());
        String confirmPassword = new String(confirmField.getPassword());

        boolean isEnabled = !oldPassword.isEmpty() && !newPassword.isEmpty()
                && newPassword.equals(confirmPassword);
        sendBtn.setEnabled(isEnabled);
    }

    /**
     * Handles password update logic, including verification and user feedback.
     */
    private void handlePasswordChange() {
        User user = CurrentUser.getCurrentUser();
        boolean updated = UserManager.updateUserPassword(
                user.getId(),
                new String(oldField.getPassword()),
                new String(newpassField.getPassword())
        );

        if (updated) {
            AuthManager.reloadUsers();
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
            windowManager.switchToPage("Profile");
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect old password!");
        }
    }

    // Listener for real-time validation of password fields
    private final DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            checkFields();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkFields();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkFields();
        }
    };

    /**
     * Listener for UI LookAndFeel changes to refresh styling dynamically.
     */
    private class ThemeChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName()) || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> updateComponentStyles());
            }
        }
    }

    /**
     * Updates component colors and borders based on the current LookAndFeel.
     */
    private void updateComponentStyles() {
        setBackground(UIManager.getColor("Panel.background"));
        cardPanel.setBackground(UIManager.getColor("Table.background"));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 2),
                new EmptyBorder(30, 40, 30, 40)
        ));
        repaint();
    }

    /**
     * Removes theme listener when this panel is removed from the component hierarchy.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}
