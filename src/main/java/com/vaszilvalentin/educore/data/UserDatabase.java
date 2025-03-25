/*
 * Manages user data storage and retrieval using a JSON file.
 * Provides methods to save and load users.
 */

package com.vaszilvalentin.educore.data;

/**
 * @author vaszilvalentin
 */

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.educore.users.User;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    // Gson instance with support for Java time serialization and pretty printing
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    
    // File path where user data is stored
    private static final String USERS_FILE = "data/users.json";

    /**
     * Ensures that the user storage file exists.
     * If the file does not exist, it creates the necessary directories and an empty JSON array.
     */
    private static void ensureFileExists() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile(); // Create the file
                
                // Initialize the file with an empty JSON array
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the list of users to the JSON file.
     * 
     * @param users The list of users to be stored
     */
    public static void saveUsers(List<User> users) {
        ensureFileExists(); // Ensure the file exists before writing
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer); // Write the entire list with pretty printing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of users from the JSON file.
     * If the file cannot be read, it returns an empty list.
     * 
     * @return List of User objects
     */
    public static List<User> loadUsers() {
        ensureFileExists(); // Ensure the file exists before reading
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            return new ArrayList<>(); // Return an empty list if there's an error
        }
    }
}