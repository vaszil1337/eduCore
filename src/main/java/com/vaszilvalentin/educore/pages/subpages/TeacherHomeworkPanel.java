package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.homework.CurrentHomework;
import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.utils.NetworkTimeChecker;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * This panel represents the homework management UI for teachers. It allows
 * viewing, creating, editing, and removing homework assignments relevant to the
 * teacher's subjects and classes.
 */
public class TeacherHomeworkPanel extends JPanel {

    private JTable homeworkTable;
    private DefaultTableModel tableModel;
    private JPanel loadingPanel;
    private JLabel loadingLabel;
    private final WindowManager windowManager;
    private final List<String> teacherSubjects;
    private final List<String> teacherClasses;
    private LocalDateTime currentTime;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();

    /**
     * Constructor initializes the UI and loads homework data asynchronously.
     *
     * @param windowManager Reference to the window manager for navigation.
     */
    public TeacherHomeworkPanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.teacherSubjects = CurrentUser.getCurrentUser().getSubjects();
        this.teacherClasses = CurrentUser.getCurrentUser().getTaughtClasses();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initializeTable();
        initializeButtons();
        showLoadingAnimation();

        // Fetch network time asynchronously and then load homework data
        NetworkTimeChecker.getNetworkTimeAsync().thenAccept(time -> {
            currentTime = time;
            SwingUtilities.invokeLater(() -> {
                hideLoadingAnimation();
                loadHomeworkData();
            });
        });
        // Register theme listener to update UI on theme change
        UIManager.addPropertyChangeListener(themeChangeListener);

    }

    /**
     * Displays a loading animation while data is being fetched.
     */
    private void showLoadingAnimation() {
        removeAll(); // Clear existing components

        // Panel using GridBagLayout to center content
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
     * Removes the loading animation and restores the main UI.
     */
    private void hideLoadingAnimation() {
        remove(loadingPanel);
        initializeTable();
        initializeButtons();
        revalidate();
        repaint();
    }

    /**
     * Sets up the homework table with headers, styles, and cell behavior.
     */
    private void initializeTable() {
        String[] columnNames = {"ID", "Class", "Subject", "Description", "Deadline"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        homeworkTable = new JTable(tableModel);
        homeworkTable.setRowHeight(30);
        homeworkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        homeworkTable.setShowGrid(false);
        homeworkTable.setIntercellSpacing(new Dimension(0, 0));

        homeworkTable.setBackground(UIManager.getColor("Table.background"));
        homeworkTable.setForeground(UIManager.getColor("Table.foreground"));

        // Hide the ID column (used internally)
        homeworkTable.getColumnModel().getColumn(0).setMinWidth(0);
        homeworkTable.getColumnModel().getColumn(0).setMaxWidth(0);
        homeworkTable.getColumnModel().getColumn(0).setWidth(0);

        // Custom renderer for table cells (centered alignment, alternating colors)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(noFocusBorder);

                if (isSelected) {
                    setBackground(UIManager.getColor("Table.selectionBackground"));
                    setForeground(UIManager.getColor("Table.selectionForeground"));
                } else {
                    setBackground(row % 2 == 0
                            ? UIManager.getColor("Table.background")
                            : UIManager.getColor("Table.alternateRowColor"));
                    setForeground(UIManager.getColor("Table.foreground"));
                }
                return this;
            }
        };

        for (int i = 0; i < homeworkTable.getColumnCount(); i++) {
            homeworkTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Style the table header
        JTableHeader header = homeworkTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(UIManager.getColor("TableHeader.background"));
                setForeground(UIManager.getColor("TableHeader.foreground"));
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Container.borderColor")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(homeworkTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIManager.getColor("Table.background"));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes UI buttons for navigation and actions.
     */
    private void initializeButtons() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        backButton.setPreferredSize(new Dimension(120, 30));
        backButton.setMargin(new Insets(5, 10, 5, 10));
        leftPanel.add(backButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Initialize all action buttons
        JButton refreshButton = new JButton("Refresh");
        JButton createButton = new JButton("Create Homework");
        JButton editButton = new JButton("Edit Homework");
        JButton removeButton = new JButton("Remove Homework");
        JButton detailsButton = new JButton("Homework Details");

        // Consistent sizing for all buttons
        Dimension buttonSize = new Dimension(150, 30);
        refreshButton.setPreferredSize(buttonSize);
        createButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);
        detailsButton.setPreferredSize(buttonSize);

        // Attach actions
        refreshButton.addActionListener(e -> loadHomeworkData());
        createButton.addActionListener(this::createHomework);
        editButton.addActionListener(this::editHomework);
        removeButton.addActionListener(this::removeHomework);
        detailsButton.addActionListener(this::showHomeworkDetails);

        rightPanel.add(refreshButton);
        rightPanel.add(createButton);
        rightPanel.add(editButton);
        rightPanel.add(removeButton);
        rightPanel.add(detailsButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads and filters homework based on the teacher's assigned subjects and
     * classes.
     */
    private void loadHomeworkData() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<Homework> allHomework = HomeworkManager.getAllHomework();

        for (Homework hw : allHomework) {
            if (teacherClasses.contains(hw.getClassId()) && teacherSubjects.contains(hw.getSubject())) {
                tableModel.addRow(new Object[]{
                    hw.getId(),
                    hw.getClassId(),
                    hw.getSubject(),
                    hw.getDescription(),
                    hw.getDeadline().format(formatter)
                });
            }
        }
    }

    /**
     * Opens the Create Homework page.
     */
    private void createHomework(ActionEvent e) {
        CreateHomeworkPanel.refreshCurrentInstance();
        windowManager.switchToPage("CreateHomework");
    }

    /**
     * Opens the Edit Homework page for the selected homework item.
     */
    private void editHomework(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to edit.");
            return;
        }

        String homeworkId = (String) tableModel.getValueAt(selectedRow, 0);

        List<Homework> allHomework = HomeworkManager.getAllHomework();
        for (Homework hw : allHomework) {
            if (hw.getId().equals(homeworkId)) {
                CurrentHomework.setEditHomeworkId(homeworkId);
                EditHomeworkPanel.refreshCurrentInstance();
                windowManager.switchToPage("EditHomework");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Could not find selected homework.");
    }

    /**
     * Removes the selected homework item after confirmation.
     */
    private void removeHomework(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this homework assignment?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String homeworkId = (String) tableModel.getValueAt(selectedRow, 0);

        try {
            HomeworkManager.deleteHomework(homeworkId);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Homework removed successfully.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error removing homework: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Opens a details view for the selected homework item.
     */
    private void showHomeworkDetails(ActionEvent e) {
        int selectedRow = homeworkTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a homework to view details.");
            return;
        }

        String homeworkId = (String) tableModel.getValueAt(selectedRow, 0);
        CurrentHomework.setViewHomeworkId(homeworkId);
        HomeworkDetailsPanel.refreshCurrentInstance();
        windowManager.switchToPage("HomeworkDetails");
    }

    /**
     * Updates the table and scroll pane appearance to match the current UI
     * theme. Ensures consistent visual appearance on theme changes.
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
     * accordingly. This ensures the panel remains visually consistent across
     * theme changes.
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
     * Called when the component is removed. Cleans up UI listeners to prevent
     * memory leaks.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

}
