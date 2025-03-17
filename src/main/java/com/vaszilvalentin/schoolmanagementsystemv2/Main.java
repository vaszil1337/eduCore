/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.vaszilvalentin.schoolmanagementsystemv2;

import com.vaszilvalentin.schoolmanagementsystemv2.users.User;
import com.vaszilvalentin.schoolmanagementsystemv2.users.UserManager;
import com.vaszilvalentin.schoolmanagementsystemv2.utils.PDFExporter;
import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    public static void main(String[] args) {
        WindowManager windowManager = new WindowManager("Home");
        
        // Create a new user
    //   User user1 = new User("Emillaaa Ilaaa", "emiaal@example.com", 32, "teacher", null, "History", null);

        // Add the user (password will be auto-generated and encrypted)
    //    UserManager.addUser(user1);

        // Print all users
     /*   List<User> allUsers = UserManager.getAllUsers();
        for (User u : allUsers) {
            System.out.println(u);
        } */
     
     
     // Load all students (filter by role if needed)
        List<User> students = UserManager.getUsersByRole("student");

        // Export students' email and plain passwords to a PDF file
        String filePath = "students_report.pdf";
        PDFExporter.exportStudentLoginsToPDF(students, filePath);
        
        List<User> users = UserManager.getAllUsers();
        PDFExporter.exportUsersByRoleToPDF(users, "teacher" , "teachers.pdf");
        
        PDFExporter.exportStudentsByGradeToPDF(students, "10th Grade", "tengrade.pdf");
    }
    
}
