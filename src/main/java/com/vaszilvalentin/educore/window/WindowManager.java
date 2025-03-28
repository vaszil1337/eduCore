/**
 * WindowManager class is responsible for managing the application's main window and pages.
 * It handles theme application, page transitions, resizing of components based on window size,
 * and macOS-specific UI configurations.
 * The window can switch between different pages (Landing, Settings, Login, Home) using a CardLayout.
 * It also ensures the correct theme is applied at startup based on the user's preferences.
 * 
*/

package com.vaszilvalentin.educore.window;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.vaszilvalentin.educore.pages.HomePage;
import com.vaszilvalentin.educore.pages.LandingPage;
import com.vaszilvalentin.educore.pages.LoginPage;
import com.vaszilvalentin.educore.pages.SettingsPage;
import com.vaszilvalentin.educore.pages.subpages.PasswordUpdate;
import com.vaszilvalentin.educore.preference.PreferenceManager;
import com.vaszilvalentin.educore.preference.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class WindowManager {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> pages;

    public WindowManager(String landing) {
        // Load the stored theme value and apply it
        Theme theme = PreferenceManager.loadTheme();
        applyTheme(theme);

        pages = new HashMap<>();
        SwingUtilities.invokeLater(() -> initializeWindow());
    }

    // Initialize the window and set it to full screen
    private void initializeWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Start in full-screen mode
        frame.setLocationRelativeTo(null);  // Center the window

        // macOS-specific window settings
        if (SystemInfo.isMacOS) {
            // Set the window title bar appearance for macOS
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);

        // Create and add the initial landing page
        JPanel initialPage = createPage("Landing");
        addPage("Landing", initialPage);

        // Automatically resize components when the window is resized
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeComponents(frame.getSize());
            }
        });

        frame.setVisible(true);
        switchToPage("Landing");
    }

    // Apply the selected theme (Light or Dark)
    public static void applyTheme(Theme theme) {
        try {
            // Determine theme name and properties file path
            String themeName = theme == Theme.DARK ? "Custom Dark" : "Custom Light";
            String themePath = theme == Theme.DARK 
                ? "/themes/flatlaf-custom-dark.properties" 
                : "/themes/flatlaf-custom.properties";
            
            // Load the properties file for the theme
            InputStream themeStream = WindowManager.class.getResourceAsStream(themePath);
            if (themeStream == null) {
                throw new FileNotFoundException("Theme file not found: " + themePath);
            }
            
            // Create FlatLaf with the appropriate theme
            FlatLaf laf = new FlatPropertiesLaf(themeName, themeStream);
            UIManager.setLookAndFeel(laf);

            // macOS-specific settings
            if (SystemInfo.isMacOS) {
                // Enable native window decorations (title bar, buttons)
                FlatLaf.setUseNativeWindowDecorations(true);
                JFrame.setDefaultLookAndFeelDecorated(true);
            }
            
            // Update UI components globally
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh all pages by updating their component trees
    public void refreshAllPages() {
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            SwingUtilities.updateComponentTreeUI(entry.getValue());
        }
    }

    // Add a new page to the main panel
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

    // Create a page based on the given name (Landing, Settings, Home, etc.)
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
            case "PasswordUpdate":
                return new PasswordUpdate(this);
            default:
                throw new IllegalArgumentException("Unknown page: " + pageName);
        }
    }

    // Resize components on all pages based on the window size
    private void resizeComponents(Dimension frameSize) {
        double width = frameSize.getWidth();
        double height = frameSize.getHeight();

        // Resize all pages based on window size
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            JPanel page = entry.getValue();
            resizePageComponents(page, width, height);
        }

        mainPanel.revalidate();  // Revalidate the layout
        mainPanel.repaint();     // Repaint the components
    }

    // Resize components on a specific page based on window size
    private void resizePageComponents(JPanel page, double width, double height) {
        for (Component comp : page.getComponents()) {
            if (comp instanceof JTextField) {
                comp.setPreferredSize(new Dimension((int) (width * 0.4), 30));  // 40% of window width
            } else if (comp instanceof JButton) {
                comp.setPreferredSize(new Dimension((int) (width * 0.2), 50));  // 20% of window width
            } else if (comp instanceof JLabel) {
                comp.setFont(new Font("Arial", Font.PLAIN, (int) (height * 0.05)));  // Font size relative to height
            }
        }
    }
}