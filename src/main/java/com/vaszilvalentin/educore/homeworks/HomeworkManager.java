package com.vaszilvalentin.educore.homeworks;

import com.vaszilvalentin.educore.data.HomeworkDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages homework assignments: creation, retrieval, updating, deletion,
 * class/subject filtering, and handling student submissions and grades.
 */
public class HomeworkManager {

    /**
     * Adds a new homework assignment to the database.
     * Automatically assigns a unique ID.
     *
     * @param homework Homework object to add
     */
    public static void addHomework(Homework homework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        homework.setId(HomeworkDatabase.generateHomeworkId());
        homeworkList.add(homework);
        HomeworkDatabase.saveHomework(homeworkList);
    }

    /**
     * Returns all homework assignments from the database.
     *
     * @return List of all homework
     */
    public static List<Homework> getAllHomework() {
        return HomeworkDatabase.loadHomework();
    }

    /**
     * Retrieves all homework for a specific class.
     *
     * @param classId The class ID (e.g., "10B")
     * @return Filtered list of homework
     */
    public static List<Homework> getHomeworkByClass(String classId) {
        List<Homework> filteredHomework = new ArrayList<>();

        for (Homework homework : HomeworkDatabase.loadHomework()) {
            if (homework.getClassId().equalsIgnoreCase(classId)) {
                filteredHomework.add(homework);
            }
        }

        return filteredHomework;
    }

    /**
     * Retrieves all homework for a specific subject.
     *
     * @param subject The subject name (e.g., "Math")
     * @return Filtered list of homework
     */
    public static List<Homework> getHomeworkBySubject(String subject) {
        List<Homework> filteredHomework = new ArrayList<>();

        for (Homework homework : HomeworkDatabase.loadHomework()) {
            if (homework.getSubject().equalsIgnoreCase(subject)) {
                filteredHomework.add(homework);
            }
        }

        return filteredHomework;
    }

    /**
     * Updates a specific homework assignment by ID.
     *
     * @param id              The homework ID to update
     * @param updatedHomework The updated homework object
     */
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

    /**
     * Deletes a homework assignment by its ID.
     *
     * @param id The homework ID to delete
     */
    public static void deleteHomework(String id) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();
        homeworkList.removeIf(homework -> homework.getId().equals(id));
        HomeworkDatabase.saveHomework(homeworkList);
    }

    /**
     * Adds or replaces a submission for a student.
     * Grade is initially set to "-" (ungraded).
     *
     * @param homeworkId Homework ID
     * @param studentId  Student ID
     * @param filePath   Submitted file path
     */
    public static void addSubmission(String homeworkId, String studentId, String filePath) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                Homework.Submission submission = new Homework.Submission(filePath, "-");
                homework.addSubmission(studentId, submission);
                break;
            }
        }

        HomeworkDatabase.saveHomework(homeworkList);
    }

    /**
     * Removes a student's submission from a homework.
     *
     * @param homeworkId Homework ID
     * @param studentId  Student ID
     */
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

    /**
     * Sets a grade for a student's submission.
     *
     * @param homeworkId Homework ID
     * @param studentId  Student ID
     * @param grade      Grade to assign (e.g., "5")
     */
    public static void setSubmissionGrade(String homeworkId, String studentId, String grade) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                Homework.Submission submission = homework.getSubmission(studentId);
                if (submission != null) {
                    submission.setGrade(grade);
                }
                break;
            }
        }

        HomeworkDatabase.saveHomework(homeworkList);
    }

    /**
     * Retrieves the grade for a student's submission.
     *
     * @param homeworkId Homework ID
     * @param studentId  Student ID
     * @return Grade as string, or "-" if ungraded
     */
    public static String getSubmissionGrade(String homeworkId, String studentId) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                Homework.Submission submission = homework.getSubmission(studentId);
                if (submission != null) {
                    return submission.getGrade();
                }
            }
        }

        return "-";
    }
}
