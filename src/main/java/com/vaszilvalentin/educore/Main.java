/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vaszilvalentin.educore;

import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.utils.PDFExporter;
import com.vaszilvalentin.educore.window.WindowManager;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    public static void main(String[] args) {
        WindowManager windowManager = new WindowManager("Landing");

        List<User> users = UserManager.getAllUsers();
        PDFExporter.exportUsersByRoleToPDF(users, "teacher", "teachers.pdf");
        PDFExporter.exportUsersByRoleToPDF(users, "student", "students.pdf");
        
    }
}
