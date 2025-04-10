package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A Swing panel for creating and submitting homework assignments. Handles user
 * input validation and communicates with HomeworkManager for persistence.
 */
public class CreateHomeworkPanel extends JPanel {

    // Window manager for navigation between panels
    private final WindowManager windowManager;
    // Singleton reference to the current instance
    private static CreateHomeworkPanel currentInstance;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // Form input components
    private JTextField subjectField;
    private JTextField classField;
    private JTextArea descriptionArea;
    private JTextField deadlineField;

    /**
     * Constructs the homework creation panel.
     *
     * @param windowManager The window manager for navigation control
     */
    public CreateHomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);

    }

    /**
     * Initializes the panel UI components and layout.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // Main form panel with centered positioning
        JPanel formPanel = createFormPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(50, 50, 50, 50);
        gbc.anchor = GridBagConstraints.CENTER;
        add(formPanel, gbc);

        revalidate();
        repaint();
    }

    /**
     * Creates the main form panel containing all input fields.
     *
     * @return Configured JPanel with form components
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));
        formPanel.setBackground(UIManager.getColor("Table.background"));
        formPanel.setPreferredSize(new Dimension(600, 700));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Create New Homework");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));
        titlePanel.add(titleLabel);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titlePanel, gbc);
        gbc.gridwidth = 1;

        // Class input field
        gbc.gridy++;
        formPanel.add(createLabel("Class:", labelFont), gbc);
        classField = new JTextField(25);
        classField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(classField, gbc);

        // Subject input field
        gbc.gridy++;
        formPanel.add(createLabel("Subject:", labelFont), gbc);
        subjectField = new JTextField(25);
        subjectField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(subjectField, gbc);

        // Description field with scrollable text area
        gbc.gridy++;
        formPanel.add(createLabel("Description:", labelFont), gbc);
        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setFont(getFont().deriveFont(14f));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setMinimumSize(new Dimension(600, 120));
        scrollPane.setMaximumSize(new Dimension(600, 300));

        gbc.gridy++;
        formPanel.add(scrollPane, gbc);

        // Deadline field with default tomorrow 4PM value
        gbc.gridy++;
        formPanel.add(createLabel("Deadline (yyyy-MM-dd HH:mm):", labelFont), gbc);
        deadlineField = new JTextField(25);
        deadlineField.setFont(getFont().deriveFont(14f));
        LocalDateTime defaultDeadline = LocalDateTime.now().plusDays(1).withHour(16).withMinute(0);
        deadlineField.setText(defaultDeadline.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridy++;
        formPanel.add(deadlineField, gbc);

        // Action buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        buttonPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setFont(getFont().deriveFont(14f));
        cancelButton.addActionListener(e -> windowManager.switchToPage("TeacherHomework"));
        buttonPanel.add(cancelButton);

        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));

        JButton createButton = new JButton("Create");
        createButton.setPreferredSize(new Dimension(120, 35));
        createButton.setFont(getFont().deriveFont(14f));
        createButton.addActionListener(e -> createHomework());
        buttonPanel.add(createButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    /**
     * Creates a styled label for form fields.
     *
     * @param text The label text
     * @param font The font to use
     * @return Configured JLabel instance
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Validates inputs and creates a new homework assignment. Shows error
     * dialogs for invalid data before submission.
     */
    private void createHomework() {
        String classId = classField.getText().trim();
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        // Input validation checks
        if (classId.isEmpty()) {
            showError("Please enter a class.");
            return;
        }
        if (subject.isEmpty()) {
            showError("Please enter a subject.");
            return;
        }
        if (description.isEmpty()) {
            showError("Please enter a description.");
            return;
        }
        if (deadlineStr.isEmpty()) {
            showError("Please enter a deadline.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, formatter);

            // Deadline must be in the future
            if (deadline.isBefore(LocalDateTime.now())) {
                showError("Deadline must be in the future.");
                return;
            }

            // Create and persist the homework
            Homework newHomework = new Homework(
                    null, // ID generated by HomeworkManager
                    description,
                    deadline,
                    subject,
                    classId
            );

            HomeworkManager.addHomework(newHomework);
            JOptionPane.showMessageDialog(this, "Homework created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            windowManager.switchToPage("TeacherHomework");

        } catch (DateTimeParseException ex) {
            showError("Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2023-12-31 16:00).");
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
     * Refreshes the current panel instance if it exists.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.refreshData();
        }
    }

    /**
     * Refreshes panel data and UI components.
     */
    private void refreshData() {
        initPanel();
        revalidate();
        repaint();
    }

    /**
     * Listens for Look and Feel changes and updates component styles
     * accordingly.
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
        UIManager.removePropertyChangeListener(themeChangeListener); // Prevent memory leak
    }
}
