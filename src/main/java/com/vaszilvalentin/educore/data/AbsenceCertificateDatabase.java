/*
 * Handles data storage and retrieval for absence certificates using JSON files.
 * Provides methods to save, load, and generate unique IDs for certificates.
 */

package com.vaszilvalentin.educore.data;

/**
 * @author vaszilvalentin
 */

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaszilvalentin.educore.certificates.AbsenceCertificate;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbsenceCertificateDatabase {
    // Gson instance with support for Java time serialization
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    
    // File path where certificates are stored
    private static final String CERTIFICATES_FILE = "data/certificates.json";

    /**
     * Ensures that the certificate storage file exists.
     * If the file does not exist, it creates the necessary directories and an empty JSON array.
     */
    private static void ensureFileExists() {
        File file = new File(CERTIFICATES_FILE);
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
     * Saves the list of absence certificates to the JSON file.
     * 
     * @param certificates The list of absence certificates to be stored
     */
    public static void saveCertificates(List<AbsenceCertificate> certificates) {
        ensureFileExists();
        try (FileWriter writer = new FileWriter(CERTIFICATES_FILE)) {
            gson.toJson(certificates, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the list of absence certificates from the JSON file.
     * If the file cannot be read, it returns an empty list.
     * 
     * @return List of AbsenceCertificate objects
     */
    public static List<AbsenceCertificate> loadCertificates() {
        ensureFileExists();
        try (FileReader reader = new FileReader(CERTIFICATES_FILE)) {
            Type certificateListType = new TypeToken<ArrayList<AbsenceCertificate>>() {}.getType();
            return gson.fromJson(reader, certificateListType);
        } catch (IOException e) {
            return new ArrayList<>(); // Return an empty list in case of an error
        }
    }

    /**
     * Generates a unique ID for an absence certificate using UUID.
     * 
     * @return A unique certificate ID as a string
     */
    public static String generateCertificateId() {
        return UUID.randomUUID().toString();
    }
}