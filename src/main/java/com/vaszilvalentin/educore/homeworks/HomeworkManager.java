/*
 * Manages homework assignments, including adding, updating, deleting, and handling submissions.
 */

package com.vaszilvalentin.educore.homeworks;

import com.vaszilvalentin.educore.data.HomeworkDatabase;
import java.util.ArrayList;
import java.util.List;

public class HomeworkManager {

    /**
     * Adds a new homework assignment to the database.
     * 
     * @param homework The homework object to be added
     */
    public static void addHomework(Homework homework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load existing homework
        homework.setId(HomeworkDatabase.generateHomeworkId()); // Generate a unique ID for the new homework
        homeworkList.add(homework); // Add the new homework to the list
        HomeworkDatabase.saveHomework(homeworkList); // Save the updated list to the database
    }

    /**
     * Retrieves all homework assignments from the database.
     * 
     * @return A list of all homework assignments
     */
    public static List<Homework> getAllHomework() {
        return HomeworkDatabase.loadHomework(); // Load and return all homework from the database
    }

    /**
     * Retrieves all homework assignments for a specific grade.
     * 
     * @param grade The grade filter for the homework assignments
     * @return A list of homework assignments for the specified grade
     */
    public static List<Homework> getHomeworkByGrade(String grade) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load all homework assignments
        List<Homework> filteredHomework = new ArrayList<>(); // List to store filtered homework assignments

        // Filter homework by grade
        for (Homework homework : homeworkList) {
            if (homework.getGrade().equalsIgnoreCase(grade)) {
                filteredHomework.add(homework);
            }
        }
        return filteredHomework; // Return filtered homework assignments
    }

    /**
     * Updates an existing homework assignment with new details.
     * 
     * @param id The ID of the homework to be updated
     * @param updatedHomework The new homework object containing updated details
     */
    public static void updateHomework(String id, Homework updatedHomework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load existing homework assignments

        // Find and update the homework with the matching ID
        for (int i = 0; i < homeworkList.size(); i++) {
            if (homeworkList.get(i).getId().equals(id)) {
                homeworkList.set(i, updatedHomework); // Replace the old homework with the updated one
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList); // Save the updated homework list to the database
    }

    /**
     * Deletes a homework assignment by its ID.
     * 
     * @param id The ID of the homework to be deleted
     */
    public static void deleteHomework(String id) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load existing homework assignments

        // Remove the homework with the matching ID
        homeworkList.removeIf(homework -> homework.getId().equals(id));

        HomeworkDatabase.saveHomework(homeworkList); // Save the updated homework list after deletion
    }

    /**
     * Adds a submission for a specific student to a homework assignment.
     * 
     * @param homeworkId The ID of the homework
     * @param studentId The ID of the student submitting the homework
     * @param filePath The file path where the submission is stored
     */
    public static void addSubmission(String homeworkId, String studentId, String filePath) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load existing homework assignments

        // Find the homework by its ID and add the student's submission
        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                homework.addSubmission(studentId, filePath);
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList); // Save the updated homework list
    }

    /**
     * Removes a student's submission from a specific homework assignment.
     * 
     * @param homeworkId The ID of the homework
     * @param studentId The ID of the student whose submission is to be removed
     */
    public static void removeSubmission(String homeworkId, String studentId) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework(); // Load existing homework assignments

        // Find the homework by its ID and remove the student's submission
        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                homework.removeSubmission(studentId);
                break;
            }
        }
        HomeworkDatabase.saveHomework(homeworkList); // Save the updated homework list after removal
    }
}