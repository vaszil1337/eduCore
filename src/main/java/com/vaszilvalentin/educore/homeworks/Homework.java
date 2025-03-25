/*
 * Represents a homework assignment, including its details, deadline, 
 * and student submissions.
 */

package com.vaszilvalentin.educore.homeworks;

/**
 * @author vaszilvalentin
 */

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Homework {
    private String id; // Unique identifier for the homework
    private String description; // Description of the assignment
    private LocalDateTime deadline; // Submission deadline
    private String grade; // Grade assigned to the homework
    private Map<String, String> submissions; // Stores student submissions (Student ID -> File Path)

    /**
     * Constructor to initialize a Homework object.
     * 
     * @param id          Unique identifier for the homework
     * @param description Description of the homework assignment
     * @param deadline    Submission deadline
     * @param grade       Assigned grade (can be updated later)
     */
    public Homework(String id, String description, LocalDateTime deadline, String grade) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.grade = grade;
        this.submissions = new HashMap<>();
    }

    // Getters and setters

    /** @return The unique identifier of the homework */
    public String getId() { return id; }

    /** Sets the unique identifier of the homework */
    public void setId(String id) { this.id = id; }

    /** @return The description of the homework assignment */
    public String getDescription() { return description; }

    /** Sets the description of the homework assignment */
    public void setDescription(String description) { this.description = description; }

    /** @return The submission deadline */
    public LocalDateTime getDeadline() { return deadline; }

    /** Sets the submission deadline */
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    /** @return The assigned grade */
    public String getGrade() { return grade; }

    /** Sets the assigned grade */
    public void setGrade(String grade) { this.grade = grade; }

    /** @return A map of student submissions (Student ID -> File Path) */
    public Map<String, String> getSubmissions() { return submissions; }

    /** Sets the student submissions */
    public void setSubmissions(Map<String, String> submissions) { this.submissions = submissions; }

    /**
     * Adds a submission for a student.
     * 
     * @param studentId The ID of the student submitting the homework
     * @param filePath  The file path of the submission
     */
    public void addSubmission(String studentId, String filePath) {
        submissions.put(studentId, filePath);
    }

    /**
     * Removes a submission for a student.
     * 
     * @param studentId The ID of the student whose submission should be removed
     */
    public void removeSubmission(String studentId) {
        submissions.remove(studentId);
    }

    /**
     * Returns a string representation of the Homework object.
     */
    @Override
    public String toString() {
        return "Homework{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", grade='" + grade + '\'' +
                ", submissions=" + submissions +
                '}';
    }
}