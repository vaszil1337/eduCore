package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.users.CurrentUserSelection;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.window.WindowManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Admin user management panel that provides CRUD operations for user accounts.
 * Features include:
 * - Filtering users by email and role
 * - Viewing all users in a sortable table
 * - Creating, editing, and deleting user accounts
 * - Viewing detailed user information
 * - Theme-aware UI that responds to system theme changes
 */
public class ManageUsers extends JPanel {

    // UI Components
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField emailFilterField;
    private JComboBox<String> roleFilterComboBox;
    private JPanel filterPanel;
    
    // Dependencies
    private final WindowManager windowManager;
    
    // Listener for theme changes
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    /**
     * Constructs the user management panel.
     * @param windowManager The window manager for navigation between panels
     */
    public ManageUsers(WindowManager windowManager) {
        this.windowManager = windowManager;

        // Set up the main panel layout and padding
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialize UI components
        initializeFilterPanel();
        initializeTable();
        initializeButtons();
        loadUserData();

        // Register theme listener to maintain consistent styling
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the filter panel with email and role filter controls.
     */
    private void initializeFilterPanel() {
        filterPanel = new JPanel(new GridBagLayout());
        
        // Create a titled border with current theme colors
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, UIManager.getColor("Panel.foreground")),
                "Filter Users"
        );
        filterPanel.setBorder(border);
        
        // Set up layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Uniform padding
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Components expand horizontally
        gbc.weightx = 1.0;  // Give extra space to components

        // Email filter components
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailFilterField = new JTextField();
        emailFilterField.setPreferredSize(new Dimension(200, 25));  // Consistent sizing
        filterPanel.add(emailFilterField, gbc);

        // Role filter components
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        roleFilterComboBox = new JComboBox<>(new String[]{"All", "student", "teacher", "admin"});
        roleFilterComboBox.setPreferredSize(new Dimension(200, 25));  // Match email field size
        filterPanel.add(roleFilterComboBox, gbc);

        // Filter button positioned to the right
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;  // Span two rows
        gbc.fill = GridBagConstraints.NONE;  // Don't expand button
        JButton applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.addActionListener(e -> applyFilters());
        filterPanel.add(applyFilterButton, gbc);

        add(filterPanel, BorderLayout.NORTH);
    }

    /**
     * Initializes the user table with appropriate columns, renderers, and behaviors.
     */
    private void initializeTable() {
        // Column headers for the table
        String[] columnNames = {"ID", "Name", "Email", "Role", "Class"};
        
        // Custom table model to control editability and column types
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable direct cell editing
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Ensure proper sorting behavior by column type
                return String.class;  // All columns contain string data
            }
        };

        // Configure table appearance and behavior
        usersTable = new JTable(tableModel);
        usersTable.setAutoCreateRowSorter(true);  // Enable column sorting
        usersTable.setRowHeight(30);  // Comfortable row height
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Single row selection
        usersTable.setShowGrid(false);  // Cleaner look without grid lines
        usersTable.setIntercellSpacing(new Dimension(0, 0));  // Tight spacing

        // Apply current theme colors
        usersTable.setBackground(UIManager.getColor("Table.background"));
        usersTable.setForeground(UIManager.getColor("Table.foreground"));

        // Hide the ID column (used internally but not displayed)
        usersTable.getColumnModel().getColumn(0).setMinWidth(0);
        usersTable.getColumnModel().getColumn(0).setMaxWidth(0);
        usersTable.getColumnModel().getColumn(0).setWidth(0);

        // Custom cell renderer for consistent styling and alternating row colors
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                // Handle row conversion for sorted tables
                int modelRow = table.convertRowIndexToModel(row);
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, modelRow, column);
                
                // Center-align all cell content
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(noFocusBorder);  // Remove focus border

                // Apply selection or alternating row colors
                if (isSelected) {
                    setBackground(UIManager.getColor("Table.selectionBackground"));
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                } else {
                    setBackground(modelRow % 2 == 0
                            ? UIManager.getColor("Table.background")
                            : UIManager.getColor("Table.alternateRowColor"));
                    setForeground(UIManager.getColor("Table.foreground"));
                }
                return this;
            }
        };

        // Apply the custom renderer to all columns
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Customize table header appearance
        JTableHeader header = usersTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Style header with centered, bold text and custom border
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Container.borderColor")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return this;
            }
        });

        // Wrap table in scroll pane with proper theming
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIManager.getColor("Table.background"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the action buttons panel with navigation and CRUD operations.
     */
    private void initializeButtons() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));  // Consistent padding

        // Left-aligned back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        backButton.setPreferredSize(new Dimension(120, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));  // Comfortable button padding
        leftPanel.add(backButton);

        // Right-aligned action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Action buttons with consistent sizing
        JButton refreshButton = new JButton("Refresh");
        JButton createButton = new JButton("Create User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton detailsButton = new JButton("User Details");

        // Uniform button sizing
        Dimension buttonSize = new Dimension(150, 30);
        refreshButton.setPreferredSize(buttonSize);
        createButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        detailsButton.setPreferredSize(buttonSize);

        // Attach action handlers
        refreshButton.addActionListener(e -> {
            // Reset filters and reload data
            emailFilterField.setText("");
            roleFilterComboBox.setSelectedIndex(0);
            loadUserData();
        });
        createButton.addActionListener(this::createUser);
        editButton.addActionListener(this::editUser);
        deleteButton.addActionListener(this::deleteUser);
        detailsButton.addActionListener(this::showUserDetails);

        // Add buttons to panel in logical order
        rightPanel.add(refreshButton);
        rightPanel.add(createButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);
        rightPanel.add(detailsButton);

        // Combine left and right button panels
        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads user data from UserManager and populates the table.
     * Resets any existing sorting.
     */
    private void loadUserData() {
        tableModel.setRowCount(0);  // Clear existing data
        List<User> users = UserManager.getAllUsers();

        // Add each user as a row in the table
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getClassId() != null ? user.getClassId() : "N/A"  // Handle null class IDs
            });
        }
        
        // Clear any existing sorting
        usersTable.setRowSorter(null);
    }

    /**
     * Applies the current filter criteria to the user table.
     * Filters by email (contains) and role (exact match).
     */
    private void applyFilters() {
        String emailFilter = emailFilterField.getText().trim().toLowerCase();
        String roleFilter = (String) roleFilterComboBox.getSelectedItem();

        tableModel.setRowCount(0);  // Reset table
        List<User> users = UserManager.getAllUsers();

        // Apply filters to each user
        for (User user : users) {
            boolean emailMatches = user.getEmail().toLowerCase().contains(emailFilter);
            boolean roleMatches = "All".equals(roleFilter) || user.getRole().equals(roleFilter);

            if (emailMatches && roleMatches) {
                tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getClassId() != null ? user.getClassId() : "N/A"
                });
            }
        }
    }

    /**
     * Handles the create user action by navigating to the user creation form.
     * @param e The action event
     */
    private void createUser(ActionEvent e) {
        CurrentUserSelection.clear();  // Ensure we're creating a new user
        CreateUserPanel.refreshCurrentInstance();
        windowManager.switchToPage("CreateUser");
    }

    /**
     * Handles the edit user action by navigating to the user edit form.
     * Validates that a user is selected before proceeding.
     * @param e The action event
     */
    private void editUser(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }

        // Pass the selected user ID to the edit form
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        CurrentUserSelection.setEditUserId(userId);
        EditUserPanel.refreshCurrentInstance();
        windowManager.switchToPage("EditUser");
    }

    /**
     * Handles the delete user action with confirmation dialog.
     * Validates selection and confirms before deletion.
     * @param e The action event
     */
    private void deleteUser(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        // Get user details for confirmation message
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);

        // Confirm deletion with the user
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete user: " + userName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            UserManager.deleteUser(userId);  // Perform deletion
            tableModel.removeRow(selectedRow);  // Update UI
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        }
    }

    /**
     * Handles viewing detailed user information.
     * Validates selection before navigation.
     * @param e The action event
     */
    private void showUserDetails(ActionEvent e) {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to view details.");
            return;
        }

        // Pass the selected user ID to the details view
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        CurrentUserSelection.setViewUserId(userId);
        ViewUserPanel.refreshCurrentInstance();
        windowManager.switchToPage("UserDetails");
    }

    /**
     * Updates component styles when the theme changes.
     * Ensures UI remains consistent with the current look and feel.
     */
    private void updateComponentStyles() {
        if (usersTable != null) {
            // Update table and header colors
            usersTable.setBackground(UIManager.getColor("Table.background"));
            usersTable.setForeground(UIManager.getColor("Table.foreground"));
            usersTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            usersTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            usersTable.repaint();
        }
        
        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, UIManager.getColor("Panel.foreground")),
                "Filter Users"
        );
        filterPanel.setBorder(border);

        // Update panel hierarchy colors
        Component parent = getParent();
        if (parent != null) {
            parent.setBackground(UIManager.getColor("Panel.background"));
        }

        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Listener for theme change events to dynamically update UI styling.
     */
    private class ThemeChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Respond to look and feel changes
            if ("lookAndFeel".equals(evt.getPropertyName())
                    || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> updateComponentStyles());
            }
        }
    }

    /**
     * Clean up when panel is removed by unregistering theme listener.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}