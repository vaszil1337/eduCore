/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vaszilvalentin.educore;

import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.utils.ExampleDataGenerator;
import com.vaszilvalentin.educore.utils.HomeworkAutoGrader;
import com.vaszilvalentin.educore.utils.PDFExporter;
import com.vaszilvalentin.educore.window.WindowManager;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    private static WindowManager windowManager;

    public static void main(String[] args) {
        startApplication();
        /*      
        
        Test data
        
        ExampleDataGenerator.populateSystemWithExampleUsers(160, 20, 1);
        List<User> students = UserManager.getUsersByRole("student");
        List<User> teachers = UserManager.getUsersByRole("teacher");
        PDFExporter.exportStudentLoginsToPDF(students, "stud.pdf");
        PDFExporter.exportTeacherLoginsToPDF(teachers, "teach.pdf");
        
         */
        
        HomeworkAutoGrader.gradeAllOverdueHomework()
                .thenRun(() -> System.out.println("Automatic grading completed"))
                .exceptionally(ex -> {
                    System.err.println("Error during automatic grading: " + ex.getMessage());
                    return null;
                });
    }

    public static void restartApplication() {
        if (windowManager != null) {
            windowManager.closeWindow();
        }
        startApplication();
    }

    private static void startApplication() {
        windowManager = new WindowManager("Landing");
    }
}
