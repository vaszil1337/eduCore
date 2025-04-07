/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.vaszilvalentin.educore;

import com.vaszilvalentin.educore.homeworks.Homework;
import com.vaszilvalentin.educore.homeworks.HomeworkManager;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;
import com.vaszilvalentin.educore.window.WindowManager;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author vaszilvalentin
 */
public class Main {

    public static void main(String[] args) {
        WindowManager windowManager = new WindowManager("Landing");
       // UserManager.addUser(new User("Kis Jeno", "jenoke@gmail.com", 15, "student", "9.A", null, null, null));
       //  HomeworkManager.addHomework(new Homework(null,"IT assignment",LocalDateTime.now().plusDays(2),"Informatics","11.B"));
    }
}
