/*
 * Utility class for exporting user data to PDF.
 */
package com.vaszilvalentin.educore.utils;

/**
 * @author vaszilvalentin
 */
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.vaszilvalentin.educore.users.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDFExporter {

    /**
     * Exports a list of students' email addresses and decrypted passwords to a PDF file.
     *
     * @param students The list of students
     * @param filePath The file path for the exported PDF
     */
    public static void exportStudentLoginsToPDF(List<User> students, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Student Login Credentials"));
            document.add(new Paragraph("\n"));

            // Create a table with two columns (Email and Password)
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell("Email");
            table.addCell("Password");

            for (User student : students) {
                String email = student.getEmail();
                String plainPassword = EncryptionUtils.decrypt(student.getPassword()); // Decrypt stored password
                table.addCell(email);
                table.addCell(plainPassword);
            }

            document.add(table);
            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Exports students of a specific grade to a PDF file.
     *
     * @param students The list of students
     * @param grade    The grade to filter by
     * @param filePath The file path for the exported PDF
     */
    public static void exportStudentsByGradeToPDF(List<User> students, String grade, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Students in Grade " + grade));
            document.add(new Paragraph("\n"));

            // Create a table with three columns (Name, Email, Grade)
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Name");
            table.addCell("Email");
            table.addCell("Grade");

            for (User user : students) {
                if (grade.equals(user.getGrade())) {
                    table.addCell(user.getName());
                    table.addCell(user.getEmail());
                    table.addCell(user.getGrade());
                }
            }

            document.add(table);
            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    /**
     * Exports users of a specific role (Student, Teacher, Admin) to a PDF file.
     * Teachers have additional columns for subject and taught classes.
     *
     * @param users    The list of users
     * @param role     The role to filter by (student, teacher, or admin)
     * @param filePath The file path for the exported PDF
     */
    public static void exportUsersByRoleToPDF(List<User> users, String role, String filePath) {
        // Define table column count based on role and map role names to readable format
        Map<String, String> roleMap = Map.of(
                "student", "Student",
                "teacher", "Teacher",
                "admin", "Administrator"
        );
        String translatedRole = roleMap.getOrDefault(role, "Unknown Role");
        boolean isTeacher = "teacher".equals(role);
        int tableSize = isTeacher ? 4 : 2; // Teachers have extra columns

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(translatedRole + " Users"));
            document.add(new Paragraph("\n"));

            // Create table headers with bold font
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            PdfPTable table = new PdfPTable(tableSize);
            table.setWidthPercentage(100);

            // Add common columns (Name, Email)
            PdfPCell nameHeader = new PdfPCell(new Phrase("Name", headerFont));
            PdfPCell emailHeader = new PdfPCell(new Phrase("Email", headerFont));
            table.addCell(nameHeader);
            table.addCell(emailHeader);

            // Add teacher-specific columns (Subject, Taught Classes)
            if (isTeacher) {
                PdfPCell subjectHeader = new PdfPCell(new Phrase("Subject", headerFont));
                PdfPCell classesHeader = new PdfPCell(new Phrase("Taught Classes", headerFont));
                table.addCell(subjectHeader);
                table.addCell(classesHeader);
            }

            // Populate the table with user data
            for (User user : users) {
                if (role.equals(user.getRole())) {
                    table.addCell(user.getName());
                    table.addCell(user.getEmail());

                    if (isTeacher) {
                        table.addCell(user.getSubject() != null ? user.getSubject() : "-");

                        // Ensure that taughtClasses is not null
                        List<String> taughtClasses = user.getTaughtClasses() != null ? user.getTaughtClasses() : List.of();
                        table.addCell(taughtClasses.isEmpty() ? "-" : String.join(", ", taughtClasses));
                    }
                }
            }

            document.add(table);
            System.out.println("PDF exported successfully to: " + filePath);
        } catch (DocumentException | IOException e) {
            System.err.println("Error exporting PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}