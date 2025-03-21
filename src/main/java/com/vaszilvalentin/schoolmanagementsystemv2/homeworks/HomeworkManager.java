/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.homeworks;

/**
 *
 * @author vaszilvalentin
 */
import com.vaszilvalentin.schoolmanagementsystemv2.data.HomeworkDatabase;
import java.util.ArrayList;
import java.util.List;

public class HomeworkManager {

    // Add a new homework assignment
    public static void addHomework(Homework homework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        homework.setId(HomeworkDatabase.generateHomeworkId());
        homeworkList.add(homework);
        HomeworkDatabase.saveHomework(homeworkList);
    }

    // Get all homework assignments
    public static List<Homework> getAllHomework() {
        return HomeworkDatabase.loadHomework();
    }

    // Get homework assignments for a specific grade
    public static List<Homework> getHomeworkByGrade(String grade) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        List<Homework> filteredHomework = new ArrayList<>();
        for (Homework homework : homeworkList) {
            if (homework.getGrade().equalsIgnoreCase(grade)) {
                filteredHomework.add(homework);
            }
        }
        return filteredHomework;
    }

    // Update a homework assignment
    public static void updateHomework(String id, Homework updatedHomework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        for (int i = 0; i < homeworkList.size(); i++) {
            if (homeworkList.get(i).getId().equals(id)) {
                homeworkList.set(i, updatedHomework);
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList);
    }

    // Delete a homework assignment
    public static void deleteHomework(String id) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        homeworkList.removeIf(homework -> homework.getId().equals(id));
        HomeworkDatabase.saveHomework(homeworkList);
    }

    // Add a submission for a student
    public static void addSubmission(String homeworkId, String studentId, String filePath) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                homework.addSubmission(studentId, filePath);
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList);
    }

    // Remove a submission for a student
    public static void removeSubmission(String homeworkId, String studentId) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                homework.removeSubmission(studentId);
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList);
    }
}
