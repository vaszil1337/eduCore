package com.vaszilvalentin.educore.users;

/**
 * Utility class for managing temporary user selection state across the application.
 *
 * This class maintains static references to user IDs that are currently:
 * - Being edited (edit mode)
 * - Being viewed (details mode)
 *
 * Primary use cases include:
 * - Passing user context between screens without constructor parameters
 * - Maintaining state during navigation flows
 * - Providing thread-safe access to current selection
 *
 * Note: This class uses static fields and should be cleared when
 * selection state is no longer needed to prevent memory leaks.
 */
public class CurrentUserSelection {

    // Static fields maintain state across application
    private static String editUserId;    // ID of user being modified
    private static String viewUserId;    // ID of user being viewed

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private CurrentUserSelection() {}

    // Edit User Operations

    /**
     * Sets the ID of the user currently being edited.
     * @param userId The unique identifier of the user to edit
     */
    public static void setEditUserId(String userId) {
        editUserId = userId;
    }

    /**
     * Retrieves the ID of the user marked for editing.
     * @return The user ID or null if no user is being edited
     */
    public static String getEditUserId() {
        return editUserId;
    }

    /**
     * Checks whether there is a user currently selected for editing.
     * @return true if an edit user ID is set, false otherwise
     */
    public static boolean hasEditUserId() {
        return editUserId != null;
    }

    // View User Operations

    /**
     * Sets the ID of the user currently being viewed.
     * @param userId The unique identifier of the user to view
     */
    public static void setViewUserId(String userId) {
        viewUserId = userId;
    }

    /**
     * Retrieves the ID of the user marked for viewing.
     * @return The user ID or null if no user is being viewed
     */
    public static String getViewUserId() {
        return viewUserId;
    }

    // State Management

    /**
     * Clears all current user selections.
     *
     * Important: Should be called when:
     * - Navigation is complete
     * - Operation is cancelled
     * - Application state needs resetting
     */
    public static void clear() {
        editUserId = null;
        viewUserId = null;
    }
}
