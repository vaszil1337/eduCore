/*
 * Manages the currently authenticated user session.
 * Provides methods to set, retrieve, and check the authentication state of a user.
 */
package com.vaszilvalentin.educore.auth;

/**
 * @author vaszilvalentin
 */

import com.vaszilvalentin.educore.users.User;

public class CurrentUser {

    // Holds the currently logged-in user instance
    private static User currentUser;

    /**
     * Private constructor to prevent instantiation.
     * This class is designed to be used statically.
     */
    private CurrentUser() {}

    /**
     * Sets the currently logged-in user.
     *
     * @param user The authenticated user to be set as the current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return The current User object, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}