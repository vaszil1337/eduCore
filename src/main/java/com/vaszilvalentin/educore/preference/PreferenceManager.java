package com.vaszilvalentin.educore.preference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class PreferenceManager {
    
    private static final String FILE_NAME = "data/preference.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Theme currentTheme = null; // Cache for the current theme

    private static void ensureFileExists() {
        File file = new File(FILE_NAME);
        
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                saveTheme(Theme.LIGHT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Theme loadTheme() {
        ensureFileExists();
        
        try (Reader reader = new FileReader(FILE_NAME)) {
            String themeString = GSON.fromJson(reader, String.class);
            currentTheme = Theme.fromString(themeString);
            return currentTheme;
        } catch (IOException e) {
            e.printStackTrace();
            currentTheme = Theme.LIGHT;
            return currentTheme;
        }
    }

    public static void saveTheme(Theme theme) {
        if (theme == null) {
            theme = Theme.LIGHT;
        }
        
        try (Writer writer = new FileWriter(FILE_NAME)) {
            GSON.toJson(theme.getDisplayName(), writer);
            currentTheme = theme; // Update the cached theme
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the currently active theme.
     * If not loaded yet, it loads it from the preferences file.
     * 
     * @return the current theme
     */
    public static Theme getCurrentTheme() {
        if (currentTheme == null) {
            return loadTheme();
        }
        return currentTheme;
    }
}