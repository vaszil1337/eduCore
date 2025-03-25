package com.vaszilvalentin.schoolmanagementsystemv2.pages;

import com.vaszilvalentin.schoolmanagementsystemv2.preference.PreferenceManager;
import com.vaszilvalentin.schoolmanagementsystemv2.preference.Theme;
import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsPage extends JPanel {

    // Load the currently saved theme
    private final Theme currentTheme = PreferenceManager.loadTheme();

    public SettingsPage(WindowManager windowManager) {
        setLayout(new GridBagLayout()); // Centering content

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        container.setPreferredSize(new Dimension(400, 250));
        container.setMaximumSize(new Dimension(400, 250));

        JLabel titleLabel = new JLabel("Beállítások");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Theme Selector
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));

        JLabel themeLabel = new JLabel("Válassz témát:");
        themeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        themeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<Theme> themeSelector = new JComboBox<>(Theme.values());
        themeSelector.setSelectedItem(currentTheme);
        themeSelector.setMaximumSize(new Dimension(200, 30));
        themeSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeSelector.setFocusable(false);

        themeSelector.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Theme selectedTheme = (Theme) themeSelector.getSelectedItem();
            PreferenceManager.saveTheme(selectedTheme);
            WindowManager.applyTheme(selectedTheme);
            
            windowManager.refreshAllPages();
        }
    });

        themePanel.add(themeLabel);
        themePanel.add(Box.createVerticalStrut(5)); // Spacing
        themePanel.add(themeSelector);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton homeButton = new JButton("Vissza a főoldalra");
        homeButton.setPreferredSize(new Dimension(150, 35));
        homeButton.setFocusPainted(false);
        homeButton.addActionListener(e -> windowManager.switchToPage("Landing"));

        buttonPanel.add(homeButton);

        // Add components to container
        container.add(titleLabel);
        container.add(themePanel);
        container.add(buttonPanel);

        // Center the container
        add(container);
    }
}
