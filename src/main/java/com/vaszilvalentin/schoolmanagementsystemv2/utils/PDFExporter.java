/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.utils;

/**
 *
 * @author vaszilvalentin
 */
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaszilvalentin.schoolmanagementsystemv2.users.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class PDFExporter {

    // Export students' email and plain passwords to a PDF file
    public static void exportStudentLoginsToPDF(List<User> students, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Student Email and Password Report"));
            document.add(new Paragraph("\n"));

            for (User student : students) {
                String email = student.getEmail();
                String plainPassword = EncryptionUtils.decrypt(student.getPassword());
                document.add(new Paragraph("Email: " + email));
                document.add(new Paragraph("Password: " + plainPassword));
                document.add(new Paragraph("\n"));
            }

            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    // Export students by grade to a PDF file
    public static void exportStudentsByGradeToPDF(List<User> students, String grade, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(grade + " students"));
            document.add(new Paragraph("\n"));

            for (User user : students) {
                if (grade.equals(user.getGrade())) {
                    Field[] fields = User.class.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        if (!fieldName.equals("password") && !fieldName.equals("id")) {
                            Object value = field.get(user);
                            if (value != null) {
                                document.add(new Paragraph(fieldName + ": " + value));
                            }
                        }
                    }
                    document.add(new Paragraph("\n"));
                }
            }

            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException | IllegalAccessException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    // Export users by role with all their data except password, id, and null values
    public static void exportUsersByRoleToPDF(List<User> users, String role, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("User Report for Role: " + role));
            document.add(new Paragraph("\n"));

            for (User user : users) {
                if (role.equals(user.getRole())) {
                    Field[] fields = User.class.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        if (!fieldName.equals("password") && !fieldName.equals("id")) {
                            Object value = field.get(user);
                            if (value != null) {
                                document.add(new Paragraph(fieldName + ": " + value));
                            }
                        }
                    }
                    document.add(new Paragraph("\n"));
                }
            }

            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException | IllegalAccessException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}
