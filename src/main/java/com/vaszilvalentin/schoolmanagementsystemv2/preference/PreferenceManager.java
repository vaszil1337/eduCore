/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.preference;

/**
 *
 * @author vaszilvalentin
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class PreferenceManager {
    private static final String FILE_NAME = "data/preference.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static void ensureFileExists() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                saveTheme(Theme.LIGHT); // Create a default file if it doesn't exist
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Loads the theme from the JSON file
    public static Theme loadTheme() {
        ensureFileExists();
        try {
            Reader reader = new FileReader(FILE_NAME);
            String themeString = GSON.fromJson(reader, String.class);
            return Theme.fromString(themeString);
        } catch (IOException e) {
            e.printStackTrace();
            return Theme.LIGHT;  // Return default value in case of an error
        }
    }

    // Saves the selected theme to the JSON file
    public static void saveTheme(Theme theme) {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            GSON.toJson(theme.getDisplayName(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
