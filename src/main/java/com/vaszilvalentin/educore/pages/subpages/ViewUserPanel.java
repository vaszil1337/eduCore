package com.vaszilvalentin.educore.pages.subpages;

import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.Subject;
import com.vaszilvalentin.educore.users.ClassLevel;
import com.vaszilvalentin.educore.users.CurrentUserSelection;
import com.vaszilvalentin.educore.window.WindowManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Swing panel for viewing user details (students or teachers). Displays all
 * user information in a read-only format.
 */
public class ViewUserPanel extends JPanel {

    private static ViewUserPanel currentInstance;
    private final WindowManager windowManager;
    private final PropertyChangeListener themeChangeListener = new ThemeChangeListener();
    private User userToView;
    private String viewableId;

    // Display components (all read-only)
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel roleLabel;
    private JLabel birthDateLabel;
    private JPanel subjectsPanel;
    private JPanel classesPanel;
    private JLabel studentClassLabel;

    /**
     * Constructs a ViewUserPanel with window management.
     *
     * @param windowManager The application's window manager
     */
    public ViewUserPanel(WindowManager windowManager) {
        currentInstance = this;
        this.windowManager = windowManager;
        this.viewableId = CurrentUserSelection.getViewUserId();
        loadUserToView();
        initPanel();
        UIManager.addPropertyChangeListener(themeChangeListener);
    }

    /**
     * Loads the user to view based on current selection ID.
     */
    private void loadUserToView() {
        this.userToView = UserManager.getAllUsers().stream()
                .filter(user -> user.getId().equals(viewableId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Initializes the panel UI components in read-only mode.
     */
    private void initPanel() {
        removeAll();
        setLayout(new GridBagLayout());

        // Main display container
        JPanel displayBox = new JPanel();
        displayBox.setLayout(new BoxLayout(displayBox, BoxLayout.Y_AXIS));
        displayBox.setPreferredSize(new Dimension(700, 600));
        displayBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Container.borderColor"), 1),
                new EmptyBorder(30, 20, 30, 20)
        ));
        displayBox.setBackground(UIManager.getColor("Table.background"));

        // Content panels
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(createLeftPanel());
        contentPanel.add(createRightPanel());
        displayBox.add(contentPanel);
        displayBox.add(Box.createVerticalStrut(20));

        // Back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            CurrentUserSelection.clear();
            windowManager.switchToPage("ManageUsers");
        });
        buttonPanel.add(backButton);
        displayBox.add(buttonPanel);

        // Center the display
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(displayBox, gbc);

        // Populate with user data
        populateDisplayWithUserData();

        revalidate();
        repaint();
    }

    /**
     * Populates display components with user data.
     */
    private void populateDisplayWithUserData() {
        if (userToView == null) {
            return;
        }

        nameLabel.setText(userToView.getName());
        emailLabel.setText(userToView.getEmail());
        roleLabel.setText(capitalizeFirstLetter(userToView.getRole()));
        birthDateLabel.setText(userToView.getBirthDate().toString());

        if ("student".equalsIgnoreCase(userToView.getRole())) {
            studentClassLabel.setText(userToView.getClassId());
        } else if ("teacher".equalsIgnoreCase(userToView.getRole())) {
            studentClassLabel.setText("-");

            List<String> subjects = userToView.getSubjects();
            if (subjects != null) {
                for (Subject subject : Subject.values()) {
                    if (subjects.contains(subject.toString())) {
                        JLabel subjectLabel = new JLabel("• " + subject.toString());
                        subjectLabel.setBorder(new EmptyBorder(2, 10, 2, 0));
                        subjectsPanel.add(subjectLabel);
                    }
                }
            }

            List<String> classes = userToView.getTaughtClasses();
            if (classes != null) {
                for (ClassLevel classLevel : ClassLevel.values()) {
                    if (classes.contains(classLevel.toString())) {
                        JLabel classLabel = new JLabel("• " + classLevel.toString());
                        classLabel.setBorder(new EmptyBorder(2, 10, 2, 0));
                        classesPanel.add(classLabel);
                    }
                }
            }
        } else {
            studentClassLabel.setText("-");
        }

    }

    /**
     * Creates the left panel with basic user information.
     *
     * @return Configured JPanel with basic info display
     */
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        leftPanel.setBackground(UIManager.getColor("Table.background"));
        leftPanel.setPreferredSize(new Dimension(300, 550));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Basic Information");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Name display
        gbc.gridy++;
        leftPanel.add(createInfoLabel("Full Name:", labelFont), gbc);
        nameLabel = createDisplayLabel();
        gbc.gridy++;
        leftPanel.add(nameLabel, gbc);

        // Email display
        gbc.gridy++;
        leftPanel.add(createInfoLabel("Email:", labelFont), gbc);
        emailLabel = createDisplayLabel();
        gbc.gridy++;
        leftPanel.add(emailLabel, gbc);

        // Role display
        gbc.gridy++;
        leftPanel.add(createInfoLabel("Role:", labelFont), gbc);
        roleLabel = createDisplayLabel();
        gbc.gridy++;
        leftPanel.add(roleLabel, gbc);

        // Birth date display
        gbc.gridy++;
        leftPanel.add(createInfoLabel("Birth Date:", labelFont), gbc);
        birthDateLabel = createDisplayLabel();
        gbc.gridy++;
        leftPanel.add(birthDateLabel, gbc);

        return leftPanel;
    }

    /**
     * Creates the right panel with role-specific information.
     *
     * @return Configured JPanel with role-specific display
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        rightPanel.setBackground(UIManager.getColor("Table.background"));
        rightPanel.setPreferredSize(new Dimension(300, 550));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        Font labelFont = getFont().deriveFont(Font.BOLD, 14);
        JLabel titleLabel = new JLabel("Role-Specific Information");
        titleLabel.setFont(labelFont.deriveFont(Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Student class display
        gbc.gridy++;
        rightPanel.add(createInfoLabel("Class (for students):", labelFont), gbc);
        studentClassLabel = createDisplayLabel();
        gbc.gridy++;
        rightPanel.add(studentClassLabel, gbc);

        // Teacher subjects display
        gbc.gridy++;
        rightPanel.add(createInfoLabel("Subjects (for teachers):", labelFont), gbc);
        subjectsPanel = new JPanel();
        subjectsPanel.setLayout(new BoxLayout(subjectsPanel, BoxLayout.Y_AXIS));
        subjectsPanel.setOpaque(false);
        subjectsPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        gbc.gridy++;
        rightPanel.add(subjectsPanel, gbc);

        // Teacher classes display
        gbc.gridy++;
        rightPanel.add(createInfoLabel("Taught Classes (for teachers):", labelFont), gbc);
        classesPanel = new JPanel();
        classesPanel.setLayout(new BoxLayout(classesPanel, BoxLayout.Y_AXIS));
        classesPanel.setOpaque(false);
        classesPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        gbc.gridy++;
        rightPanel.add(classesPanel, gbc);

        return rightPanel;
    }

    /**
     * Creates a styled label for field names.
     *
     * @param text The label text
     * @param font The font to use
     * @return Configured JLabel
     */
    private JLabel createInfoLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(UIManager.getColor("Label.foreground"));
        return label;
    }

    /**
     * Creates a styled label for displaying information.
     *
     * @return Configured JLabel
     */
    private JLabel createDisplayLabel() {
        JLabel label = new JLabel();
        label.setFont(getFont().deriveFont(14f));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("TextField.borderColor"), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        label.setOpaque(true);
        label.setBackground(UIManager.getColor("TextField.background"));
        label.setMinimumSize(new Dimension(200, 30));
        label.setPreferredSize(new Dimension(200, 30));
        return label;
    }

    /**
     * Capitalizes the first letter of a string.
     *
     * @param input The string to capitalize
     * @return The capitalized string
     */
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Reloads the user to view based on current selection.
     */
    public void reloadUserToView() {
        this.viewableId = CurrentUserSelection.getViewUserId();
        loadUserToView();
    }

    /**
     * Listener for theme changes that refreshes the UI.
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
        UIManager.removePropertyChangeListener(themeChangeListener);
    }

    /**
     * Refreshes the current instance of the panel.
     */
    public static void refreshCurrentInstance() {
        if (currentInstance != null) {
            currentInstance.reloadUserToView();
            currentInstance.initPanel();
        }
    }
}
