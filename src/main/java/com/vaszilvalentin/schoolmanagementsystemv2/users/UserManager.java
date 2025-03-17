/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.users;

/**
 *
 * @author vaszilvalentin
 */
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.schoolmanagementsystemv2.data.Database;
import com.vaszilvalentin.schoolmanagementsystemv2.utils.EncryptionUtils;
import com.vaszilvalentin.schoolmanagementsystemv2.utils.PasswordUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final Type USER_LIST_TYPE = new TypeToken<ArrayList<User>>() {}.getType();
    
    private static String generateNextId() {
        List<User> users = Database.loadUsers();
        int maxId = 0;

        // Find the maximum ID in the current list
        for (User user : users) {
            int userId = Integer.parseInt(user.getId());
            if (userId > maxId) {
                maxId = userId;
            }
        }

        // Increment the maximum ID by 1
        return String.valueOf(maxId + 1);
    }
    
    private static String generatePassword() {
        String plainPassword = PasswordUtils.generatePassword(12); // 12-character password
        System.out.println("Generated password: " + plainPassword);

        // Encrypt the password
        String encryptedPassword = EncryptionUtils.encrypt(plainPassword);
        return encryptedPassword;
    }
    
    // Verify a user's password
    public static boolean verifyPassword(User user, String inputPassword) {
        String decryptedPassword = EncryptionUtils.decrypt(user.getPassword());
        return decryptedPassword.equals(inputPassword);
    }

    // Check if a user with the given ID already exists
    public static boolean userExists(String id, String email) {
    List<User> users = Database.loadUsers();
    for (User user : users) {
        if (user.getId().equals(id) || user.getName().equalsIgnoreCase(email)) {
            return true; // User with the same ID or name exists
        }
    }
    return false; // User does not exist
}

    // Add a new user (with duplicate check)
    public static void addUser(User user) {
        List<User> users = Database.loadUsers();
        user.setId(generateNextId());
        user.setPassword(generatePassword());
        users.add(user);
        Database.saveUsers(users);
    }

    // Get all users
    public static List<User> getAllUsers() {
        return Database.loadUsers();
    }

    // Get users by role
    public static List<User> getUsersByRole(String role) {
        List<User> users = Database.loadUsers();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals(role)) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    // Update a user
    public static void updateUser(String id, User updatedUser) {
        List<User> users = Database.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.set(i, updatedUser);
                break;
            }
        }
        Database.saveUsers(users);
    }

    // Delete a user
    public static void deleteUser(String id) {
        List<User> users = Database.loadUsers();
        users.removeIf(user -> user.getId().equals(id));
        Database.saveUsers(users);
    }
}
