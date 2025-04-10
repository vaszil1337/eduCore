package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.window.WindowManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vaszilvalentin.educore.utils.NetworkTimeChecker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This panel is responsible for displaying, submitting, and managing homework
 * for the current student. It includes UI rendering, async data loading, and
 * user interactions.
 */
public class HomeworkPanel extends JPanel {

    private LocalDateTime currentTime;
    private JTable homeworkTable;
    private DefaultTableModel tableModel;
    private JButton submitButton;
    private JButton viewDetailsButton;
    private JButton refreshButton;
    private final String currentStudentId;
    private final String currentClassId;
    private final WindowManager windowManager;
    private final Map<Integer, String> rowToHomeworkIdMap;
    private JPanel loadingPanel;
    private JLabel loadingLabel;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    /**
     * Constructor initializes UI and starts asynchronous network time fetch.
     */
    public HomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.currentStudentId = CurrentUser.getCurrentUser().getId();
        this.currentClassId = CurrentUser.getCurrentUser().getClassId();
        this.rowToHomeworkIdMap = new HashMap<>();

        initializeUI();
        showLoadingAnimation();

        // Fetch accurate current time from a network source
        NetworkTimeChecker.getNetworkTimeAsync().thenAccept(time -> {
            currentTime = time;
            SwingUtilities.invokeLater(() -> {
                hideLoadingAnimation();
                loadHomeworkData();
            });
        });
        // Register listener to apply styling on theme change
        UIManager.addPropertyChangeListener(themeChangeListener);

    }

    /**
     * Displays a centered loading animation while data is being fetched.
     */
    private void showLoadingAnimation() {
        removeAll();

        loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        loadingLabel = new JLabel("Loading homework data...", JLabel.CENTER);
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(16f));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 6));
        progressBar.setMaximumSize(new Dimension(200, 6));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(loadingLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(progressBar);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        loadingPanel.add(contentPanel, gbc);

        add(loadingPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Removes loading animation and re-renders the main UI.
     */
    private void hideLoadingAnimation() {
        remove(loadingPanel);
        initializeUIComponents();
        revalidate();
        repaint();
    }

    /**
     * Initializes panel layout and sets padding.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        initializeUIComponents();
    }

    /**
     * Initializes table and buttons used in the homework panel.
     */
    private void initializeUIComponents() {
        String[] columnNames = {"Subject", "Description", "Deadline", "Status", "Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disallow cell editing
            }
        };

        // Configure homework table
        homeworkTable = new JTable(tableModel);
        homeworkTable.setRowHeight(30);
        homeworkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        homeworkTable.setShowGrid(false);
        homeworkTable.setIntercellSpacing(new Dimension(0, 0));

        // Renderer for centered, theme-colored cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
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

        // Custom header rendering
        JTableHeader header = homeworkTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Container.borderColor")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(homeworkTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UIManager.getColor("Table.background"));
        add(scrollPane, BorderLayout.CENTER);

        // Create bottom panel with navigation and action buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        backButton.setPreferredSize(new Dimension(120, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));
        leftPanel.add(backButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        refreshButton = new JButton("Refresh");
        viewDetailsButton = new JButton("View Details");
        submitButton = new JButton("Submit Homework");

        Dimension buttonSize = new Dimension(120, 30);
        refreshButton.setPreferredSize(buttonSize);
        viewDetailsButton.setPreferredSize(buttonSize);
        submitButton.setPreferredSize(new Dimension(160, 30));

        refreshButton.addActionListener(e -> loadHomeworkData());
        viewDetailsButton.addActionListener(this::viewHomeworkDetails);
        submitButton.addActionListener(this::submitHomework);

        rightPanel.add(refreshButton);
        rightPanel.add(viewDetailsButton);
        rightPanel.add(submitButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);

        // Set custom renderer for status column
        homeworkTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
    }

    /**
     * Loads homework data for the current class and student.
     */
    private void loadHomeworkData() {
        tableModel.setRowCount(0);
        rowToHomeworkIdMap.clear();
        List<Homework> homeworkList = HomeworkManager.getHomeworkByClass(currentClassId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int rowIndex = 0;
        for (Homework homework : homeworkList) {
            if (!homework.getClassId().equals(currentClassId)) {
                continue;
            }

            boolean isOverdue = homework.getDeadline().isBefore(currentTime);
            Homework.Submission submission = homework.getSubmission(currentStudentId);

            String status;
            if (submission != null) {
                status = "✅ Submitted";
            } else {
                status = isOverdue ? "❌ Overdue" : "⏳ Pending";
            }

            tableModel.addRow(new Object[]{
                homework.getSubject(),
                homework.getDescription(),
                homework.getDeadline().format(formatter),
                status,
                submission != null ? submission.getGrade() : "-"
            });

            rowToHomeworkIdMap.put(rowIndex, homework.getId());
            rowIndex++;
        }
    }

    /**
     * Handles logic for submitting or resubmitting homework.
     */
    private void submitHomework(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to submit",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String homeworkId = rowToHomeworkIdMap.get(selectedRow);
        Homework homework = getHomeworkById(homeworkId);

        if (homework == null) {
            JOptionPane.showMessageDialog(this, "Homework not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Homework.Submission submission = homework.getSubmission(currentStudentId);

        if (submission != null && submission.getGrade() != null && !submission.getGrade().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "You have already received a grade for this homework. Resubmission is not allowed.",
                    "Submission Not Allowed",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        boolean isOverdue = homework.getDeadline().isBefore(currentTime);

        if (isOverdue && submission != null) {
            JOptionPane.showMessageDialog(this,
                    "You already submitted this homework and the deadline has passed.\n"
                    + "You cannot resubmit after the deadline.",
                    "Resubmission Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isOverdue && submission == null) {
            JOptionPane.showMessageDialog(this,
                    "The deadline has passed. You can no longer submit this homework.",
                    "Late Submission Not Allowed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (submission != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "You've already submitted this homework. Resubmit?",
                    "Confirm Resubmission",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            HomeworkManager.addSubmission(homeworkId, currentStudentId,
                    fileChooser.getSelectedFile().getAbsolutePath(), LocalDateTime.now());
            loadHomeworkData();
            JOptionPane.showMessageDialog(this,
                    submission != null ? "Submission updated!" : "Submission received!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Displays detailed information about the selected homework and submission.
     */
    private void viewHomeworkDetails(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to view details",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String homeworkId = rowToHomeworkIdMap.get(selectedRow);
        Homework homework = getHomeworkById(homeworkId);

        if (homework != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedDeadline = homework.getDeadline().format(formatter);
            boolean isOverdue = homework.getDeadline().isBefore(currentTime);

            StringBuilder details = new StringBuilder();
            details.append("Subject: ").append(homework.getSubject()).append("\n");
            details.append("Description: ").append(homework.getDescription()).append("\n");
            details.append("Deadline: ").append(formattedDeadline).append("\n\n");

            Homework.Submission submission = homework.getSubmission(currentStudentId);
            if (submission != null) {
                String filePath = submission.getFilePath();
                String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                String formattedSubmissionDate = submission.getSubmissionDate() != null
                        ? submission.getSubmissionDate().format(formatter) : "Not specified";

                details.append("Your Submission:\n");
                details.append("File: ").append(fileName).append("\n");
                details.append("Submitted: ").append(formattedSubmissionDate).append("\n");
                details.append("Grade: ").append(submission.getGrade());
            } else if (isOverdue) {
                details.append("You haven't submitted this homework and the deadline has passed.\n");
                details.append("Late submissions are not accepted.");
            } else {
                details.append("You haven't submitted this homework yet.");
            }

            JOptionPane.showMessageDialog(this, details.toString(),
                    "Homework Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Retrieves a Homework object by its ID.
     */
    private Homework getHomeworkById(String id) {
        List<Homework> homeworkList = HomeworkManager.getHomeworkByClass(currentClassId);
        for (Homework hw : homeworkList) {
            if (hw.getId().equals(id)) {
                return hw;
            }
        }
        return null;
    }

    /**
     * Custom cell renderer for displaying status icons in the "Status" column.
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);

            if (value != null && column == 3) {
                String status = value.toString();
                if (status.contains("Overdue")) {
                    setText("❌ Overdue");
                } else if (status.contains("Submitted")) {
                    setText("✅ Submitted");
                } else if (status.contains("Pending")) {
                    setText("⏳ Pending");
                }
            }

            return this;
        }
    }

    /**
     * Updates component colors and styling to reflect the current Look and Feel
     * (theme). Ensures consistent appearance when switching between themes.
     */
    private void updateComponentStyles() {
        if (homeworkTable != null) {
            homeworkTable.setBackground(UIManager.getColor("Table.background"));
            homeworkTable.setForeground(UIManager.getColor("Table.foreground"));
            homeworkTable.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            homeworkTable.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            homeworkTable.repaint();
        }

        Component parent = getParent();
        if (parent != null) {
            parent.setBackground(UIManager.getColor("Panel.background"));
        }

        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Listens for Look and Feel changes and updates component styles
     * accordingly. Keeps the UI in sync with theme updates at runtime.
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
     * Called when the panel is removed from its parent. Removes theme listener
     * to avoid memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

}
