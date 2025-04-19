/*
 * Authentication manager for handling user login, logout, and authentication.
 */
package com.vaszilvalentin.educore.auth;

/**
 * @author vaszilvalentin
 */
import com.vaszilvalentin.educore.Main;
import com.vaszilvalentin.educore.log.LoginLogManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;

import java.util.List;

public class AuthManager {

    // A static list of users fetched from the database via UserManager
    private static List<User> users = UserManager.getAllUsers();

    /**
     * Finds a user in the system by their email address.
     *
     * @param email The email address to search for
     * @return The corresponding User object if found, otherwise null
     */
    public static User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user; // Return user if a match is found
            }
        }
        return null; // Return null if no matching user is found
    }

    /**
     * Authenticates a user by checking if their email and password are valid.
     *
     * @param email The user's email address
     * @param inputPassword The password entered by the user
     * @return true if authentication is successful, false otherwise
     */
    public static boolean authenticate(String email, String inputPassword) {
        // Find the user by email
        User user = findUserByEmail(email);

        // Verify the password if user exists
        if (user != null && UserManager.verifyPassword(user, inputPassword)) {
            // Set the authenticated user as the currently logged-in user
            CurrentUser.setCurrentUser(user);
            LoginLogManager.logLogin(user.getId());
            return true;
        }

        return false; // Return false if authentication fails
    }

    /**
     * Logs out the currently authenticated user.
     */
    public static void logout(boolean isAppExit) {
        if (CurrentUser.getCurrentUser() != null) {
            LoginLogManager.logLogout(CurrentUser.getCurrentUser().getId());
            CurrentUser.setCurrentUser(null);
        }

        if (!isAppExit) {
            Main.restartApplication(); // Only restart if not exiting
        }
    }

}
