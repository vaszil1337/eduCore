package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.AuthManager;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.Subject;
import com.vaszilvalentin.educore.users.ClassLevel;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Swing panel for creating new users (students or teachers) with form-based input.
 * Features responsive layout, role-specific fields, and theme support.
 */
public class CreateUserPanel extends JPanel {

    // Window management reference for navigation
    private final WindowManager windowManager;
    
    // Singleton reference for refresh functionality
    private static CreateUserPanel currentInstance;
    
    // Listener for theme changes
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // Form input components
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JTextField birthDateField;
    private JPanel subjectsPanel;
    private JPanel classesPanel;
    private JComboBox<String> studentClassComboBox;

    /**
     * Constructs a new CreateUserPanel with window management capability.
     * @param windowManager The application's window manager for page navigation
     */
    public CreateUserPanel(WindowManager windowManager) {
        currentInstance = this;
        this.windowManager = windowManager;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes and configures the main panel components.
     * Creates the form layout with left/right sections and action buttons.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout()); // Centered layout for the entire panel

        // Main form container box
        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setPreferredSize(new Dimension(700, 700));
        formBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 20, 30, 20) // Inner padding
        ));
        formBox.setBackground(UIManager.getColor("Table.background"));

        // Content panel with left/right sections
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(createLeftPanel());  // Basic information
        contentPanel.add(createRightPanel()); // Role-specific information
        formBox.add(contentPanel);
        formBox.add(Box.createVerticalStrut(10)); // Spacing before buttons

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> windowManager.switchToPage("ManageUsers"));

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createUser());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        formBox.add(buttonPanel);

        // Center the form box in the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(formBox, gbc);

        revalidate();
        repaint();
    }

    /**
     * Creates the left panel containing basic user information fields.
     * @return Configured JPanel with name, email, role, and birth date inputs
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        leftPanel.setBackground(UIManager.getColor("Table.background"));
        leftPanel.setPreferredSize(new Dimension(300, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0); // Component spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Section title
        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Basic Information");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Name field
        gbc.gridy++;
        leftPanel.add(createLabel("Full Name:", labelFont), gbc);
        nameField = new JTextField(25);
        nameField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(nameField, gbc);

        // Email field
        gbc.gridy++;
        leftPanel.add(createLabel("Email:", labelFont), gbc);
        emailField = new JTextField(25);
        emailField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(emailField, gbc);

        // Role selection
        gbc.gridy++;
        leftPanel.add(createLabel("Role:", labelFont), gbc);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Teacher"});
        roleComboBox.setFont(getFont().deriveFont(14f));
        roleComboBox.setForeground(UIManager.getColor("TextField.foreground"));
        roleComboBox.addItemListener(e -> onRoleChanged(e));
        gbc.gridy++;
        leftPanel.add(roleComboBox, gbc);

        // Birth date field
        gbc.gridy++;
        leftPanel.add(createLabel("Birth Date (YYYY-MM-DD):", labelFont), gbc);
        birthDateField = new JTextField(25);
        birthDateField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(birthDateField, gbc);

        return leftPanel;
    }

    /**
     * Creates the right panel with role-specific information fields.
     * @return Configured JPanel with dynamic fields for students/teachers
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        rightPanel.setBackground(UIManager.getColor("Table.background"));
        rightPanel.setPreferredSize(new Dimension(300, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Section title
        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Role-Specific Information");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Student class selection (only visible for student role)
        gbc.gridy++;
        rightPanel.add(createLabel("Class (for students):", labelFont), gbc);
        studentClassComboBox = new JComboBox<>();
        for (ClassLevel classLevel : ClassLevel.values()) {
            studentClassComboBox.addItem(classLevel.toString());
        }
        studentClassComboBox.setFont(getFont().deriveFont(14f));
        studentClassComboBox.setForeground(UIManager.getColor("TextField.foreground"));
        studentClassComboBox.setEnabled("Student".equals(roleComboBox.getSelectedItem()));
        gbc.gridy++;
        rightPanel.add(studentClassComboBox, gbc);

        // Teacher subjects (only visible for teacher role)
        gbc.gridy++;
        rightPanel.add(createLabel("Subjects (for teachers):", labelFont), gbc);
        subjectsPanel = new JPanel();
        subjectsPanel.setLayout(new BoxLayout(subjectsPanel, BoxLayout.Y_AXIS));
        subjectsPanel.setOpaque(false);
        for (Subject subject : Subject.values()) {
            JCheckBox subjectCheckBox = new JCheckBox(subject.toString());
            subjectCheckBox.setEnabled("Teacher".equals(roleComboBox.getSelectedItem()));
            subjectCheckBox.setForeground(UIManager.getColor("TextField.foreground"));
            subjectsPanel.add(subjectCheckBox);
        }
        gbc.gridy++;
        rightPanel.add(subjectsPanel, gbc);

        // Teacher classes (only visible for teacher role)
        gbc.gridy++;
        rightPanel.add(createLabel("Taught Classes (for teachers):", labelFont), gbc);
        classesPanel = new JPanel();
        classesPanel.setLayout(new BoxLayout(classesPanel, BoxLayout.Y_AXIS));
        classesPanel.setOpaque(false);
        for (ClassLevel classLevel : ClassLevel.values()) {
            JCheckBox classCheckBox = new JCheckBox(classLevel.toString());
            classCheckBox.setEnabled("Teacher".equals(roleComboBox.getSelectedItem()));
            classCheckBox.setForeground(UIManager.getColor("TextField.foreground"));
            classesPanel.add(classCheckBox);
        }
        gbc.gridy++;
        rightPanel.add(classesPanel, gbc);

        return rightPanel;
    }

    /**
     * Helper method to create consistently styled labels
     * @param text The label text
     * @param font The font to use
     * @return Configured JLabel with consistent styling
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Handles role selection changes to show/hide appropriate fields
     * @param e The item state change event
     */
    private void onRoleChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            boolean isTeacher = "Teacher".equals(selectedRole);

            // Toggle teacher-specific fields
            for (Component component : subjectsPanel.getComponents()) {
                component.setEnabled(isTeacher);
            }
            for (Component component : classesPanel.getComponents()) {
                component.setEnabled(isTeacher);
            }

            // Toggle student-specific field
            studentClassComboBox.setEnabled(!isTeacher);
        }
    }

    /**
     * Validates inputs and creates a new user based on form data
     */
    private void createUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String birthDateStr = birthDateField.getText().trim();

        // Input validation
        if (name.isEmpty()) {
            showError("Please enter the user's full name.");
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        LocalDate birthDate = parseBirthDate(birthDateStr);
        if (birthDate == null) {
            showError("Please enter a valid birth date in the format YYYY-MM-DD.");
            return;
        }

        // Build user based on role
        User.Builder userBuilder = new User.Builder(name, email, role.toLowerCase())
                .birthDate(birthDate);

        if ("teacher".equals(role.toLowerCase())) {
            List<String> selectedSubjects = getSelectedSubjects();
            List<String> selectedClasses = getSelectedClasses();

            if (selectedSubjects.isEmpty() || selectedClasses.isEmpty()) {
                showError("Please select both a subject and a class for teachers.");
                return;
            }

            userBuilder.subjects(selectedSubjects)
                    .taughtClasses(selectedClasses);
        } else if ("student".equals(role.toLowerCase())) {
            String studentClass = (String) studentClassComboBox.getSelectedItem();
            if (studentClass == null || studentClass.isEmpty()) {
                showError("Please select a class for the student.");
                return;
            }
            userBuilder.classId(studentClass);
        }

        // Create and save the user
        User newUser = userBuilder.build();
        UserManager.addUser(newUser);
        AuthManager.reloadUsers();
        showMessage("User created successfully!");
        windowManager.switchToPage("ManageUsers");
    }

    /**
     * Collects selected subjects from checkboxes
     * @return List of selected subject names
     */
    private List<String> getSelectedSubjects() {
        List<String> selectedSubjects = new ArrayList<>();
        for (Component component : subjectsPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedSubjects.add(checkBox.getText());
                }
            }
        }
        return selectedSubjects;
    }

    /**
     * Collects selected classes from checkboxes
     * @return List of selected class names
     */
    private List<String> getSelectedClasses() {
        List<String> selectedClasses = new ArrayList<>();
        for (Component component : classesPanel.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) component;
                if (checkBox.isSelected()) {
                    selectedClasses.add(checkBox.getText());
                }
            }
        }
        return selectedClasses;
    }

    /**
     * Parses birth date string into LocalDate object
     * @param birthDateStr Date string in YYYY-MM-DD format
     * @return Parsed LocalDate or null if invalid
     */
    private LocalDate parseBirthDate(String birthDateStr) {
        try {
            return LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Displays an error message dialog
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success message dialog
     * @param message The success message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Listener for theme changes to refresh UI when theme updates
     */
    private class ThemeChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("lookAndFeel".equals(evt.getPropertyName())
                    || evt.getPropertyName().startsWith("laf.styleChanged")) {
                SwingUtilities.invokeLater(() -> initPanel());
            }
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
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
}