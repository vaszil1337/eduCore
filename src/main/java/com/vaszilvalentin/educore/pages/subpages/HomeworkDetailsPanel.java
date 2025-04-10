package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.homework.CurrentHomework;
import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This panel displays detailed homework information including student
 * submissions and allows grade assignment. It is typically used by a teacher to
 * review homework submissions and provide grades.
 */
public class HomeworkDetailsPanel extends JPanel {

    private final WindowManager windowManager; // Manages UI navigation
    private Homework homework; // The homework currently being viewed
    private JTable submissionsTable; // Table listing student submissions
    public static HomeworkDetailsPanel currentInstance; // Static reference for external refresh capability
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    /**
     * Constructor initializes the panel and sets up its layout.
     */
    public HomeworkDetailsPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        currentInstance = this;
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes and lays out the entire panel with header, table, and button
     * bar.
     */
    private void initPanel() {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("Panel.background"));

        // Fetch homework ID and the homework object to display
        String homeworkId = CurrentHomework.getViewHomeworkId();
        this.homework = HomeworkManager.getHomeworkById(homeworkId);

        // Handle missing homework gracefully
        if (this.homework == null) {
            JOptionPane.showMessageDialog(this, "Homework not found.");
            windowManager.switchToPage("TeacherHomework");
            return;
        }

        // Add UI components
        add(createDetailsPanel(), BorderLayout.NORTH);
        add(new JScrollPane(createSubmissionsTable()), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Creates and returns the panel displaying homework metadata.
     */
    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 10, 5));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        detailsPanel.setBackground(UIManager.getColor("Panel.background"));

        // Add basic homework information
        detailsPanel.add(new JLabel("Homework Details"));
        detailsPanel.add(new JLabel("Description: " + homework.getDescription()));
        detailsPanel.add(new JLabel("Deadline: "
                + homework.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        detailsPanel.add(new JLabel("Subject: " + homework.getSubject()));
        detailsPanel.add(new JLabel("Class: " + homework.getClassId()));

        return detailsPanel;
    }

    /**
     * Creates a JTable to display student submissions and their grades.
     */
    private JTable createSubmissionsTable() {
        String[] columnNames = {"Student Name", "Email", "Submission Date", "Grade", "File"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing directly in the table
            }
        };

        // Filter students by the homework's class
        List<User> students = UserManager.getUsersByRole("student").stream()
                .filter(s -> s.getClassId().equals(homework.getClassId()))
                .collect(Collectors.toList());

        // Populate the table rows with student submission data
        for (User student : students) {
            Homework.Submission submission = homework.getSubmissions().get(student.getId());
            String fileName = (submission != null && submission.getFilePath() != null)
                    ? new File(submission.getFilePath()).getName()
                    : "No file";

            model.addRow(new Object[]{
                student.getName(),
                student.getEmail(),
                formatDate(submission != null ? submission.getSubmissionDate() : null),
                submission != null ? submission.getGrade() : "-",
                fileName
            });
        }

        // Configure table appearance
        submissionsTable = new JTable(model);
        submissionsTable.setRowHeight(30);
        submissionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center-align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < submissionsTable.getColumnCount(); i++) {
            submissionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return submissionsTable;
    }

    /**
     * Creates the bottom panel containing action buttons.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(0, 0, 10, 10));
        buttonPanel.setBackground(UIManager.getColor("Panel.background"));

        // Add "Open File" button
        JButton openFileBtn = new JButton("Open Selected File");
        openFileBtn.addActionListener(this::handleOpenFile);

        // Add "Set Grade" button
        JButton setGradeBtn = new JButton("Set Grade");
        setGradeBtn.addActionListener(this::handleSetGrade);

        // Add "Back" button
        JButton backBtn = new JButton("Back to List");
        backBtn.addActionListener(e -> windowManager.switchToPage("TeacherHomework"));

        // Add buttons to the panel
        buttonPanel.add(openFileBtn);
        buttonPanel.add(setGradeBtn);
        buttonPanel.add(backBtn);

        return buttonPanel;
    }

    /**
     * Opens the selected student's submitted file if available.
     */
    private void handleOpenFile(ActionEvent e) {
        int selectedRow = submissionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first");
            return;
        }

        String studentName = (String) submissionsTable.getValueAt(selectedRow, 0);
        String studentEmail = (String) submissionsTable.getValueAt(selectedRow, 1);
        String studentId = findStudentIdByEmail(studentEmail);

        if (studentId != null) {
            Homework.Submission submission = homework.getSubmission(studentId);
            if (submission != null && submission.getFilePath() != null) {
                if ("AUTOGENERATED_GRADE".equals(submission.getFilePath())) {
                    showError("This submission was auto-generated and cannot be opened.");
                    return;
                }

                openSubmissionFile(submission.getFilePath());
            } else {
                showError("No file submitted for " + studentName);
            }
        }
    }

    /**
     * Allows the teacher to set or update the grade for a selected student.
     */
    private void handleSetGrade(ActionEvent e) {
        int selectedRow = submissionsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select a student first");
            return;
        }

        String studentName = (String) submissionsTable.getValueAt(selectedRow, 0);
        String studentEmail = (String) submissionsTable.getValueAt(selectedRow, 1);
        String studentId = findStudentIdByEmail(studentEmail);
        Homework.Submission submission = homework.getSubmission(studentId);

        if (submission == null || submission.getFilePath() == null) {
            showWarning("You cannot set a grade for a student who has not submitted the homework.");
            return;
        }

        if ("AUTOGENERATED_GRADE".equals(submission.getFilePath())) {
            showWarning("Cannot set grade for auto-generated submissions.");
            return;
        }

        String currentGrade = (String) submissionsTable.getValueAt(selectedRow, 3);

        String newGrade = JOptionPane.showInputDialog(
                this,
                "Enter grade for " + studentName + ":",
                currentGrade);

        if (newGrade != null && !newGrade.trim().isEmpty()) {
            if (validateGrade(newGrade)) {
                HomeworkManager.setSubmissionGrade(homework.getId(), studentId, newGrade);
                submissionsTable.setValueAt(newGrade, selectedRow, 3);
            }
        }
    }

    /**
     * Validates that the grade is a number or the dash character.
     */
    private boolean validateGrade(String grade) {
        try {
            if (!grade.equals("-")) {
                Integer.parseInt(grade);
            }
            return true;
        } catch (NumberFormatException e) {
            showError("Please enter a valid number or '-' for no grade");
            return false;
        }
    }

    /**
     * Opens a file in the system's default application.
     */
    private void openSubmissionFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                showError("File not found: " + filePath);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                showError("File operations not supported on this platform");
            }
        } catch (IOException ex) {
            showError("Error opening file: " + ex.getMessage());
        }
    }

    /**
     * Finds the student ID from the email.
     */
    private String findStudentIdByEmail(String email) {
        return UserManager.getUsersByRole("student").stream()
                .filter(s -> s.getEmail().equals(email))
                .findFirst()
                .map(User::getId)
                .orElse(null);
    }

    /**
     * Formats a LocalDateTime for display.
     */
    private String formatDate(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Not submitted";
    }

    /**
     * Utility to show an error dialog.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Utility to show a warning dialog.
     */
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Static method to refresh the panel if the current instance exists.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.refreshData();
        }
    }

    /**
     * Re-initializes the panel to reflect the latest data.
     */
    private void refreshData() {
        initPanel();
        revalidate();
        repaint();
    }

    /**
     * Updates the background and styling of the panel and table to match the
     * current theme.
     */
    private void updateComponentStyles() {
        setBackground(UIManager.getColor("Panel.background"));

        if (submissionsTable != null) {
            submissionsTable.setBackground(UIManager.getColor("Table.background"));
            submissionsTable.setForeground(UIManager.getColor("Table.foreground"));
            submissionsTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            submissionsTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            submissionsTable.repaint();
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
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

}
