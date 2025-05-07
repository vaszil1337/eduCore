/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vaszilvalentin.educore;

import com.vaszilvalentin.educore.utils.ExampleDataGenerator;
import com.vaszilvalentin.educore.utils.HomeworkAutoGrader;
import com.vaszilvalentin.educore.window.WindowManager;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    private static WindowManager windowManager;

    public static void main(String[] args) {
        startApplication();
      //  ExampleDataGenerator.populateSystemWithExampleUsers(160, 20, 1);
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
