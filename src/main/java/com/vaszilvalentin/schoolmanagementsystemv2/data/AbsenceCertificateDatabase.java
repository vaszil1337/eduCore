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
import com.vaszilvalentin.schoolmanagementsystemv2.certificates.AbsenceCertificate;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbsenceCertificateDatabase {
    private static final Gson gson = Converters.registerAll(new GsonBuilder()).setPrettyPrinting().create();
    private static final String CERTIFICATES_FILE = "data/certificates.json";

    private static void ensureFileExists() {
        File file = new File(CERTIFICATES_FILE);
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

    public static void saveCertificates(List<AbsenceCertificate> certificates) {
        ensureFileExists();
        try (FileWriter writer = new FileWriter(CERTIFICATES_FILE)) {
            gson.toJson(certificates, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<AbsenceCertificate> loadCertificates() {
        ensureFileExists();
        try (FileReader reader = new FileReader(CERTIFICATES_FILE)) {
            Type certificateListType = new TypeToken<ArrayList<AbsenceCertificate>>() {}.getType();
            return gson.fromJson(reader, certificateListType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static String generateCertificateId() {
        return UUID.randomUUID().toString();
    }
}

