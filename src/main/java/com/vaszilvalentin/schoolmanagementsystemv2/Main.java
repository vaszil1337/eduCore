/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.vaszilvalentin.schoolmanagementsystemv2;

import com.vaszilvalentin.schoolmanagementsystemv2.users.User;
import com.vaszilvalentin.schoolmanagementsystemv2.users.UserManager;
import com.vaszilvalentin.schoolmanagementsystemv2.window.WindowManager;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    public static void main(String[] args) {
        WindowManager windowManager = new WindowManager("Home");
        
        List<User> students = UserManager.getUsersByRole("student");
        List<User> admins = UserManager.getUsersByRole("admin");
        
        System.out.println("Students:");
        System.out.println(students.toString()+"\n");
        System.out.println("Admins");
        System.out.println(admins.toString());
    }
    
}
