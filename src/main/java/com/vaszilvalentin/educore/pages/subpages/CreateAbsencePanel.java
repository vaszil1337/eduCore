package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.absence.AbsenceCertificate;
import com.vaszilvalentin.educore.absence.AbsenceCertificateManager;
import com.vaszilvalentin.educore.window.WindowManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.auth.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Swing panel that enables teachers to create absence certificates for students.
 * Provides class and student selection dropdowns along with date range input fields.
 * Implements theme change support through PropertyChangeListener.
 */
public class CreateAbsencePanel extends JPanel {

    // Window management reference for navigation
    private final WindowManager windowManager;
    
    // Singleton instance tracking for refresh functionality
    private static CreateAbsencePanel currentInstance;
    
    // Listener for theme changes
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // UI Components
    private JComboBox<String> classComboBox;      // Dropdown for class selection
    private JComboBox<String> studentComboBox;    // Dropdown for student selection
    private JTextField startDateField;            // Input for absence start date
    private JTextField endDateField;              // Input for absence end date
    
    // Data stores for dropdown mappings
    private final Map<String, String> classIdMap = new HashMap<>();  // Maps class display names to IDs
    private final Map<String, User> studentMap = new HashMap<>();    // Maps student display strings to User objects

    /**
     * Constructs a new CreateAbsencePanel with the specified WindowManager.
     * 
     * @param windowManager The WindowManager instance for page navigation
     */
    public CreateAbsencePanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the panel UI components and layout.
     * Clears existing components and builds the form from scratch.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));
        
        // Create and add the main form panel
        add(createFormPanel());
        
        // Refresh the display
        revalidate();
        repaint();
        
        // Load students for the initially selected class
        if (classComboBox.getItemCount() > 0) {
            updateStudentComboBox();
        }
    }

    /**
     * Creates and configures the main form panel containing all input fields.
     * 
     * @return The fully configured JPanel containing the form elements
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setBackground(UIManager.getColor("Table.background"));
        formPanel.setPreferredSize(new Dimension(600, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);

        // Title Label
        JLabel titleLabel = new JLabel("Create New Absence Certificate");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Class Selection
        gbc.gridy++;
        formPanel.add(createLabel("Class:", labelFont), gbc);
        classComboBox = new JComboBox<>();
        classComboBox.setFont(getFont().deriveFont(14f));
        loadTeacherClasses();
        classComboBox.addActionListener(e -> updateStudentComboBox());
        gbc.gridy++;
        formPanel.add(classComboBox, gbc);

        // Student Selection
        gbc.gridy++;
        formPanel.add(createLabel("Student:", labelFont), gbc);
        studentComboBox = new JComboBox<>();
        studentComboBox.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(studentComboBox, gbc);

        // Start Date Field
        gbc.gridy++;
        formPanel.add(createLabel("Start Date (yyyy-MM-dd):", labelFont), gbc);
        startDateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        startDateField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(startDateField, gbc);

        // End Date Field
        gbc.gridy++;
        formPanel.add(createLabel("End Date (yyyy-MM-dd):", labelFont), gbc);
        endDateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        endDateField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(endDateField, gbc);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> windowManager.switchToPage("TeacherAbsence"));
        
        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createAbsence());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        gbc.gridy++;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    /**
     * Loads the classes taught by the current teacher into the class combo box.
     * Automatically selects the first class if available.
     */
    private void loadTeacherClasses() {
        classIdMap.clear();
        classComboBox.removeAllItems();
        
        User currentTeacher = CurrentUser.getCurrentUser();
        if (currentTeacher != null && currentTeacher.getTaughtClasses() != null) {
            for (String classId : currentTeacher.getTaughtClasses()) {
                String displayName = "Class " + classId;
                classIdMap.put(displayName, classId);
                classComboBox.addItem(displayName);
            }
            
            // Auto-select first class if available
            if (classComboBox.getItemCount() > 0) {
                classComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * Updates the student combo box based on the currently selected class.
     * Loads students belonging to the selected class.
     */
    private void updateStudentComboBox() {
        studentMap.clear();
        studentComboBox.removeAllItems();
        
        String selectedClass = (String) classComboBox.getSelectedItem();
        if (selectedClass == null) return;
        
        String classId = classIdMap.get(selectedClass);
        List<User> students = UserManager.getStudentsByClass(classId);
        
        // Populate student dropdown with formatted display names
        for (User student : students) {
            String displayName = student.getName() + " (" + student.getEmail() + ")";
            studentMap.put(displayName, student);
            studentComboBox.addItem(displayName);
        }
    }

    /**
     * Creates a consistently styled label with the specified text and font.
     * 
     * @param text The label text to display
     * @param font The font to use for the label
     * @return The configured JLabel instance
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Validates input and creates a new absence certificate.
     * Handles date parsing and validation before creating the certificate.
     */
    private void createAbsence() {
        // Validate student selection
        String studentDisplay = (String) studentComboBox.getSelectedItem();
        if (studentDisplay == null || studentDisplay.isEmpty()) {
            showError("Please select a student.");
            return;
        }
        User student = studentMap.get(studentDisplay);
        
        // Parse and validate dates
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startDateField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
            endDate = LocalDate.parse(endDateField.getText(), DateTimeFormatter.ISO_LOCAL_DATE);
            
            if (endDate.isBefore(startDate)) {
                showError("End date cannot be before start date.");
                return;
            }
        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use yyyy-MM-dd.");
            return;
        }

        // Create new absence certificate
        AbsenceCertificate absence = new AbsenceCertificate(
            student.getId(),
            "-", // Placeholder for file path (to be added later)
            startDate,
            endDate,
            "-"  // Placeholder for absence type (to be specified by student)
        );
        
        // Save the certificate and return to absence management page
        AbsenceCertificateManager.addCertificate(absence);
        showMessage("Absence recorded successfully!");
        windowManager.switchToPage("TeacherAbsence");
    }

    /**
     * Displays an error message dialog with the specified message.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an information message dialog with the specified message.
     * 
     * @param message The message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refreshes the current panel instance if it exists.
     * Used to update the UI after external changes.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.initPanel();
        }
    }

    /**
     * Inner class to handle theme change events.
     * Reinitializes the panel when the look and feel changes.
     */
    private class ThemeChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName()) || 
                evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> initPanel());
            }
        }
    }

    /**
     * Cleans up resources when the panel is removed from the container.
     * Removes the theme change listener to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}