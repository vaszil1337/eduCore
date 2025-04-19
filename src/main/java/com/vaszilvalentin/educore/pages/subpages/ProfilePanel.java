package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.components.AvatarIcon;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ProfilePanel displays the user's profile information in a card layout.
 * It shows personal details, role-specific information, and provides actions
 * like password change. The panel automatically updates when the theme changes.
 */
public class ProfilePanel extends JPanel {

    private final WindowManager windowManager;
    private final User user;
    private static ProfilePanel currentInstance;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    private JPanel profileCard;
    private JLabel roleLabel, avatarLabel;

    /**
     * Constructs a new ProfilePanel with the specified WindowManager.
     * 
     * @param windowManager The WindowManager instance for navigation
     */
    public ProfilePanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.user = CurrentUser.getCurrentUser();
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the panel components and layout.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        profileCard = createProfileCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(profileCard, gbc);

        revalidate();
        repaint();
    }

    /**
     * Creates the profile card containing all user information.
     * 
     * @return The configured profile card panel
     */
    private JPanel createProfileCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 2),
                new EmptyBorder(30, 40, 30, 40)
        ));
        card.setBackground(UIManager.getColor("Table.background"));
        card.setPreferredSize(new Dimension(600, 580));

        // Avatar setup
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(130, 130));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateAvatar();
        card.add(avatarLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Role label setup
        roleLabel = new JLabel(user.getRole().toUpperCase());
        roleLabel.setFont(getFont().deriveFont(Font.BOLD, 16));
        roleLabel.setOpaque(true);
        roleLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateRoleLabelStyle();
        card.add(roleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // User information fields
        card.add(createLabeledValue("Name", user.getName()));
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        card.add(createLabeledValue("Email", user.getEmail()));
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        card.add(createLabeledValue("Age", user.getAge() > 0 ? String.valueOf(user.getAge()) : "not specified"));
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Teacher-specific information
        if ("teacher".equalsIgnoreCase(user.getRole())) {
            if (user.getSubjects() != null && !user.getSubjects().isEmpty()) {
                card.add(createLabeledValue("Subjects taught", String.join(", ", user.getSubjects())));
                card.add(Box.createRigidArea(new Dimension(0, 10)));
            }

            if (user.getTaughtClasses() != null && !user.getTaughtClasses().isEmpty()) {
                card.add(createLabeledValue("Classes taught", String.join(", ", user.getTaughtClasses())));
                card.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        // Student-specific information
        if ("student".equalsIgnoreCase(user.getRole())) {
            card.add(createLabeledValue("Class: ", user.getClassId()));
            card.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Change password button
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(140, 35));
        changePasswordButton.setFont(getFont().deriveFont(Font.BOLD, 13));
        changePasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePasswordButton.addActionListener(e -> {
            windowManager.switchToPage("PasswordUpdate");
        });
        card.add(changePasswordButton);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.setFont(getFont().deriveFont(Font.BOLD, 15));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        card.add(Box.createVerticalGlue());
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(backButton);

        return card;
    }

    /**
     * Creates a labeled value pair panel.
     * 
     * @param label The description label
     * @param value The value to display
     * @return The configured panel with label and value
     */
    private JPanel createLabeledValue(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JLabel labelPart = new JLabel(label + ": ");
        labelPart.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JLabel valuePart = new JLabel(value);
        valuePart.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(labelPart);
        panel.add(valuePart);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return panel;
    }

    /**
     * Updates the avatar icon with user initials.
     */
    private void updateAvatar() {
        String[] nameParts = user.getName().split(" ");
        String initials = nameParts.length >= 2
                ? nameParts[0].substring(0, 1) + nameParts[1].substring(0, 1)
                : user.getName().substring(0, Math.min(2, user.getName().length()));
        avatarLabel.setIcon(new AvatarIcon(initials.toUpperCase(), 130));
    }

    /**
     * Updates the role label styling based on current theme.
     */
    private void updateRoleLabelStyle() {
        if (roleLabel == null) {
            return;
        }

        Color bgColor = UIManager.getColor("Panel.background");
        Color fgColor = UIManager.getColor("Panel.foreground");

        roleLabel.setBackground(bgColor != null ? bgColor : Color.LIGHT_GRAY);
        roleLabel.setForeground(fgColor != null ? fgColor : Color.BLACK);
    }

    /**
     * Refreshes the current profile panel instance if it exists.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.refreshData();
        }
    }

    /**
     * Refreshes the panel data and layout.
     */
    private void refreshData() {
        initPanel();
    }

    /**
     * Listener for theme change events to update UI accordingly.
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
     * Updates all component styles when theme changes.
     */
    private void updateComponentStyles() {
        setBackground(UIManager.getColor("Panel.background"));
        if (profileCard != null) {
            profileCard.setBackground(UIManager.getColor("Table.background"));
            profileCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 2),
                    new EmptyBorder(40, 40, 40, 40)
            ));
        }
        updateAvatar();
        updateRoleLabelStyle();
        revalidate();
        repaint();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}