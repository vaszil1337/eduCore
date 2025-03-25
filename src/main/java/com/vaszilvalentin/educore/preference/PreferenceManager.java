/*
 * Manages loading and saving the user's theme preference to a JSON file.
 * Provides methods to ensure the file exists, load the saved theme, and save a new theme.
 */

package com.vaszilvalentin.educore.preference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class PreferenceManager {
    
    // The file path where the theme preference is stored
    private static final String FILE_NAME = "data/preference.json";
    
    // Gson object for serializing and deserializing the theme preference to and from JSON
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Ensures that the preference file exists. If not, creates it and sets a default theme.
     */
    private static void ensureFileExists() {
        File file = new File(FILE_NAME);
        
        // Check if the file exists
        if (!file.exists()) {
            try {
                // Create parent directories and the file if it doesn't exist
                file.getParentFile().mkdirs();
                file.createNewFile();
                
                // Set default theme (LIGHT) if the file is newly created
                saveTheme(Theme.LIGHT);
            } catch (IOException e) {
                e.printStackTrace(); // Handle any errors that occur during file creation
            }
        }
    }

    /**
     * Loads the theme preference from the JSON file.
     * If the file does not exist or an error occurs, it returns the default theme (LIGHT).
     * 
     * @return The user's saved theme preference, or the default theme if an error occurs.
     */
    public static Theme loadTheme() {
        ensureFileExists(); // Ensure the file exists before trying to read it
        
        try (Reader reader = new FileReader(FILE_NAME)) {
            // Read the theme string from the file
            String themeString = GSON.fromJson(reader, String.class);
            // Convert the string into a Theme enum value and return it
            return Theme.fromString(themeString);
        } catch (IOException e) {
            e.printStackTrace(); // Handle any I/O errors that occur
            return Theme.LIGHT;  // Return the default theme if an error occurs
        }
    }

    /**
     * Saves the selected theme to the JSON file.
     * 
     * @param theme The theme to be saved to the file.
     */
    public static void saveTheme(Theme theme) {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            // Write the theme's display name (String) to the file as JSON
            GSON.toJson(theme.getDisplayName(), writer);
        } catch (IOException e) {
            e.printStackTrace(); // Handle any I/O errors that occur
        }
    }
}