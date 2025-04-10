/**
 * WindowManager class is responsible for managing the application's main window and pages.
 * It handles theme application, page transitions, resizing of components based on window size,
 * and macOS-specific UI configurations.
 *
 * Pages are managed using a CardLayout, allowing for dynamic page switching (Landing, Login, Home, etc.).
 * It also ensures that the correct theme (Light/Dark) is applied at startup based on user preferences.
 */
package com.vaszilvalentin.educore.window;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.util.SystemInfo;
import com.vaszilvalentin.educore.pages.HomePage;
import com.vaszilvalentin.educore.pages.LandingPage;
import com.vaszilvalentin.educore.pages.LoginPage;
import com.vaszilvalentin.educore.pages.subpages.CreateHomeworkPanel;
import com.vaszilvalentin.educore.pages.subpages.EditHomeworkPanel;
import com.vaszilvalentin.educore.pages.subpages.HomeworkDetailsPanel;
import com.vaszilvalentin.educore.pages.subpages.HomeworkPanel;
import com.vaszilvalentin.educore.pages.subpages.PasswordUpdate;
import com.vaszilvalentin.educore.pages.subpages.ProfilePanel;
import com.vaszilvalentin.educore.pages.subpages.TeacherHomeworkPanel;
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
        // Load and apply the user's stored theme preference
        Theme theme = PreferenceManager.loadTheme();
        applyTheme(theme);

        pages = new HashMap<>();
        SwingUtilities.invokeLater(this::initializeWindow);
    }

    // Initializes the application window and loads the first page
    private void initializeWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen mode
        frame.setLocationRelativeTo(null); // Center the window on the screen

        // macOS-specific UI configurations
        if (SystemInfo.isMacOS) {
            frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        }

        // Use CardLayout for managing multiple pages
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);

        frame.setJMenuBar(createMenuBar());

        // Create and add the initial page (Landing by default)
        JPanel initialPage = createPage("Landing");
        addPage("Landing", initialPage);

        // Handle component resizing dynamically
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeComponents(frame.getSize());
            }
        });

        frame.setVisible(true);
        switchToPage("Landing");
    }

    public void closeWindow() {
        if (frame != null) {
            pages.clear();
            mainPanel.removeAll();
            frame.dispose();
            frame = null;
        }
    }

    /**
     * Applies a custom theme with animation and macOS support. If an error
     * occurs during theme loading, an error dialog is shown.
     */
    public static void applyTheme(Theme theme) {
        try {
            FlatAnimatedLafChange.duration = 1000; // Animation duration in ms
            FlatAnimatedLafChange.resolution = 16; // ~60 FPS (16.6ms per frame)
            FlatAnimatedLafChange.showSnapshot();  // Start animation snapshot

            // Determine theme properties
            String themeName = theme == Theme.DARK ? "Custom Dark" : "Custom Light";
            String themePath = theme == Theme.DARK
                    ? "/themes/flatlaf-custom-dark.properties"
                    : "/themes/flatlaf-custom.properties";

            // Load theme file from resources
            InputStream themeStream = WindowManager.class.getResourceAsStream(themePath);
            if (themeStream == null) {
                throw new FileNotFoundException("Theme file not found: " + themePath);
            }

            // Apply the custom theme
            FlatLaf laf = new FlatPropertiesLaf(themeName, themeStream);
            UIManager.setLookAndFeel(laf);

            // macOS native decorations
            if (SystemInfo.isMacOS) {
                FlatLaf.setUseNativeWindowDecorations(true);
                JFrame.setDefaultLookAndFeelDecorated(true);
            }

            // Update UI for all open windows
            for (Frame frame : Frame.getFrames()) {
                SwingUtilities.updateComponentTreeUI(frame);
                if (frame instanceof JFrame) {
                    ((JFrame) frame).repaint();
                }
            }

            FlatAnimatedLafChange.hideSnapshotWithAnimation(); // Complete animation
            PreferenceManager.saveTheme(theme); // Save user preference

        } catch (Exception e) {
            // Show a dialog with the error message if theme switch fails
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "An error occurred while applying the theme:\n" + e.getMessage(),
                        "Theme Error",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

    // Adds a page to the card layout and stores it in the map
    private void addPage(String pageName, JPanel panel) {
        pages.put(pageName, panel);
        mainPanel.add(panel, pageName);
    }

    // Switch to a page by its name, creating it if it doesn't already exist
    public void switchToPage(String pageName) {
        if (!pages.containsKey(pageName)) {
            JPanel newPage = createPage(pageName);
            addPage(pageName, newPage);
        }
        cardLayout.show(mainPanel, pageName);
    }

    // Creates instances of pages based on their string identifiers
    private JPanel createPage(String pageName) {
        switch (pageName) {
            case "Landing":
                return new LandingPage(this);
            case "Login":
                return new LoginPage(this);
            case "Home":
                return new HomePage(this);
            case "PasswordUpdate":
                return new PasswordUpdate(this);
            case "StudentHomework":
                return new HomeworkPanel(this);
            case "TeacherHomework":
                return new TeacherHomeworkPanel(this);
            case "EditHomework":
                return new EditHomeworkPanel(this);
            case "CreateHomework":
                return new CreateHomeworkPanel(this);
            case "HomeworkDetails":
                return new HomeworkDetailsPanel(this);
            case "Profile":
                return new ProfilePanel(this);
            default:
                throw new IllegalArgumentException("Unknown page: " + pageName);
        }
    }

    // Creates the application's menu bar, including theme switch options
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("Settings");

        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightTheme = new JMenuItem("Light Mode");
        JMenuItem darkTheme = new JMenuItem("Dark Mode");

        lightTheme.addActionListener(e -> {
            if (!PreferenceManager.getCurrentTheme().equals(Theme.LIGHT)) {
                applyTheme(Theme.LIGHT);
                PreferenceManager.saveTheme(Theme.LIGHT);
            }
        });

        darkTheme.addActionListener(e -> {
            if (!PreferenceManager.getCurrentTheme().equals(Theme.DARK)) {
                applyTheme(Theme.DARK);
                PreferenceManager.saveTheme(Theme.DARK);
            }
        });

        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);
        settingsMenu.add(themeMenu);
        menuBar.add(settingsMenu);

        return menuBar;
    }

    // Resizes UI components on all pages proportionally to the new window size
    private void resizeComponents(Dimension frameSize) {
        double width = frameSize.getWidth();
        double height = frameSize.getHeight();

        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            JPanel page = entry.getValue();
            resizePageComponents(page, width, height);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Adjusts size and font of components on a single page
    private void resizePageComponents(JPanel page, double width, double height) {
        for (Component comp : page.getComponents()) {
            if (comp instanceof JTextField) {
                comp.setPreferredSize(new Dimension((int) (width * 0.4), 30));  // TextFields take 40% width
            } else if (comp instanceof JButton) {
                comp.setPreferredSize(new Dimension((int) (width * 0.2), 50));  // Buttons take 20% width
            } else if (comp instanceof JLabel) {
                comp.setFont(new Font("Arial", Font.PLAIN, (int) (height * 0.05)));  // Font size scaled with height
            }
        }
    }
}
