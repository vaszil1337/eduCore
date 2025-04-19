package com.vaszilvalentin.educore.data;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.educore.log.LoginLog;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles storage and retrieval of login logs in a JSON file.
 */
public class LoginLogDatabase {
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    private static final String LOG_FILE = "data/login_logs.json";

    private static void ensureFileExists() {
        File file = new File(LOG_FILE);
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

    public static void saveLogs(List<LoginLog> logs) {
        ensureFileExists();
        try (FileWriter writer = new FileWriter(LOG_FILE)) {
            gson.toJson(logs, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LoginLog> loadLogs() {
        ensureFileExists();
        try (FileReader reader = new FileReader(LOG_FILE)) {
            Type logListType = new TypeToken<ArrayList<LoginLog>>() {}.getType();
            return gson.fromJson(reader, logListType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static String generateLogId() {
        return UUID.randomUUID().toString();
    }
}
