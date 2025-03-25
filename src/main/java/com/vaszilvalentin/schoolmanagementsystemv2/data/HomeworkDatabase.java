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
import com.vaszilvalentin.schoolmanagementsystemv2.homeworks.Homework;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeworkDatabase {
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    private static final String HOMEWORK_FILE = "data/homeworks.json";

    private static void ensureFileExists() {
        File file = new File(HOMEWORK_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Save all homework assignments to the JSON file
    public static void saveHomework(List<Homework> homeworkList) {
        ensureFileExists();
        try (FileWriter writer = new FileWriter(HOMEWORK_FILE)) {
            gson.toJson(homeworkList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all homework assignments from the JSON file
    public static List<Homework> loadHomework() {
        ensureFileExists();
        try (FileReader reader = new FileReader(HOMEWORK_FILE)) {
            Type homeworkListType = new TypeToken<ArrayList<Homework>>() {}.getType();
            return gson.fromJson(reader, homeworkListType);
        } catch (IOException e) {
            return new ArrayList<>(); // Return an empty list if the file doesn't exist
        }
    }
    
    public static String generateHomeworkId() {
        return UUID.randomUUID().toString();
    }
}
