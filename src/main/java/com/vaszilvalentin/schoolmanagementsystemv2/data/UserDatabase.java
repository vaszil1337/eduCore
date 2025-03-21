/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.data;

/**
 *
 * @author vaszilvalentin
 */
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.schoolmanagementsystemv2.users.User;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {

    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    private static final String USERS_FILE = "src/main/java/com/vaszilvalentin/schoolmanagementsystemv2/data/users.json";

    // Ensure the file exists and is initialized with an empty JSON array
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

    // Save all users to the JSON file with pretty printing
    public static void saveUsers(List<User> users) {
        ensureFileExists(); // Ensure the file exists before writing
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer); // Write the entire list with pretty printing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all users from the JSON file
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
