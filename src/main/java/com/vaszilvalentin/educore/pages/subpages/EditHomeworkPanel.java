package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.homework.CurrentHomework;
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
 * A Swing panel that provides an interface for teachers to edit existing homework assignments.
 * The panel includes dropdown selectors for class and subject, along with fields for
 * description and deadline. Implements theme change support through PropertyChangeListener.
 */
public class EditHomeworkPanel extends JPanel {

    // Window management reference
    private final WindowManager windowManager;
    
    // The homework being edited
    private Homework homework;
    
    // Singleton instance tracking
    private static EditHomeworkPanel currentInstance;
    
    // Theme change listener
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // UI Components
    private JComboBox<String> classComboBox;
    private JComboBox<String> subjectComboBox;
    private JTextArea descriptionArea;
    private JTextField deadlineField;
    
    // Data stores for dropdown mappings
    private final Map<String, String> classIdMap = new HashMap<>();  // Maps display names to class IDs
    private final Map<String, String> subjectMap = new HashMap<>();  // Maps display names to subjects
    
    // Current teacher user
    private final User currentTeacher;

    /**
     * Constructs a new EditHomeworkPanel with the specified WindowManager.
     * 
     * @param windowManager The WindowManager instance for navigation
     */
    public EditHomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        currentInstance = this;
        currentTeacher = CurrentUser.getCurrentUser();
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the panel by clearing existing components and setting up the UI.
     * Loads the homework data to be edited and creates the form panel.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // Load the homework to edit
        String homeworkId = CurrentHomework.getEditHomeworkId();
        this.homework = findHomeworkById(homeworkId);

        if (this.homework == null) {
            JOptionPane.showMessageDialog(this, "Homework not found.");
            windowManager.switchToPage("TeacherHomework");
            return;
        }

        // Set up layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(50, 50, 50, 50);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Add the form panel
        add(createFormPanel(), gbc);

        revalidate();
        repaint();
    }

    /**
     * Creates the main form panel containing all input fields and controls.
     * 
     * @return The configured JPanel containing the form elements
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

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);

        // Title
        JLabel titleLabel = new JLabel("Edit Homework");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Class combobox
        gbc.gridy++;
        formPanel.add(createLabel("Class:", labelFont), gbc);
        classComboBox = new JComboBox<>();
        classComboBox.setFont(getFont().deriveFont(14f));
        loadTeacherClasses();
        selectCurrentClass();
        gbc.gridy++;
        formPanel.add(classComboBox, gbc);

        // Subject combobox
        gbc.gridy++;
        formPanel.add(createLabel("Subject:", labelFont), gbc);
        subjectComboBox = new JComboBox<>();
        subjectComboBox.setFont(getFont().deriveFont(14f));
        loadTeacherSubjects();
        selectCurrentSubject();
        gbc.gridy++;
        formPanel.add(subjectComboBox, gbc);

        // Description field with scroll pane
        gbc.gridy++;
        formPanel.add(createLabel("Description:", labelFont), gbc);
        descriptionArea = new JTextArea(homework.getDescription(), 6, 30);
        descriptionArea.setFont(getFont().deriveFont(14f));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.gridy++;
        formPanel.add(scrollPane, gbc);

        // Deadline field
        gbc.gridy++;
        formPanel.add(createLabel("Deadline (yyyy-MM-dd HH:mm):", labelFont), gbc);
        deadlineField = new JTextField(homework.getDeadline()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 25);
        deadlineField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(deadlineField, gbc);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> windowManager.switchToPage("TeacherHomework"));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveHomework());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridy++;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    /**
     * Loads the classes taught by the current teacher into the class combo box.
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
        }
    }

    /**
     * Selects the current class in the combo box based on the homework being edited.
     */
    private void selectCurrentClass() {
        String currentClassId = homework.getClassId();
        for (Map.Entry<String, String> entry : classIdMap.entrySet()) {
            if (entry.getValue().equals(currentClassId)) {
                classComboBox.setSelectedItem(entry.getKey());
                break;
            }
        }
    }

    /**
     * Loads the subjects taught by the current teacher into the subject combo box.
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
        }
    }

    /**
     * Selects the current subject in the combo box based on the homework being edited.
     */
    private void selectCurrentSubject() {
        String currentSubject = homework.getSubject();
        subjectComboBox.setSelectedItem(currentSubject);
    }

    /**
     * Creates a styled label with consistent formatting.
     * 
     * @param text The label text
     * @param font The font to use
     * @return The configured JLabel
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Finds a homework assignment by its ID.
     * 
     * @param homeworkId The ID of the homework to find
     * @return The Homework object, or null if not found
     */
    private Homework findHomeworkById(String homeworkId) {
        return HomeworkManager.getAllHomework().stream()
            .filter(hw -> hw.getId().equals(homeworkId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Validates and saves the edited homework data.
     */
    private void saveHomework() {
        String classDisplay = (String) classComboBox.getSelectedItem();
        String subjectDisplay = (String) subjectComboBox.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        // Validation
        if (classDisplay == null) {
            showError("Please select a class.");
            return;
        }
        if (subjectDisplay == null) {
            showError("Please select a subject.");
            return;
        }
        if (description.isEmpty()) {
            showError("Please enter a description.");
            return;
        }

        try {
            // Parse and validate deadline
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // Create updated homework object
            Homework updated = new Homework(
                homework.getId(),
                description,
                deadline,
                subjectMap.get(subjectDisplay),
                classIdMap.get(classDisplay)
            );

            // Save to manager
            HomeworkManager.updateHomework(homework.getId(), updated);
            showMessage("Homework updated successfully!");
            windowManager.switchToPage("TeacherHomework");

        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use yyyy-MM-dd HH:mm.");
        }
    }

    /**
     * Displays an error message dialog.
     * 
     * @param message The error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an information message dialog.
     * 
     * @param message The message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refreshes the current instance of the panel if it exists.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.initPanel();
        }
    }

    /**
     * Inner class to handle theme change events.
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
     * Cleans up resources when the panel is removed.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }
}