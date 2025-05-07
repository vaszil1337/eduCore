package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.utils.PDFExporter;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The TeacherListPanel class provides a graphical interface for administrators
 * to view and manage teacher information. It displays a list of all teachers
 * with their personal details, subjects taught, and assigned classes. The panel
 * supports exporting teacher data to PDF format, including an option to export
 * login credentials.
 */
public class TeacherListPanel extends JPanel {

    // UI Components
    private JTable teacherTable;
    private DefaultTableModel tableModel;

    // Theme management
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // Application window management
    private final WindowManager windowManager;

    /**
     * Constructs a new TeacherListPanel with the specified WindowManager.
     * Initializes the UI components and sets up theme change listeners.
     *
     * @param windowManager The WindowManager instance for managing application
     * navigation
     */
    public TeacherListPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        initUI();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the user interface components and layout. Creates the teacher
     * table, action buttons, and sets up their event handlers.
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialize table model with columns: #, Name, Email, Subjects, Taught Classes
        tableModel = new DefaultTableModel(new String[]{"#", "Name", "Email", "Subjects", "Taught Classes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };

        teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(30); // Set consistent row height for better readability

        // Configure scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Configure column alignments
        for (int i = 0; i < teacherTable.getColumnCount(); i++) {
            TableColumn column = teacherTable.getColumnModel().getColumn(i);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            // Left-align only the Subjects and Taught Classes columns for better text display
            renderer.setHorizontalAlignment((i == 3 || i == 4) ? SwingConstants.LEFT : SwingConstants.CENTER);
            column.setCellRenderer(renderer);
        }

        // Create action buttons
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));

        JButton exportButton = new JButton("Export to PDF");
        exportButton.addActionListener(this::handleExport);

        JButton exportLoginsButton = new JButton("Export Logins to PDF");
        exportLoginsButton.addActionListener(this::handleExportLogins);

        // Configure button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(backButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(exportLoginsButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial teacher data
        loadTeachers();
    }

    /**
     * Loads teacher data from the UserManager and populates the table. Clears
     * existing data before loading to ensure fresh display.
     */
    private void loadTeachers() {
        List<User> teachers = UserManager.getUsersByRole("teacher");
        tableModel.setRowCount(0); // Clear existing data

        // Populate table with teacher information
        for (int i = 0; i < teachers.size(); i++) {
            User teacher = teachers.get(i);
            tableModel.addRow(new Object[]{
                i + 1, // Serial number
                teacher.getName(), // Teacher's full name
                teacher.getEmail(), // Contact email
                String.join(", ", teacher.getSubjects()), // Comma-separated subjects
                String.join(", ", teacher.getTaughtClasses()) // Comma-separated classes
            });
        }
    }

    /**
     * Handles the export action for teacher data. Exports the current teacher
     * list to a PDF file at user-specified location.
     *
     * @param e The action event triggered by the export button
     */
    private void handleExport(ActionEvent e) {
        List<User> teachers = UserManager.getUsersByRole("teacher");
        if (teachers.isEmpty()) {
            showWarningDialog("There are no teachers in the system.");
            return;
        }

        JFileChooser fileChooser = createFileChooser("Choose a location to save the PDF", "teachers_list.pdf");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            PDFExporter.exportUsersByRoleToPDF(teachers, "teacher", filePath);
            showSuccessDialog("PDF successfully exported to:\n" + filePath);
        }
    }

    /**
     * Handles the export action for teacher login credentials. Available only
     * to admin users, exports login data to a PDF file.
     *
     * @param e The action event triggered by the export logins button
     */
    private void handleExportLogins(ActionEvent e) {
        List<User> teachers = UserManager.getUsersByRole("teacher");
        if (teachers.isEmpty()) {
            showWarningDialog("There are no teachers in the system.");
            return;
        }

        JFileChooser fileChooser = createFileChooser("Choose a location to save the PDF", "teacher_logins.pdf");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            PDFExporter.exportTeacherLoginsToPDF(teachers, filePath);
            showSuccessDialog("Login PDF successfully exported to:\n" + filePath);
        }
    }

    /**
     * Creates a configured file chooser dialog for export operations.
     *
     * @param title The dialog title
     * @param defaultFilename The suggested filename
     * @return Configured JFileChooser instance
     */
    private JFileChooser createFileChooser(String title, String defaultFilename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        return fileChooser;
    }

    /**
     * Displays a warning message dialog.
     *
     * @param message The warning message to display
     */
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Displays a success message dialog.
     *
     * @param message The success message to display
     */
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Updates UI component styles when the application theme changes. Ensures
     * visual consistency across different look-and-feel themes.
     */
    private void updateComponentStyles() {
        if (teacherTable != null) {
            teacherTable.setBackground(UIManager.getColor("Table.background"));
            teacherTable.setForeground(UIManager.getColor("Table.foreground"));
            teacherTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            teacherTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            teacherTable.repaint();
        }
        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Inner class that listens for theme changes in the application. Updates
     * component styles when the look-and-feel changes.
     */
    private class ThemeChangeListener implements PropertyChangeListener {

        /**
         * Responds to property change events, particularly theme changes.
         *
         * @param evt The property change event
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName())
                    || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> updateComponentStyles());
            }
        }
    }

    /**
     * Cleans up resources when the panel is removed from the UI. Removes the
     * theme change listener to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}
