/*
 * Manages user-related operations such as adding, updating, deleting users, verifying passwords, and handling user data.
 * This class communicates with the UserDatabase to persist data and provides utility methods for managing user accounts.
 */
package com.vaszilvalentin.educore.users;

import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.educore.data.UserDatabase;
import com.vaszilvalentin.educore.utils.EncryptionUtils;
import com.vaszilvalentin.educore.utils.PasswordUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserManager {

    // Define the type for a list of users for Gson deserialization
    private static final Type USER_LIST_TYPE = new TypeToken<ArrayList<User>>() {
    }.getType();

    // Generates the next ID for a new user based on the highest current ID
    private static String generateNextId() {
        List<User> users = UserDatabase.loadUsers();
        int maxId = 0;

        // Find the maximum ID in the current list
        for (User user : users) {
            int userId = Integer.parseInt(user.getId());
            if (userId > maxId) {
                maxId = userId;
            }
        }

        // Increment the maximum ID by 1 for the new user
        return String.valueOf(maxId + 1);
    }

    // Generates a random password and encrypts it
    private static String generatePassword() {
        return EncryptionUtils.encrypt(PasswordUtils.generatePassword(12));
    }

    // Verifies if the provided password matches the stored password for a user
    public static boolean verifyPassword(User user, String inputPassword) {
        String decryptedPassword = EncryptionUtils.decrypt(user.getPassword());
        return decryptedPassword.equals(inputPassword);
    }

    // Updates the password of a user after verifying the old password
    public static boolean updateUserPassword(String userId, String oldPassword, String newPassword) {
        List<User> users = UserDatabase.loadUsers();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                // Decrypt the stored password
                String decryptedPassword = EncryptionUtils.decrypt(user.getPassword());

                // Verify the old password before updating
                if (decryptedPassword.equals(oldPassword)) {
                    // Encrypt the new password and update the user
                    user.setPassword(EncryptionUtils.encrypt(newPassword));

                    // Use the existing updateUser method to save changes
                    updateUser(userId, user);
                    return true; // Password update successful
                } else {
                    return false; // Old password is incorrect
                }
            }
        }
        return false; // User not found
    }

    // Checks if a user with the given ID or email already exists
    public static boolean userExists(String id, String email) {
        List<User> users = UserDatabase.loadUsers();
        for (User user : users) {
            if (user.getId().equals(id) || user.getEmail().equals(email)) {
                return true; // User exists
            }
        }
        return false; // User does not exist
    }

    // Adds a new user, generates an ID and a password, then saves the user
    public static void addUser(User user) {
        List<User> users = UserDatabase.loadUsers();
        user.setId(generateNextId());
        user.setPassword(generatePassword());
        users.add(user);
        UserDatabase.saveUsers(users);
    }

    // Retrieves all users from the database
    public static List<User> getAllUsers() {
        return UserDatabase.loadUsers();
    }

    // Retrieves users based on their role
    public static List<User> getUsersByRole(String role) {
        List<User> users = UserDatabase.loadUsers();
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals(role)) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    public static List<User> getStudentsByClass(String classId) {
        return getUsersByRole("student").stream()
                .filter(student -> student.getClassId().equals(classId))
                .collect(Collectors.toList());
    }

    // Updates a user based on their ID with new user information
    public static void updateUser(String id, User updatedUser) {
        List<User> users = UserDatabase.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.set(i, updatedUser);
                break;
            }
        }
        UserDatabase.saveUsers(users);
    }

    // Deletes a user by their ID
    public static void deleteUser(String id) {
        List<User> users = UserDatabase.loadUsers();
        users.removeIf(user -> user.getId().equals(id));
        UserDatabase.saveUsers(users);
    }
}
