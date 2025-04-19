package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.Subject;
import com.vaszilvalentin.educore.users.ClassLevel;
import com.vaszilvalentin.educore.users.CurrentUserSelection;
import com.vaszilvalentin.educore.utils.EncryptionUtils;
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
import java.util.Arrays;

/**
 * Swing panel for editing existing users (students or teachers). Allows
 * modification of user details with pre-filled fields from existing user data.
 * Handles both student and teacher roles with appropriate fields for each.
 */
public class EditUserPanel extends JPanel {

    // Singleton instance tracking for refresh purposes
    private static EditUserPanel currentInstance;

    // Core dependencies
    private final WindowManager windowManager;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // User data being edited
    private User userToEdit;
    private String editableId;

    // Form UI components
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JTextField birthDateField;
    private JPanel subjectsPanel;
    private JPanel classesPanel;
    private JComboBox<String> studentClassComboBox;
    private JButton updateButton;
    private JCheckBox updatePasswordCheckbox;
    private JPasswordField newPasswordField;

    /**
     * Constructs the edit user panel.
     *
     * @param windowManager The application's window manager for navigation
     */
    public EditUserPanel(WindowManager windowManager) {
        currentInstance = this;
        this.windowManager = windowManager;
        this.editableId = CurrentUserSelection.getEditUserId();

        // Load user data to edit
        this.userToEdit = UserManager.getAllUsers().stream()
                .filter(user -> user.getId().equals(editableId))
                .findFirst()
                .orElse(null);

        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the panel UI components and layout. Creates all form fields
     * and action buttons.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());

        // Main form container with consistent styling
        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setPreferredSize(new Dimension(700, 700));
        formBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 20, 30, 20)
        ));
        formBox.setBackground(UIManager.getColor("Table.background"));

        // Content panels (left for basic info, right for role-specific info)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(createLeftPanel());
        contentPanel.add(createRightPanel());
        formBox.add(contentPanel);
        formBox.add(Box.createVerticalStrut(10));

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cancel button returns to user management
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            CurrentUserSelection.clear();
            windowManager.switchToPage("ManageUsers");
        });

        // Update button saves changes
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateUser());

        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        formBox.add(buttonPanel);

        // Center the form in the main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(formBox, gbc);

        // Pre-fill form with existing user data
        populateFormWithUserData();

        revalidate();
        repaint();
    }

    /**
     * Populates form fields with data from the user being edited. Handles
     * student, teacher, and admin-specific fields appropriately.
     */
    private void populateFormWithUserData() {
        nameField.setText(userToEdit.getName());
        emailField.setText(userToEdit.getEmail());
        roleComboBox.setSelectedItem(capitalizeFirstLetter(userToEdit.getRole()));
        birthDateField.setText(userToEdit.getBirthDate().toString());

        String role = userToEdit.getRole().toLowerCase();

        if ("student".equals(role)) {
            studentClassComboBox.setSelectedItem(userToEdit.getClassId());
            studentClassComboBox.setEnabled(true);

            // Disable teacher-specific fields
            for (Component comp : subjectsPanel.getComponents()) {
                comp.setEnabled(false);
            }
            for (Component comp : classesPanel.getComponents()) {
                comp.setEnabled(false);
            }

        } else if ("teacher".equals(role)) {
            // Enable teacher-specific fields
            for (Component comp : subjectsPanel.getComponents()) {
                if (comp instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) comp;
                    cb.setEnabled(true);
                    cb.setSelected(userToEdit.getSubjects().contains(cb.getText()));
                }
            }

            for (Component comp : classesPanel.getComponents()) {
                if (comp instanceof JCheckBox) {
                    JCheckBox cb = (JCheckBox) comp;
                    cb.setEnabled(true);
                    cb.setSelected(userToEdit.getTaughtClasses().contains(cb.getText()));
                }
            }

            // Disable student class selector
            studentClassComboBox.setEnabled(false);

        } else if ("admin".equals(role)) {
            // Admin has no editable class or subject info
            studentClassComboBox.setEnabled(false);

            for (Component comp : subjectsPanel.getComponents()) {
                comp.setEnabled(false);
                if (comp instanceof JCheckBox) {
                    ((JCheckBox) comp).setSelected(false);
                }
            }

            for (Component comp : classesPanel.getComponents()) {
                comp.setEnabled(false);
                if (comp instanceof JCheckBox) {
                    ((JCheckBox) comp).setSelected(false);
                }
            }
        }
    }

    /**
     * Creates the left panel containing basic user information fields.
     *
     * @return Configured JPanel with basic info fields
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        leftPanel.setBackground(UIManager.getColor("Table.background"));
        leftPanel.setPreferredSize(new Dimension(300, 650));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

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
        leftPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(25);
        nameField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(nameField, gbc);

        // Email field
        gbc.gridy++;
        leftPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(25);
        emailField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(emailField, gbc);

        // Role selection (disabled for editing)
        gbc.gridy++;
        leftPanel.add(new JLabel("Role:"), gbc);
        roleComboBox = new JComboBox<>(new String[]{"Student", "Teacher", "Admin"});
        roleComboBox.setFont(getFont().deriveFont(14f));
        roleComboBox.setForeground(UIManager.getColor("TextField.foreground"));
        roleComboBox.setEnabled(false); // Role cannot be changed after creation
        roleComboBox.addItemListener(e -> onRoleChanged(e));
        gbc.gridy++;
        leftPanel.add(roleComboBox, gbc);

        // Birth date
        gbc.gridy++;
        leftPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"), gbc);
        birthDateField = new JTextField(25);
        birthDateField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        leftPanel.add(birthDateField, gbc);
        
        // Password update section
        gbc.gridy++;
        updatePasswordCheckbox = new JCheckBox("Reset Password");
        updatePasswordCheckbox.setForeground(UIManager.getColor("Label.foreground"));
        updatePasswordCheckbox.setFont(getFont().deriveFont(14f));
        leftPanel.add(updatePasswordCheckbox, gbc);

        gbc.gridy++;
        leftPanel.add(new JLabel("New Password:"), gbc);
        newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(getFont().deriveFont(14f));
        newPasswordField.setEnabled(false);
        gbc.gridy++;
        leftPanel.add(newPasswordField, gbc);

        // Enable/disable password field based on checkbox
        updatePasswordCheckbox.addItemListener(e -> {
            boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
            newPasswordField.setEnabled(enabled);
            if (!enabled) {
                newPasswordField.setText("");
            }
        });

        return leftPanel;
    }

    /**
     * Creates the right panel with role-specific fields.
     *
     * @return Configured JPanel with role-specific fields
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

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Role-Specific Information");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Student class selection
        gbc.gridy++;
        rightPanel.add(new JLabel("Class (for students):"), gbc);
        studentClassComboBox = new JComboBox<>();
        for (ClassLevel classLevel : ClassLevel.values()) {
            studentClassComboBox.addItem(classLevel.toString());
        }
        studentClassComboBox.setFont(getFont().deriveFont(14f));
        studentClassComboBox.setForeground(UIManager.getColor("TextField.foreground"));
        studentClassComboBox.setEnabled("Student".equals(roleComboBox.getSelectedItem()));
        gbc.gridy++;
        rightPanel.add(studentClassComboBox, gbc);

        // Teacher subjects
        gbc.gridy++;
        rightPanel.add(new JLabel("Subjects (for teachers):"), gbc);
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

        // Teacher classes
        gbc.gridy++;
        rightPanel.add(new JLabel("Taught Classes (for teachers):"), gbc);
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
     * Updates the user with edited information after validation. Performs field
     * validation before updating and provides user feedback.
     */
    private void updateUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String birthDateStr = birthDateField.getText().trim();

        // Basic field validation
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
        
        // Handle password reset if requested
        if (updatePasswordCheckbox.isSelected()) {
            char[] newPasswordChars = newPasswordField.getPassword();
            
            if (newPasswordChars.length == 0) {
                showError("Please enter a new password.");
                Arrays.fill(newPasswordChars, '0');
                return;
            }

            String newPassword = new String(newPasswordChars);
            // Encrypt and set the new password
            userToEdit.setPassword(EncryptionUtils.encrypt(newPassword));
            // Clear sensitive data from memory
            Arrays.fill(newPasswordChars, '0');
        }

        // Update core user properties
        userToEdit.setName(name);
        userToEdit.setEmail(email);
        userToEdit.setBirthDate(birthDate);

        // Handle role-specific updates
        if ("teacher".equalsIgnoreCase(userToEdit.getRole())) {
            List<String> selectedSubjects = getSelectedSubjects();
            List<String> selectedClasses = getSelectedClasses();

            if (selectedSubjects.isEmpty() || selectedClasses.isEmpty()) {
                showError("Please select both a subject and a class for teachers.");
                return;
            }

            userToEdit.setSubjects(selectedSubjects);
            userToEdit.setTaughtClasses(selectedClasses);
        } else if ("student".equalsIgnoreCase(userToEdit.getRole())) {
            String studentClass = (String) studentClassComboBox.getSelectedItem();
            if (studentClass == null || studentClass.isEmpty()) {
                showError("Please select a class for the student.");
                return;
            }
            userToEdit.setClassId(studentClass);
        }
        
        // Clear password fields
        updatePasswordCheckbox.setSelected(false);
        newPasswordField.setText("");

        // Persist changes
        UserManager.updateUser(editableId, userToEdit);
        showMessage("User updated successfully!");
        windowManager.switchToPage("ManageUsers");
    }

    /**
     * Handles role change events by enabling/disabling appropriate fields.
     *
     * @param e The ItemEvent triggering the change
     */
    private void onRoleChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String selectedRole = ((String) roleComboBox.getSelectedItem()).toLowerCase();

            boolean isTeacher = "teacher".equals(selectedRole);
            boolean isStudent = "student".equals(selectedRole);
            boolean isAdmin = "admin".equals(selectedRole);

            // Subjects panel (for teachers)
            for (Component component : subjectsPanel.getComponents()) {
                component.setEnabled(isTeacher);
                if (!isTeacher && component instanceof JCheckBox) {
                    ((JCheckBox) component).setSelected(false);
                }
            }

            // Classes panel (for teachers)
            for (Component component : classesPanel.getComponents()) {
                component.setEnabled(isTeacher);
                if (!isTeacher && component instanceof JCheckBox) {
                    ((JCheckBox) component).setSelected(false);
                }
            }

            // Student class combo box
            studentClassComboBox.setEnabled(isStudent);
            if (!isStudent) {
                studentClassComboBox.setSelectedItem(null);
            }
        }
    }

    /**
     * Gets the list of selected subjects from checkboxes.
     *
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
     * Gets the list of selected classes from checkboxes.
     *
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
     * Reloads the user to edit based on current selection. Used when refreshing
     * the panel.
     */
    public void reloadUserToEdit() {
        this.editableId = CurrentUserSelection.getEditUserId();
        this.userToEdit = UserManager.getAllUsers().stream()
                .filter(user -> user.getId().equals(editableId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Parses a birth date string into LocalDate.
     *
     * @param birthDateStr The date string in YYYY-MM-DD format
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
     * Shows an error message dialog.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an information message dialog.
     *
     * @param message The message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param input The string to capitalize
     * @return The capitalized string
     */
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Listener for theme changes that refreshes the UI when themes change.
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
     * Refreshes the current instance of the panel. Reloads user data and
     * reinitializes the UI.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.reloadUserToEdit();
            currentInstance.initPanel();
        }
    }
}
