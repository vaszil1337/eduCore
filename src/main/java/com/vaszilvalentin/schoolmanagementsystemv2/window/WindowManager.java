package com.vaszilvalentin.schoolmanagementsystemv2.window;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.vaszilvalentin.schoolmanagementsystemv2.pages.HomePage;
import com.vaszilvalentin.schoolmanagementsystemv2.pages.LandingPage;
import com.vaszilvalentin.schoolmanagementsystemv2.pages.LoginPage;
import com.vaszilvalentin.schoolmanagementsystemv2.pages.SettingsPage;
import com.vaszilvalentin.schoolmanagementsystemv2.preference.PreferenceManager;
import com.vaszilvalentin.schoolmanagementsystemv2.preference.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WindowManager {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> pages;

    public WindowManager(String landing) {
        Theme theme = PreferenceManager.loadTheme(); // Load the stored theme value
        applyTheme(theme); // Apply the theme on startup

        pages = new HashMap<>();
        SwingUtilities.invokeLater(() -> initializeWindow());
    }

    // Initialize the window and set it to full screen
    private void initializeWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Make window start in full-screen mode
        frame.setLocationRelativeTo(null);  // Center the window

        // macOS-specific window settings
        if (SystemInfo.isMacOS) {
            // Set the window title bar appearance
            frame.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);

        // Create and add the initial page (Home, Login, Settings, etc.)
        JPanel initialPage = createPage("Landing");
        addPage("Landing", initialPage);

        // Automatically resize components based on window size
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeComponents(frame.getSize());
            }
        });

        frame.setVisible(true);

        switchToPage("Landing");
    }

    // Applies the selected theme
    public static void applyTheme(Theme theme) {
        try {
            if (theme == Theme.DARK) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }

            // macOS-specific settings
            if (SystemInfo.isMacOS) {
                // Enable automatic window decorations (title bar, buttons, etc.)
                FlatLaf.setUseNativeWindowDecorations(true);

                // Enable full window content option (transparent title bar)
                JFrame.setDefaultLookAndFeelDecorated(true);

            }
            // Update all UI components globally
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add this method to refresh all pages
    public void refreshAllPages() {
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            SwingUtilities.updateComponentTreeUI(entry.getValue());
        }
    }

    // Add a new page (Panel) to the mainPanel
    private void addPage(String pageName, JPanel panel) {
        pages.put(pageName, panel);
        mainPanel.add(panel, pageName);
    }

    // Switch to a specific page by name
    public void switchToPage(String pageName) {
        if (!pages.containsKey(pageName)) {
            JPanel newPage = createPage(pageName);
            addPage(pageName, newPage);
        }
        cardLayout.show(mainPanel, pageName);
    }

    // Create a page based on the given name (Home, Settings, Login)
    private JPanel createPage(String pageName) {
        switch (pageName) {
            case "Landing":
                return new LandingPage(this);
            case "Settings":
                return new SettingsPage(this);
            case "Login":
                return new LoginPage(this);
            case "Home":
                return new HomePage(this);
            default:
                throw new IllegalArgumentException("Unknown page: " + pageName);
        }
    }

    // Global method to resize all components based on the current window size
    private void resizeComponents(Dimension frameSize) {
        double width = frameSize.getWidth();
        double height = frameSize.getHeight();

        // Resize all pages based on the current window size
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            JPanel page = entry.getValue();

            // Apply resizing logic to each page
            resizePageComponents(page, width, height);
        }

        mainPanel.revalidate();  // Revalidate layout
        mainPanel.repaint();     // Repaint the components
    }

    // Method to resize components on each page based on window size
    private void resizePageComponents(JPanel page, double width, double height) {
        // Iterate through each component on the page and resize it accordingly
        for (Component comp : page.getComponents()) {
            if (comp instanceof JTextField) {
                comp.setPreferredSize(new Dimension((int) (width * 0.4), 30));  // 40% of the window width
            } else if (comp instanceof JButton) {
                comp.setPreferredSize(new Dimension((int) (width * 0.2), 50));  // 20% of the window width
            } else if (comp instanceof JLabel) {
                comp.setFont(new Font("Arial", Font.PLAIN, (int) (height * 0.05)));  // Font size relative to window height
            }
        }
    }
}
