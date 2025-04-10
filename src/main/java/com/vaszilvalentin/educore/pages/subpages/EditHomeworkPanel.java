package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.homework.CurrentHomework;
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
 * A panel that allows teachers to edit an existing homework entry. Includes
 * form fields for subject, class, description, and deadline.
 */
public class EditHomeworkPanel extends JPanel {

    private final WindowManager windowManager;
    private Homework homework;
    private static EditHomeworkPanel currentInstance;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    // UI components for form fields
    private JTextField subjectField;
    private JTextField classField;
    private JTextArea descriptionArea;
    private JTextField deadlineField;
    private JPanel formPanel;

    /**
     * Constructor for EditHomeworkPanel. Initializes panel and stores the
     * reference for refresh functionality.
     */
    public EditHomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the main panel layout and loads the homework data. Redirects
     * if the homework is not found.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // Get the homework ID from the current context
        String homeworkId = CurrentHomework.getEditHomeworkId();
        this.homework = findHomeworkById(homeworkId);

        if (this.homework == null) {
            JOptionPane.showMessageDialog(this, "Homework not found.");
            windowManager.switchToPage("TeacherHomework");
            return;
        }

        // Create and add the form panel to the layout
        formPanel = createFormPanel();

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
     * Constructs the form panel with input fields and buttons.
     *
     * @return the constructed form panel
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
        JLabel titleLabel = new JLabel("Edit Homework");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));
        titlePanel.add(titleLabel);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titlePanel, gbc);
        gbc.gridwidth = 1;

        // Class field
        gbc.gridy++;
        formPanel.add(createLabel("Class:", labelFont), gbc);
        classField = new JTextField(homework.getClassId(), 25);
        classField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(classField, gbc);

        // Subject field
        gbc.gridy++;
        formPanel.add(createLabel("Subject:", labelFont), gbc);
        subjectField = new JTextField(homework.getSubject(), 25);
        subjectField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(subjectField, gbc);

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
        scrollPane.setPreferredSize(new Dimension(600, 120));
        scrollPane.setMinimumSize(new Dimension(600, 120));
        scrollPane.setMaximumSize(new Dimension(600, 300));

        gbc.gridy++;
        formPanel.add(scrollPane, gbc);

        // Deadline input
        gbc.gridy++;
        formPanel.add(createLabel("Deadline (yyyy-MM-dd HH:mm):", labelFont), gbc);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        deadlineField = new JTextField(homework.getDeadline().format(formatter), 25);
        deadlineField.setFont(getFont().deriveFont(14f));
        gbc.gridy++;
        formPanel.add(deadlineField, gbc);

        // Button panel with Save and Cancel
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

        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setFont(getFont().deriveFont(14f));
        saveButton.addActionListener(e -> saveHomework());
        buttonPanel.add(saveButton);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    /**
     * Utility method to create a styled label.
     *
     * @param text the label text
     * @param font the font to apply
     * @return the created JLabel
     */
    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Searches for a homework object by its ID.
     *
     * @param homeworkId the homework identifier
     * @return the Homework instance if found, else null
     */
    private Homework findHomeworkById(String homeworkId) {
        for (Homework hw : HomeworkManager.getAllHomework()) {
            if (hw.getId().equals(homeworkId)) {
                return hw;
            }
        }
        return null;
    }

    /**
     * Validates and saves the updated homework data. Displays error if the date
     * format is invalid.
     */
    private void saveHomework() {
        String classId = classField.getText().trim();
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();
        String deadlineStr = deadlineField.getText().trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, formatter);

            Homework updated = new Homework(
                    homework.getId(),
                    description,
                    deadline,
                    subject,
                    classId
            );

            HomeworkManager.updateHomework(homework.getId(), updated);
            JOptionPane.showMessageDialog(this, "Homework updated.");
            windowManager.switchToPage("TeacherHomework");

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd HH:mm.");
        }
    }

    /**
     * Static method to refresh the currently active instance. Used after
     * external data modifications.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.refreshData();
        }
    }

    /**
     * Refreshes the data in the form fields with the latest homework
     * information.
     */
    private void refreshData() {
        String homeworkId = CurrentHomework.getEditHomeworkId();
        homework = findHomeworkById(homeworkId);

        if (homework != null) {
            subjectField.setText(homework.getSubject());
            classField.setText(homework.getClassId());
            descriptionArea.setText(homework.getDescription());
            deadlineField.setText(homework.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

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
                SwingUtilities.invokeLater(() -> updateComponentStyles());
            }
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener); // Prevent memory leak
    }
    
    /**
     * Updates the background and styling of the panel and its components to
     * match the current Look and Feel theme.
     */
    private void updateComponentStyles() {
        setBackground(UIManager.getColor("Panel.background"));
        formPanel.setBackground(UIManager.getColor("Table.background"));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        if (subjectField != null) {
            subjectField.setBackground(UIManager.getColor("TextField.background"));
            subjectField.setForeground(UIManager.getColor("TextField.foreground"));
        }
        if (classField != null) {
            classField.setBackground(UIManager.getColor("TextField.background"));
            classField.setForeground(UIManager.getColor("TextField.foreground"));
        }
        if (descriptionArea != null) {
            descriptionArea.setBackground(UIManager.getColor("TextArea.background"));
            descriptionArea.setForeground(UIManager.getColor("TextArea.foreground"));
        }
        if (deadlineField != null) {
            deadlineField.setBackground(UIManager.getColor("TextField.background"));
            deadlineField.setForeground(UIManager.getColor("TextField.foreground"));
        }

        revalidate();
        repaint();
    }

}
