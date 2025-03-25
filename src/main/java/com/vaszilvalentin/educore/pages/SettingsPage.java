package com.vaszilvalentin.educore.pages;

import com.vaszilvalentin.educore.preference.PreferenceManager;
import com.vaszilvalentin.educore.preference.Theme;
import com.vaszilvalentin.educore.window.WindowManager;
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
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        container.setPreferredSize(new Dimension(500, 350)); // Increased size
        container.setMaximumSize(new Dimension(500, 350));

        JLabel titleLabel = new JLabel("Beállítások");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Increased font size
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Theme Selector
        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));

        JLabel themeLabel = new JLabel("Válassz témát:");
        themeLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Increased font size
        themeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<Theme> themeSelector = new JComboBox<>(Theme.values());
        themeSelector.setSelectedItem(currentTheme);
        themeSelector.setMaximumSize(new Dimension(150, 80)); // Increased size
        themeSelector.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeSelector.setFocusable(false);
        themeSelector.setFont(new Font("Arial", Font.PLAIN, 15)); // Increased font size inside combobox

        themeSelector.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Theme selectedTheme = (Theme) themeSelector.getSelectedItem();
                PreferenceManager.saveTheme(selectedTheme);
                WindowManager.applyTheme(selectedTheme);
                
                windowManager.refreshAllPages();
            }
        });

        themePanel.add(themeLabel);
        themePanel.add(Box.createVerticalStrut(10)); // Increased spacing
        themePanel.add(themeSelector);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton homeButton = new JButton("Vissza a főoldalra");
        homeButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Increased font size
        homeButton.setPreferredSize(new Dimension(200, 50)); // Increased size
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