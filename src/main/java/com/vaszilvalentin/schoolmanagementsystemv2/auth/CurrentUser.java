/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.auth;

/**
 *
 * @author vaszilvalentin
 */

import com.vaszilvalentin.schoolmanagementsystemv2.users.User;

public class CurrentUser {

    private static User currentUser;

    // Private constructor to prevent instantiation
    private CurrentUser() {}

    // Set the current user
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Get the current user
    public static User getCurrentUser() {
        return currentUser;
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}

