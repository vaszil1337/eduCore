package com.vaszilvalentin.educore.utils;

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

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Create header row
            addHeaderCell(table, "Email");
            addHeaderCell(table, "Password");

            for (User student : students) {
                String email = student.getEmail();
                String plainPassword = EncryptionUtils.decrypt(student.getPassword());

                table.addCell(makeCenteredCell(email));
                table.addCell(makeCenteredCell(plainPassword));
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
     * Exports a list of teachers' email addresses and decrypted passwords to a PDF file.
     *
     * @param teachers The list of teachers
     * @param filePath The file path for the exported PDF
     */
    public static void exportTeacherLoginsToPDF(List<User> teachers, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Teacher Login Credentials"));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // Create header row
            addHeaderCell(table, "Email");
            addHeaderCell(table, "Password");

            for (User teacher : teachers) {
                String email = teacher.getEmail();
                String plainPassword = EncryptionUtils.decrypt(teacher.getPassword());

                table.addCell(makeCenteredCell(email));
                table.addCell(makeCenteredCell(plainPassword));
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
     * Exports students of a specific class to a PDF file.
     *
     * @param students The list of students
     * @param classId  The classId to filter by
     * @param filePath The file path for the exported PDF
     */
    public static void exportStudentsByClassToPDF(List<User> students, String classId, String filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Students in Grade " + classId));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            // Create header row
            addHeaderCell(table, "#");
            addHeaderCell(table, "Name");
            addHeaderCell(table, "Email");

            // Loop through the students and add their data to the table
            for (User student : students) {
                if (classId.equals(student.getClassId())) {
                    table.addCell(makeCenteredCell(String.valueOf(students.indexOf(student) + 1)));
                    table.addCell(makeCenteredCell(student.getName()));
                    table.addCell(makeCenteredCell(student.getEmail()));
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
     * Teachers have additional columns for subjects and taught classes.
     *
     * @param users    The list of users
     * @param role     The role to filter by (student, teacher, or admin)
     * @param filePath The file path for the exported PDF
     */
    public static void exportUsersByRoleToPDF(List<User> users, String role, String filePath) {
        Map<String, String> roleMap = Map.of(
                "student", "Student",
                "teacher", "Teacher",
                "admin", "Administrator"
        );
        String translatedRole = roleMap.getOrDefault(role, "Unknown Role");
        boolean isTeacher = "teacher".equals(role);
        int tableSize = isTeacher ? 4 : 2;

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(translatedRole + " Users"));
            document.add(new Paragraph("\n"));

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            PdfPTable table = new PdfPTable(tableSize);
            table.setWidthPercentage(100);

            // Header
            addHeaderCell(table, "Name", headerFont);
            addHeaderCell(table, "Email", headerFont);

            if (isTeacher) {
                addHeaderCell(table, "Subjects", headerFont);
                addHeaderCell(table, "Taught Classes", headerFont);
            }

            for (User user : users) {
                if (role.equals(user.getRole())) {
                    table.addCell(makeCenteredCell(user.getName()));
                    table.addCell(makeCenteredCell(user.getEmail()));

                    if (isTeacher) {
                        List<String> subjects = user.getSubjects() != null ? user.getSubjects() : List.of();
                        table.addCell(makeCenteredCell(subjects.isEmpty() ? "-" : String.join(", ", subjects)));

                        List<String> taughtClasses = user.getTaughtClasses() != null ? user.getTaughtClasses() : List.of();
                        table.addCell(makeCenteredCell(taughtClasses.isEmpty() ? "-" : String.join(", ", taughtClasses)));
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

    /**
     * Helper method to create a header cell with specific styling.
     */
    private static void addHeaderCell(PdfPTable table, String content) {
        addHeaderCell(table, content, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE));
    }

    /**
     * Helper method to create a header cell with specific styling.
     */
    private static void addHeaderCell(PdfPTable table, String content, Font headerFont) {
        PdfPCell cell = new PdfPCell(new Phrase(content, headerFont));
        cell.setBackgroundColor(BaseColor.DARK_GRAY); // Set header background color
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center align the header text
        table.addCell(cell);
    }

    /**
     * Helper method to create a centered data cell.
     */
    private static PdfPCell makeCenteredCell(String content) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center align the data text
        return cell;
    }
}
