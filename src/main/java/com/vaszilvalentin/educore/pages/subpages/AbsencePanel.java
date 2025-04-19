package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.absence.AbsenceCertificate;
import com.vaszilvalentin.educore.absence.AbsenceCertificateManager;
import com.vaszilvalentin.educore.auth.CurrentUser;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A panel for managing and displaying absence certificates for the current
 * user. Provides functionality to view, upload, and manage absence certificates
 * with a tabular interface and file upload capabilities.
 */
public class AbsencePanel extends JPanel {

    // UI Components
    private final JTable table;                   // Table displaying absence certificates
    private final DefaultTableModel tableModel;    // Data model for the table

    // Application state
    private final String userId;                  // ID of the current user
    private final WindowManager windowManager;     // Manager for window navigation

    // Data management
    private final Map<Integer, String> rowToCertificateIdMap = new HashMap<>();  // Maps table rows to certificate IDs
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();  // Listens for theme changes

    /**
     * Constructs an AbsencePanel with the specified window manager. Initializes
     * UI components and loads existing absence certificates.
     *
     * @param windowManager The window manager for navigation between panels
     */
    public AbsencePanel(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.userId = CurrentUser.getCurrentUser().getId();

        // Configure panel layout and borders
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Initialize table model with column headers
        tableModel = new DefaultTableModel(new String[]{"Start", "End", "Type", "Status", "Approved"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all cells non-editable
                return false;
            }
        };

        // Configure table with model and row height
        table = new JTable(tableModel);
        table.setRowHeight(30);

        // Build UI and load data
        initializeUI();
        loadCertificates();

        // Register for theme changes
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Initializes the user interface components including: - The main
     * certificate table with scroll pane - Button panel with navigation and
     * action buttons - Column formatting and alignment
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Configure scrollable table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Center-align all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            column.setCellRenderer(centerRenderer);
        }

        // Create button panel at bottom
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates and configures the button panel containing: - Back button for
     * navigation - Refresh button to reload certificates - Upload button for
     * file submissions
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
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton uploadButton = new JButton("Upload File");
        JButton refreshButton = new JButton("Refresh");

        uploadButton.addActionListener(this::handleUpload);
        refreshButton.addActionListener(e -> loadCertificates());

        rightPanel.add(refreshButton);
        rightPanel.add(uploadButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    /**
     * Loads absence certificates for the current user and populates the table.
     * Clears existing data and maps table rows to certificate IDs.
     */
    private void loadCertificates() {
        // Clear existing data
        tableModel.setRowCount(0);
        rowToCertificateIdMap.clear();

        // Load certificates for current user
        List<AbsenceCertificate> certs = AbsenceCertificateManager.getCertificatesByUser(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Populate table with certificate data
        int row = 0;
        for (AbsenceCertificate cert : certs) {
            String status = cert.hasFile() ? "✅ Uploaded" : "⏳ Pending";
            String type = cert.hasType() ? cert.getCertificateType() : "⏳ Pending";
            String approvedStatus = cert.getApprovedStatus() ? "✅ Approved" : "⏳ Pending";

            tableModel.addRow(new Object[]{
                cert.getStartDate().format(formatter),
                cert.getEndDate().format(formatter),
                type,
                status,
                approvedStatus
            });

            // Maintain mapping between table rows and certificate IDs
            rowToCertificateIdMap.put(row, cert.getId());
            row++;
        }
    }

    /**
     * Handles file upload for the selected absence certificate. Validates
     * selection and certificate state before allowing upload.
     *
     * @param e The action event triggering the upload
     */
    private void handleUpload(ActionEvent e) {
        // Validate row selection
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Please select an absence entry to upload a file to.");
            return;
        }

        // Get selected certificate
        String certId = rowToCertificateIdMap.get(selectedRow);
        List<AbsenceCertificate> certs = AbsenceCertificateManager.getCertificatesByUser(userId);

        for (AbsenceCertificate cert : certs) {
            if (cert.getId().equals(certId)) {
                // Check approval status
                if (cert.getApprovedStatus()) {
                    showMessage("This certificate has already been approved. File upload is not allowed.");
                    return;
                }

                // Handle existing file case
                if (cert.hasFile()) {
                    int confirm = showConfirmDialog(
                            "A file has already been uploaded. Replace it?",
                            "Confirm Upload");
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                // Process file selection
                processFileUpload(cert, certId);
                return;
            }
        }

        showMessage("Certificate entry not found.");
    }

    /**
     * Processes the file upload workflow including: - File selection via file
     * chooser - Certificate type input - Saving the updated certificate
     *
     * @param cert The certificate being updated
     * @param certId The ID of the certificate
     */
    private void processFileUpload(AbsenceCertificate cert, String certId) {
        JFileChooser fileChooser = new JFileChooser();

        // Remove the default "All files" filter
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Add individual filters
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));

        // Set the combined filter as default
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "All Supported Formats (*.pdf, *.png, *.jpg, *.jpeg)",
                "docx", "pdf", "png", "jpg", "jpeg"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // Get certificate type from user
        JTextField typeField = new JTextField();
        int typeResult = showInputDialog(
                "Certificate Type (e.g., Medical, Parental Justification):",
                typeField,
                "Set Certificate Type");

        if (typeResult == JOptionPane.OK_OPTION) {
            String type = typeField.getText().trim();
            if (type.isEmpty()) {
                showMessage("Certificate type cannot be empty.");
                return;
            }

            // Update and save certificate
            cert.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
            cert.setCertificateType(type);

            AbsenceCertificateManager.deleteCertificate(certId);
            AbsenceCertificateManager.addCertificate(cert);

            loadCertificates();
            showMessage("File and type submitted successfully!");
        }
    }

    // Helper methods for dialog display
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }

    private int showInputDialog(String message, JTextField field, String title) {
        return JOptionPane.showConfirmDialog(this, new Object[]{message, field},
                title, JOptionPane.OK_CANCEL_OPTION);
    }

    /**
     * Cleans up resources when the panel is removed. Unregisters theme change
     * listener.
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

    /**
     * Updates component styles when the look and feel changes. Applies theme
     * colors to the table and panel.
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
     * Listener for theme change events. Triggers UI updates when the look and
     * feel changes.
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
