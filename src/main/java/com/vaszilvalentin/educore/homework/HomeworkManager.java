package com.vaszilvalentin.educore.homework;

import com.vaszilvalentin.educore.data.HomeworkDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages homework assignments: creation, retrieval, updating, deletion,
 * class/subject filtering, and handling student submissions and grades.
 */
public class HomeworkManager {

    /**
     * Adds a new homework assignment to the database. Automatically assigns a
     * unique ID.
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
     * Retrieves a specific homework by ID.
     *
     * @param homeworkId The homework ID to find
     * @return Homework object or null if not found
     */
    public static Homework getHomeworkById(String homeworkId) {
        for (Homework homework : HomeworkDatabase.loadHomework()) {
            if (homework.getId().equals(homeworkId)) {
                return homework;
            }
        }
        return null;
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
     * Updates a specific homework assignment by ID while preserving existing
     * submissions.
     *
     * @param id The homework ID to update
     * @param updatedHomework The updated homework object
     */
    public static void updateHomework(String id, Homework updatedHomework) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (int i = 0; i < homeworkList.size(); i++) {
            Homework existingHomework = homeworkList.get(i);
            if (existingHomework.getId().equals(id)) {
                // Preserve the existing submissions
                updatedHomework.setSubmissions(existingHomework.getSubmissions());
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
     * Adds or replaces a submission for a student with submission date. Grade
     * is initially set to "-" (ungraded).
     *
     * @param homeworkId Homework ID
     * @param studentId Student ID
     * @param filePath Submitted file path
     * @param submissionDate When the work was submitted
     */
    public static void addSubmission(String homeworkId, String studentId,
            String filePath, LocalDateTime submissionDate) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                Homework.Submission submission = new Homework.Submission(filePath, "-", submissionDate);
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
     * @param studentId Student ID
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
     * @param studentId Student ID
     * @param grade Grade to assign (e.g., "5")
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
     * @param studentId Student ID
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

    /**
     * Gets the submission date for a student's homework.
     *
     * @param homeworkId Homework ID
     * @param studentId Student ID
     * @return Submission date or null if not submitted
     */
    public static LocalDateTime getSubmissionDate(String homeworkId, String studentId) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                Homework.Submission submission = homework.getSubmission(studentId);
                if (submission != null) {
                    return submission.getSubmissionDate();
                }
            }
        }

        return null;
    }

    /**
     * Checks if a student has submitted their homework.
     *
     * @param homeworkId Homework ID
     * @param studentId Student ID
     * @return true if submitted, false otherwise
     */
    public static boolean hasSubmitted(String homeworkId, String studentId) {
        List<Homework> homeworkList = HomeworkDatabase.loadHomework();

        for (Homework homework : homeworkList) {
            if (homework.getId().equals(homeworkId)) {
                return homework.hasSubmitted(studentId);
            }
        }

        return false;
    }

    /**
     * Gets all submissions for a specific homework.
     *
     * @param homeworkId Homework ID
     * @return Map of student IDs to their submissions
     */
    public static Map<String, Homework.Submission> getAllSubmissions(String homeworkId) {
        Homework homework = getHomeworkById(homeworkId);
        return homework != null ? homework.getSubmissions() : new HashMap<>();
    }
}
