package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.users.ClassLevel;
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
 * The StudentListPanel class is a JPanel that allows teachers to select a
 * class, view a list of students from that class, and export the list to a PDF
 * file. It contains a dropdown to select the class, a table to display student
 * data, and buttons for navigating back to the home page and exporting data to
 * PDF.
 */
public class StudentListPanel extends JPanel {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> classComboBox;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();
    private final WindowManager windowManager;

    /**
     * Constructor for the StudentListPanel. Initializes the UI and listens for
     * theme changes.
     *
     * @param windowManager The WindowManager instance for page navigation
     */
    public StudentListPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        initUI();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the User Interface components for the panel.
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top panel containing the class selection dropdown
        classComboBox = new JComboBox<>();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Class:"));
        topPanel.add(classComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Table to display student details
        tableModel = new DefaultTableModel(new String[]{"#", "Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing the table cells
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30); // Set row height for readability

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Center-align all table cells
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            TableColumn column = studentTable.getColumnModel().getColumn(i);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            column.setCellRenderer(renderer);
        }

        // Bottom panel with buttons
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));

        JButton exportButton = new JButton("Export to PDF");
        exportButton.addActionListener(this::handleExport);

        JButton exportLoginsButton = null;
        User currentUser = CurrentUser.getCurrentUser();
        if ("admin".equalsIgnoreCase(currentUser.getRole())) {
            exportLoginsButton = new JButton("Export Logins to PDF");
            exportLoginsButton.addActionListener(this::handleExportLogins);
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(backButton);
        bottomPanel.add(exportButton);
        if (exportLoginsButton != null) {
            bottomPanel.add(exportLoginsButton);
        }
        add(bottomPanel, BorderLayout.SOUTH);

        // Populate the class dropdown with available classes
        populateClassComboBox();

        // ActionListener to load students when a class is selected
        classComboBox.addActionListener(e -> {
            String selected = (String) classComboBox.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                loadStudentsByClass(selected);
            }
        });
    }

    /**
     * Populates the class selection dropdown with available class IDs.
     */
    private void populateClassComboBox() {
        User currentUser = CurrentUser.getCurrentUser(); // 
        List<String> taughtClasses = currentUser.getTaughtClasses();

        if(currentUser.getRole().equals("teacher")){
            for (ClassLevel classLevel : ClassLevel.values()) {
                if (taughtClasses.contains(classLevel.getDisplayName())) {
                    classComboBox.addItem(classLevel.getDisplayName());
                }
            }
        } else {
            for (ClassLevel classLevel : ClassLevel.values()) {
                classComboBox.addItem(classLevel.getDisplayName());
            }
        }
        

        if (classComboBox.getItemCount() > 0) {
            classComboBox.setSelectedIndex(0);
            loadStudentsByClass((String) classComboBox.getSelectedItem());
        }
    }

    /**
     * Loads the students for the selected class and populates the table.
     *
     * @param classId The ID of the selected class
     */
    private void loadStudentsByClass(String classId) {
        if (classId == null || classId.isEmpty()) {
            tableModel.setRowCount(0); // Clear the table if no class is selected
            return;
        }

        // Get the list of students for the selected class
        List<User> students = UserManager.getStudentsByClass(classId);
        tableModel.setRowCount(0); // Clear table before loading new data

        // Populate the table with student data
        for (int i = 0; i < students.size(); i++) {
            User student = students.get(i);
            tableModel.addRow(new Object[]{
                i + 1,
                student.getName(),
                student.getEmail()
            });
        }
    }

    /**
     * Handles the export button click event. Allows the user to select a file
     * location and exports the student list to a PDF file.
     *
     * @param e The ActionEvent triggered by the export button click
     */
    private void handleExport(ActionEvent e) {
        String classId = (String) classComboBox.getSelectedItem();

        if (classId == null || classId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No class selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<User> students = UserManager.getStudentsByClass(classId);
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no students in the selected class.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Let the user choose a file path for the PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a location to save the PDF");
        fileChooser.setSelectedFile(new java.io.File("students_" + classId + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Export the students to PDF using the PDFExporter class
            PDFExporter.exportStudentsByClassToPDF(students, classId, filePath);
            JOptionPane.showMessageDialog(this, "PDF successfully exported to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Handles the export of student login data to a PDF file.
     *
     * @param e The ActionEvent triggered by the export logins button
     */
    private void handleExportLogins(ActionEvent e) {
        String classId = (String) classComboBox.getSelectedItem();

        if (classId == null || classId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No class selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<User> students = UserManager.getStudentsByClass(classId);
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no students in the selected class.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a location to save the PDF");
        fileChooser.setSelectedFile(new java.io.File("student_logins_" + classId + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Export login data to PDF
            PDFExporter.exportStudentLoginsToPDF(students, filePath);
            JOptionPane.showMessageDialog(this, "Login PDF successfully exported to:\n" + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Updates the component styles when the theme changes.
     */
    private void updateComponentStyles() {
        if (studentTable != null) {
            studentTable.setBackground(UIManager.getColor("Table.background"));
            studentTable.setForeground(UIManager.getColor("Table.foreground"));
            studentTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            studentTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            studentTable.repaint();
        }

        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Inner class that listens for theme changes in the Look and Feel.
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
     * Removes the theme change listener when the panel is removed from the UI.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}
