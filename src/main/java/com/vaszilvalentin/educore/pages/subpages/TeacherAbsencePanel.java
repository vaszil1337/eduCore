package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.absence.AbsenceCertificate;
import com.vaszilvalentin.educore.absence.AbsenceCertificateManager;
import com.vaszilvalentin.educore.window.WindowManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A Swing panel for managing student absence certificates by teachers. Provides
 * functionality to view, approve, and examine absence certificates for students
 * in the teacher's classes.
 */
public class TeacherAbsencePanel extends JPanel {

    // UI Components
    private final JTable table;                   // Table displaying absence certificates
    private final DefaultTableModel tableModel;   // Data model for the table

    // Application dependencies
    private final WindowManager windowManager;    // Manages application window navigation

    // Data management
    private final Map<Integer, String> rowToCertificateIdMap = new HashMap<>(); // Maps table rows to certificate IDs
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener(); // Handles theme changes

    /**
     * Constructs a TeacherAbsencePanel with the specified window manager.
     * Initializes UI components and loads absence certificate data.
     *
     * @param windowManager The window manager for navigation between panels
     */
    public TeacherAbsencePanel(WindowManager windowManager) {
        this.windowManager = windowManager;

        // Configure panel layout and borders
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialize table model with column headers
        tableModel = new DefaultTableModel(new String[]{
            "Student Name", "Class", "Email", "Start Date",
            "End Date", "Type", "Status", "Approved"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };

        // Configure table properties
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        // Build UI and load data
        initializeUI();
        loadAllCertificates();

        // Register for theme changes
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the user interface components including: - Scrollable table
     * for displaying certificates - Button panel with navigation and action
     * buttons - Column formatting and alignment
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Configure scrollable table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Configure table columns
        configureTableColumns();

        // Create and add button panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Configures table column properties including: - Alignment (center for all
     * columns) - Preferred widths for each column
     */
    private void configureTableColumns() {
        // Center-align all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            column.setCellRenderer(renderer);
        }

        // Set column-specific widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // Name
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Class
        table.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Start Date
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // End Date
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Type
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Approved
    }

    /**
     * Creates the button panel containing: - Back button for navigation -
     * Refresh button to reload data - View button to examine documents -
     * Approve button to certify absences - Add button to create new absence
     * records
     *
     * @return Configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Left-aligned back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> windowManager.switchToPage("Home"));
        leftPanel.add(backButton);

        // Right-aligned action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // Refresh button
        rightPanel.add(new JButton("Refresh") {
            {
                addActionListener(e -> loadAllCertificates());
            }
        });

        // View button
        rightPanel.add(new JButton("View Document") {
            {
                addActionListener(e -> handleView());
            }
        });

        // Approve button
        rightPanel.add(new JButton("Approve") {
            {
                addActionListener(e -> handleApprove());
            }
        });

        // Delete button
        rightPanel.add(new JButton("Delete") {
            {
                addActionListener(e -> handleDelete());
                setToolTipText("Delete selected absence record");
            }
        });

        // New Add Absence button
        rightPanel.add(new JButton("Add Absence") {
            {
                addActionListener(e -> {
                    CreateAbsencePanel.refreshCurrentInstance();
                    windowManager.switchToPage("CreateAbsence");
                });
                setToolTipText("Create new absence record");
            }
        });

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    /**
     * Loads and displays all absence certificates in the system. Filters and
     * shows only certificates for students.
     */
    private void loadAllCertificates() {
        // Clear existing data
        tableModel.setRowCount(0);
        rowToCertificateIdMap.clear();

        // Load and process certificates
        List<AbsenceCertificate> certs = AbsenceCertificateManager.getAllCertificates();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int row = 0;
        for (AbsenceCertificate cert : certs) {
            User student = UserManager.getAllUsers().stream()
                    .filter(u -> u.getId().equals(cert.getUserId()))
                    .findFirst()
                    .orElse(null);

            // Skip non-students or missing user records
            if (student == null || !"student".equals(student.getRole())) {
                continue;
            }

            // Prepare display values
            String status = cert.hasFile() ? "✅ Uploaded" : "⏳ Pending";
            String type = cert.hasType() ? cert.getCertificateType() : "⏳ Pending";
            String approvedStatus = cert.getApprovedStatus() ? "✅ Approved" : "❌ Not Approved";

            // Add row to table
            tableModel.addRow(new Object[]{
                student.getName(),
                student.getClassId(),
                student.getEmail(),
                cert.getStartDate().format(formatter),
                cert.getEndDate().format(formatter),
                type,
                status,
                approvedStatus
            });

            // Maintain row-to-ID mapping
            rowToCertificateIdMap.put(row++, cert.getId());
        }
    }

    /**
     * Handles certificate approval workflow: - Validates selection - Checks
     * certificate state - Confirms with teacher - Updates status if confirmed
     */
    private void handleApprove() {
        // Validate selection
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select a certificate to approve.");
            return;
        }

        // Retrieve certificate
        String certId = rowToCertificateIdMap.get(selectedRow);
        AbsenceCertificate cert = AbsenceCertificateManager.getCertificateById(certId);

        // Validate certificate state
        if (cert == null) {
            showMessage("Certificate not found!");
            return;
        }
        if (cert.getApprovedStatus()) {
            showMessage("This certificate is already approved!");
            return;
        }
        if (!cert.hasFile()) {
            showMessage("Cannot approve - no file uploaded yet!");
            return;
        }

        // Get student information
        User student = UserManager.getAllUsers().stream()
                .filter(u -> u.getId().equals(cert.getUserId()))
                .findFirst()
                .orElse(null);

        // Prepare confirmation dialog
        String studentInfo = student != null
                ? student.getName() + " (" + student.getEmail() + ")"
                : "Student ID: " + cert.getUserId();

        int confirm = showConfirmDialog(
                "Approve absence certificate for:\n" + studentInfo + "\n"
                + "Period: " + cert.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                + " to " + cert.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "Confirm Approval");

        // Process approval
        if (confirm == JOptionPane.YES_OPTION) {
            cert.setApprovedStatus(true);
            AbsenceCertificateManager.updateCertificate(cert);
            loadAllCertificates();
            showMessage("Certificate approved successfully!");
        }
    }

    /**
     * Handles document viewing workflow: - Validates selection - Checks for
     * attached file - Opens document with system default application
     */
    private void handleView() {
        // Validate selection
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select a certificate to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Retrieve and validate certificate
        String certId = rowToCertificateIdMap.get(selectedRow);
        AbsenceCertificate cert = AbsenceCertificateManager.getCertificateById(certId);

        if (cert == null) {
            showError("Certificate not found!");
            return;
        }
        if (!cert.hasFile()) {
            showError("No file uploaded for this certificate yet!");
            return;
        }

        // Open document
        openSubmissionFile(cert.getFilePath());
    }

    /**
     * Handles certificate deletion workflow: - Validates selection - Confirms
     * with teacher - Deletes certificate if confirmed
     */
    private void handleDelete() {
        // Validate selection
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select a certificate to delete.");
            return;
        }

        // Retrieve certificate ID from the mapping
        String certId = rowToCertificateIdMap.get(selectedRow);
        if (certId == null) {
            showError("Certificate ID not found for selected row!");
            return;
        }

        // Get certificate details for confirmation dialog
        AbsenceCertificate cert = AbsenceCertificateManager.getCertificateById(certId);
        if (cert == null) {
            showError("Certificate not found in the system!");
            return;
        }

        // Get student information for confirmation message
        User student = UserManager.getAllUsers().stream()
                .filter(u -> u.getId().equals(cert.getUserId()))
                .findFirst()
                .orElse(null);

        // Prepare confirmation message
        String studentInfo = student != null
                ? student.getName() + " (" + student.getClassId() + ")"
                : "Student ID: " + cert.getUserId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateRange = cert.getStartDate().format(formatter) + " to " + cert.getEndDate().format(formatter);

        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete this absence certificate?\n\n"
                + "Student: " + studentInfo + "\n"
                + "Period: " + dateRange + "\n"
                + "Type: " + (cert.hasType() ? cert.getCertificateType() : "Not specified"),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // Process deletion if confirmed
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                AbsenceCertificateManager.deleteCertificate(certId);
                showMessage("Certificate deleted successfully.", "Deletion Complete", JOptionPane.INFORMATION_MESSAGE);
                loadAllCertificates(); // Refresh the table
            } catch (Exception e) {
                showError("Failed to delete certificate: " + e.getMessage());
            }
        }
    }

    /**
     * Attempts to open a submission file with the system default application.
     *
     * @param filePath The path to the file to open
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

    // Helper methods for dialog display
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void showError(String message) {
        showMessage(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Cleans up resources when panel is removed. Unregisters theme change
     * listener.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

    /**
     * Updates component styles when look and feel changes. Applies theme colors
     * to table and panel.
     */
    private void updateComponentStyles() {
        if (table != null) {
            table.setBackground(UIManager.getColor("Table.background"));
            table.setForeground(UIManager.getColor("Table.foreground"));
            table.getTableHeader().setBackground(UIManager.getColor("TableHeader.background"));
            table.getTableHeader().setForeground(UIManager.getColor("TableHeader.foreground"));
            table.repaint();
        }
        setBackground(UIManager.getColor("Panel.background"));
        repaint();
    }

    /**
     * Listens for theme changes and updates UI accordingly.
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
}
