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
import com.vaszilvalentin.schoolmanagementsystemv2.users.UserManager;

import java.util.List;

public class AuthManager {

    // A static list of users (loaded from the database)
    private static List<User> users = UserManager.getAllUsers(); // Static list, no need to instantiate

     // Finds a user by their email.

    public static User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user; // Return the user if found
            }
        }
        return null; // Return null if no user is found with the given email
    }

     // Authenticates a user by checking their email and password.
    public static boolean authenticate(String email, String inputPassword) {
        // Find the user by email
        User user = findUserByEmail(email);

        // If no user is found, authentication fails
        if (user != null && UserManager.verifyPassword(user, inputPassword)) {
            // Set the current logged-in user
            CurrentUser.setCurrentUser(user);
            return true;
        }
        
        return false; // Invalid credentials
    }

    // Logout the user
    public static void logout() {
        CurrentUser.setCurrentUser(null);
    }   
}