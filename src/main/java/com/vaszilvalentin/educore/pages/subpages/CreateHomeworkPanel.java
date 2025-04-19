package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.window.WindowManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.auth.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Swing panel that enables teachers to create new homework assignments.
 * Features dropdown selection for classes and subjects, along with fields for
 * assignment description and deadline. Integrates with HomeworkManager for
 * persistence and includes comprehensive input validation.
 */
public class CreateHomeworkPanel extends JPanel {

    // Window manager for navigation between panels
    private final WindowManager windowManager;
    
    // Singleton reference for refresh functionality
    private static CreateHomeworkPanel currentInstance;
    
    // Listens for theme changes to update UI styling
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // UI Components
    private JComboBox<String> classComboBox;    // Dropdown for class selection
    private JComboBox<String> subjectComboBox;  // Dropdown for subject selection
    private JTextArea descriptionArea;          // Multiline text area for assignment details
    private JTextField deadlineField;           // Input field for due date/time

    // Data stores for combobox mappings
    private final Map<String, String> classIdMap = new HashMap<>();  // Maps display names to class IDs
    private final Map<String, String> subjectMap = new HashMap<>();  // Maps display names to subject codes
    
    // Reference to currently logged-in teacher
    private final User currentTeacher;

    /**
     * Constructs a new CreateHomeworkPanel with the specified window manager.
     * Initializes UI components and loads teacher-specific data for dropdowns.
     * 
     * @param windowManager The window manager responsible for navigation
     */
    public CreateHomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.currentTeacher = CurrentUser.getCurrentUser();
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the panel layout and components.
     * Clears existing components and rebuilds the UI from scratch.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));
        add(createFormPanel());
        revalidate();
        repaint();
    }

    /**
     * Creates and configures the main form panel containing all input fields.
     * 
     * @return A fully configured JPanel with the homework creation form
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setBackground(UIManager.getColor("Table.background"));
        formPanel.setPreferredSize(new Dimension(600, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Configure title label with consistent styling
        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Create New Homework");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));
        
        // Add title to form
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Add class selection components
        gbc.gridy++;
        formPanel.add(createLabel("Class:", labelFont), gbc);
        classComboBox = new JComboBox<>();
        classComboBox.setFont(getFont().deriveFont(14f));
        loadTeacherClasses();
        gbc.gridy++;
        formPanel.add(classComboBox, gbc);

        // Add subject selection components
        gbc.gridy++;
        formPanel.add(createLabel("Subject:", labelFont), gbc);
        subjectComboBox = new JComboBox<>();
        subjectComboBox.setFont(getFont().deriveFont(14f));
        loadTeacherSubjects();
        gbc.gridy++;
        formPanel.add(subjectComboBox, gbc);

        // Configure description field with scroll pane
        gbc.gridy++;
        formPanel.add(createLabel("Description:", labelFont), gbc);
        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setFont(getFont().deriveFont(14f));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.gridy++;
        formPanel.add(scrollPane, gbc);

        // Configure deadline field with default value
        gbc.gridy++;
        formPanel.add(createLabel("Deadline (yyyy-MM-dd HH:mm):", labelFont), gbc);
        deadlineField = new JTextField(25);
        deadlineField.setFont(getFont().deriveFont(14f));
        deadlineField.setText(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridy++;
        formPanel.add(deadlineField, gbc);

        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> windowManager.switchToPage("TeacherHomework"));

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createHomework());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        gbc.gridy++;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    /**
     * Loads the current teacher's classes into the class combobox.
     * Maps display names to actual class IDs for later reference.
     */
    private void loadTeacherClasses() {
        classIdMap.clear();
        classComboBox.removeAllItems();

        if (currentTeacher != null && currentTeacher.getTaughtClasses() != null) {
            for (String classId : currentTeacher.getTaughtClasses()) {
                String displayName = "Class " + classId;
                classIdMap.put(displayName, classId);
                classComboBox.addItem(displayName);
            }

            // Select first class by default if available
            if (classComboBox.getItemCount() > 0) {
                classComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * Loads the current teacher's subjects into the subject combobox.
     * Maintains mapping between display names and subject codes.
     */
    private void loadTeacherSubjects() {
        subjectMap.clear();
        subjectComboBox.removeAllItems();

        if (currentTeacher != null && currentTeacher.getSubjects() != null) {
            for (String subject : currentTeacher.getSubjects()) {
                String displayName = subject;
                subjectMap.put(displayName, subject);
                subjectComboBox.addItem(displayName);
            }

            // Select first subject by default if available
            if (subjectComboBox.getItemCount() > 0) {
                subjectComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * Creates a consistently styled label for form fields.
     * 
     * @param text The text to display on the label
     * @param font The font to use for the label
     * @return A configured JLabel instance
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Validates input and creates a new homework assignment.
     * Performs comprehensive validation before persisting the assignment.
     */
    private void createHomework() {
        // Get selected values from comboboxes
        String classDisplay = (String) classComboBox.getSelectedItem();
        String subjectDisplay = (String) subjectComboBox.getSelectedItem();

        // Get mapped values
        String classId = classIdMap.get(classDisplay);
        String subject = subjectMap.get(subjectDisplay);
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        // Validate required fields
        if (description.isEmpty()) {
            showError("Please enter a description.");
            return;
        }
        if (deadlineStr.isEmpty()) {
            showError("Please enter a deadline.");
            return;
        }

        try {
            // Parse and validate deadline
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if (deadline.isBefore(LocalDateTime.now())) {
                showError("Deadline must be in the future.");
                return;
            }

            // Create and persist new homework
            Homework newHomework = new Homework(
                    null,
                    description,
                    deadline,
                    subject,
                    classId
            );

            HomeworkManager.addHomework(newHomework);
            showMessage("Homework created successfully!");
            windowManager.switchToPage("TeacherHomework");

        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2023-12-31 16:00).");
        }
    }

    /**
     * Displays an error message dialog with standardized formatting.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success message dialog with standardized formatting.
     * 
     * @param message The success message to display
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
     * Listens for theme changes and updates the panel's appearance accordingly.
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

    /**
     * Cleans up resources when the panel is removed.
     * Unregisters theme change listener to prevent memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}