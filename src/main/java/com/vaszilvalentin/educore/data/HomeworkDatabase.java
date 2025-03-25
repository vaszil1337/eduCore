/*
 * Handles data storage and retrieval for homework assignments using JSON files.
 * Provides methods to save, load, and generate unique IDs for homework entries.
 */

package com.vaszilvalentin.educore.data;

/**
 * @author vaszilvalentin
 */

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.educore.homeworks.Homework;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeworkDatabase {
    // Gson instance with support for Java time serialization
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    
    // File path where homework assignments are stored
    private static final String HOMEWORK_FILE = "data/homeworks.json";

    /**
     * Ensures that the homework storage file exists.
     * If the file does not exist, it creates the necessary directories and an empty JSON array.
     */
    private static void ensureFileExists() {
        File file = new File(HOMEWORK_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile(); // Create the file
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]"); // Initialize with an empty JSON array
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Saves the list of homework assignments to the JSON file.
     * 
     * @param homeworkList The list of homework assignments to be stored
     */
    public static void saveHomework(List<Homework> homeworkList) {
        ensureFileExists();
        try (FileWriter writer = new FileWriter(HOMEWORK_FILE)) {
            gson.toJson(homeworkList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of homework assignments from the JSON file.
     * If the file cannot be read, it returns an empty list.
     * 
     * @return List of Homework objects
     */
    public static List<Homework> loadHomework() {
        ensureFileExists();
        try (FileReader reader = new FileReader(HOMEWORK_FILE)) {
            Type homeworkListType = new TypeToken<ArrayList<Homework>>() {}.getType();
            return gson.fromJson(reader, homeworkListType);
        } catch (IOException e) {
            return new ArrayList<>(); // Return an empty list in case of an error
        }
    }
    
    /**
     * Generates a unique ID for a homework assignment using UUID.
     * 
     * @return A unique homework ID as a string
     */
    public static String generateHomeworkId() {
        return UUID.randomUUID().toString();
    }
}