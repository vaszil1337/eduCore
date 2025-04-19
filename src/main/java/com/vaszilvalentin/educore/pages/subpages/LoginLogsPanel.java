package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.log.LoginLog;
import com.vaszilvalentin.educore.log.LoginLogManager;
import com.vaszilvalentin.educore.window.WindowManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * LoginLogsPanel displays a table of login activity logs,
 * allowing filtering by email and supporting dynamic UI theme updates.
 */
public class LoginLogsPanel extends JPanel {
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private JButton refreshButton;
    private JButton filterButton;
    private JTextField emailFilterField;
    private final WindowManager windowManager;

    // Listener to update styles dynamically when the Look and Feel changes
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // Maps user IDs to emails for fast lookup
    private final Map<String, String> userEmailMap = new HashMap<>();

    // Reverse map from email to user ID, used for filtering
    private final Map<String, String> emailUserIdMap = new HashMap<>();

    /**
     * Constructor initializes the panel UI and data.
     * @param windowManager Handles navigation between application windows.
     */
    public LoginLogsPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        initializeUI();
        loadUserEmails();
        loadLogsData(null); // Load all logs initially
        UIManager.addPropertyChangeListener(themeChangeListener); // Watch for theme changes
    }

    /**
     * Loads all users and populates both email lookup maps.
     */
    private void loadUserEmails() {
        userEmailMap.clear();
        emailUserIdMap.clear();

        List<User> users = UserManager.getAllUsers();
        for (User user : users) {
            userEmailMap.put(user.getId(), user.getEmail());
            emailUserIdMap.put(user.getEmail().toLowerCase(), user.getId());
        }
    }

    /**
     * Sets up the UI components and layout for the panel.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Define column names for the log table
        String[] columnNames = {"Timestamp", "User ID", "Email", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells read-only
            }
        };

        logsTable = new JTable(tableModel);
        logsTable.setRowHeight(30);
        logsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logsTable.setShowGrid(false);
        logsTable.setIntercellSpacing(new Dimension(0, 0));

        // Center-align table cells and apply theme-based coloring
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                // Update colors based on selection and theme
                if (!isSelected) {
                    setBackground(UIManager.getColor("Table.background"));
                    setForeground(UIManager.getColor("Table.foreground"));
                } else {
                    setBackground(UIManager.getColor("Table.selectionBackground"));
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                }
                return this;
            }
        };
        logsTable.setDefaultRenderer(Object.class, centerRenderer);

        // Custom header styling
        JTableHeader header = logsTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Container.borderColor")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Table.background"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel for navigation and controls
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Back button aligned left
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        backButton.setPreferredSize(new Dimension(120, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));
        leftPanel.add(backButton);

        // Filter and refresh controls aligned right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        filterPanel.add(new JLabel("Filter by Email:"));

        emailFilterField = new JTextField();
        emailFilterField.setPreferredSize(new Dimension(200, 30));
        emailFilterField.addActionListener(this::applyFilter); // Trigger filter on Enter key

        filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(this::applyFilter);
        filterButton.setPreferredSize(new Dimension(120, 30));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            emailFilterField.setText(""); // Clear filter field
            loadUserEmails();
            loadLogsData(null); // Reload all logs
        });
        refreshButton.setPreferredSize(new Dimension(120, 30));

        filterPanel.add(emailFilterField);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);
        rightPanel.add(filterPanel);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads login logs into the table, optionally filtered by email.
     * @param emailFilter Text to filter email column (case-insensitive); null to show all.
     */
    private void loadLogsData(String emailFilter) {
        tableModel.setRowCount(0); // Clear existing data

        List<LoginLog> logs = LoginLogManager.getAllLogs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (LoginLog log : logs) {
            String email = userEmailMap.getOrDefault(log.getUserId(), "N/A");

            if (emailFilter == null || emailFilter.isEmpty() ||
                email.toLowerCase().contains(emailFilter.toLowerCase())) {
                tableModel.addRow(new Object[]{
                    log.getTimestamp().format(formatter),
                    log.getUserId(),
                    email,
                    log.getAction().toString()
                });
            }
        }
    }

    /**
     * Handles filter action triggered by button or Enter key in the text field.
     * @param e ActionEvent from the filter input
     */
    private void applyFilter(ActionEvent e) {
        String filterText = emailFilterField.getText().trim();
        loadLogsData(filterText.isEmpty() ? null : filterText);
    }

    /**
     * Updates UI colors and styles based on the current theme.
     */
    private void updateComponentStyles() {
        if (logsTable != null) {
            logsTable.setBackground(UIManager.getColor("Table.background"));
            logsTable.setForeground(UIManager.getColor("Table.foreground"));
            logsTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            logsTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            logsTable.repaint();
        }

        Component parent = getParent();
        if (parent != null) {
            parent.setBackground(UIManager.getColor("Panel.background"));
        }

        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Listener to respond to Look and Feel/theme changes and apply UI updates.
     */
    private class ThemeChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName())
                    || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> updateComponentStyles());
            }
        }
    }

    /**
     * Unregisters the theme listener when the panel is removed to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}
