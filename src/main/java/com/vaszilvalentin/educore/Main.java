/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vaszilvalentin.educore;

import com.vaszilvalentin.educore.homework.Homework;
import com.vaszilvalentin.educore.homework.HomeworkManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.utils.HomeworkAutoGrader;
import com.vaszilvalentin.educore.window.WindowManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    private static WindowManager windowManager;

    public static void main(String[] args) {
        startApplication();
        HomeworkAutoGrader.gradeAllOverdueHomework()
                .thenRun(() -> System.out.println("Automatic grading completed"))
                .exceptionally(ex -> {
                    System.err.println("Error during automatic grading: " + ex.getMessage());
                    return null;
                });
        /* User teacher = new User.Builder("Tech Jen", "je", "teacher")
                .age(32)
                .subjects(List.of("Mathematics", "IT"))
                .taughtClasses(List.of("9.A","10.B"))
                .build();
        
        UserManager.addUser(teacher);
         HomeworkManager.addHomework(new Homework(null,"HR assignment",LocalDateTime.now().plusDays(2),"HR","9.B"));
         */

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
