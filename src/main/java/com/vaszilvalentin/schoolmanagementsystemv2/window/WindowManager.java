package com.vaszilvalentin.schoolmanagementsystemv2.window;

import com.vaszilvalentin.schoolmanagementsystemv2.pages.HomePage;
import com.vaszilvalentin.schoolmanagementsystemv2.pages.SettingsPage;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WindowManager {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Map<String, JPanel> pages;

    public WindowManager(String home) {
        pages = new HashMap<>();
        initializeWindow();

        JPanel initialPage = createPage(home);
        addPage(home, initialPage);

        switchToPage(home);

        frame.setVisible(true);
    }

    private void initializeWindow() {
        frame = new JFrame("MySchoolSystem");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);

        frame.setLocationRelativeTo(null);
    }

    public void addPage(String pageName, JPanel panel) {
        pages.put(pageName, panel);
        mainPanel.add(panel, pageName);
    }

    public void switchToPage(String pageName) {
        if (!pages.containsKey(pageName)) {
            JPanel newPage = createPage(pageName);
            addPage(pageName, newPage);
        }
        cardLayout.show(mainPanel, pageName);
    }

    private JPanel createPage(String pageName) {
        switch (pageName) {
            case "Home":
                return new HomePage(this);
            case "Settings":
                return new SettingsPage(this);
            default:
                throw new IllegalArgumentException("Unknown page: " + pageName);
        }
    }
}
