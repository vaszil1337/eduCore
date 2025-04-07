package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.homeworks.Homework;
import com.vaszilvalentin.educore.homeworks.HomeworkManager;
import com.vaszilvalentin.educore.window.WindowManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.border.Border;

public class HomeworkPanel extends JPanel {

    private JTable homeworkTable;
    private DefaultTableModel tableModel;
    private JButton submitButton;
    private JButton viewDetailsButton;
    private JButton refreshButton;
    private final String currentStudentId;
    private final String currentClassId;
    private final WindowManager windowManager;

    public HomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        // Get current user information
        this.currentStudentId = CurrentUser.getCurrentUser().getId();
        this.currentClassId = CurrentUser.getCurrentUser().getClassId();

        initializeUI();
        loadHomeworkData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create the table model
        String[] columnNames = {"Subject", "Description", "Deadline", "Status", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create the table
        homeworkTable = new JTable(tableModel);
        homeworkTable.setRowHeight(30);
        homeworkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        homeworkTable.setShowGrid(false);
        homeworkTable.setIntercellSpacing(new Dimension(0, 0));

        // Use system colors
        homeworkTable.setBackground(UIManager.getColor("Table.background"));
        homeworkTable.setForeground(UIManager.getColor("Table.foreground"));

        // Cell renderer with system colors
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                // Use system colors
                if (!isSelected) {
                    setBackground(UIManager.getColor("Table.background"));
                    setForeground(UIManager.getColor("Table.foreground"));
                } else {
                    setBackground(UIManager.getColor("Table.selectionBackground"));
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                }

                return this;
            }
        };
        homeworkTable.setDefaultRenderer(Object.class, centerRenderer);

        // Header with system colors + custom border
        JTableHeader header = homeworkTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                // System colors for header
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));

                // Your custom border
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Container.borderColor")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));

                return this;
            }
        });

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(homeworkTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Table.background"));

        add(scrollPane, BorderLayout.CENTER);

        // Create button panel with BorderLayout
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15)); // Added left/right padding

        // Create left panel for back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // Create back button with consistent sizing
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        backButton.setPreferredSize(new Dimension(120, 30)); // Match other buttons' height
        backButton.setMargin(new Insets(5, 10, 5, 10)); // Consistent padding
        leftPanel.add(backButton);

        // Create right panel for other buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Existing buttons with consistent sizing
        refreshButton = new JButton("Refresh");
        viewDetailsButton = new JButton("View Details");
        submitButton = new JButton("Submit Homework");

        // Set consistent button sizes
        Dimension buttonSize = new Dimension(120, 30);
        refreshButton.setPreferredSize(buttonSize);
        viewDetailsButton.setPreferredSize(buttonSize);
        submitButton.setPreferredSize(new Dimension(160, 30));

        // Add action listeners
        refreshButton.addActionListener(e -> loadHomeworkData());
        viewDetailsButton.addActionListener(this::viewHomeworkDetails);
        submitButton.addActionListener(this::submitHomework);

        // Add buttons to panels
        rightPanel.add(refreshButton);
        rightPanel.add(viewDetailsButton);
        rightPanel.add(submitButton);

        // Add panels to main button panel
        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void loadHomeworkData() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get all homework for the current student's class
        List<Homework> homeworkList = HomeworkManager.getHomeworkByClass(currentClassId);

        // Add homework to the table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Homework homework : homeworkList) {
            String subject = homework.getSubject();
            String description = homework.getDescription();
            String deadline = homework.getDeadline().format(formatter);

            // Check if student has submitted
            String status;
            String grade;
            Homework.Submission submission = homework.getSubmission(currentStudentId);
            if (submission != null) {
                status = "Submitted";
                grade = submission.getGrade();
            } else {
                status = "Pending";
                grade = "-";
            }

            tableModel.addRow(new Object[]{subject, description, deadline, status, grade});
        }
    }

    private void submitHomework(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to submit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String homeworkId = getHomeworkIdFromRow(selectedRow);

        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            HomeworkManager.addSubmission(homeworkId, currentStudentId, filePath);
            loadHomeworkData();
            JOptionPane.showMessageDialog(this, "Homework submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewHomeworkDetails(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to view details",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String homeworkId = getHomeworkIdFromRow(selectedRow);
        Homework homework = getHomeworkById(homeworkId);

        if (homework != null) {
            StringBuilder details = new StringBuilder();
            details.append("Subject: ").append(homework.getSubject()).append("\n");
            details.append("Description: ").append(homework.getDescription()).append("\n");
            details.append("Deadline: ").append(homework.getDeadline()).append("\n\n");

            Homework.Submission submission = homework.getSubmission(currentStudentId);
            if (submission != null) {
                details.append("Your Submission:\n");
                details.append("File: ").append(submission.getFilePath()).append("\n");
                details.append("Grade: ").append(submission.getGrade());
            } else {
                details.append("You haven't submitted this homework yet.");
            }

            JOptionPane.showMessageDialog(this, details.toString(),
                    "Homework Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String getHomeworkIdFromRow(int row) {
        String description = (String) tableModel.getValueAt(row, 1);
        List<Homework> homeworkList = HomeworkManager.getHomeworkByClass(currentClassId);
        for (Homework hw : homeworkList) {
            if (hw.getDescription().equals(description)) {
                return hw.getId();
            }
        }
        return null;
    }

    private Homework getHomeworkById(String id) {
        List<Homework> homeworkList = HomeworkManager.getHomeworkByClass(currentClassId);
        for (Homework hw : homeworkList) {
            if (hw.getId().equals(id)) {
                return hw;
            }
        }
        return null;
    }
}
