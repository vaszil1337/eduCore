package com.vaszilvalentin.educore.homeworks;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a homework assignment, including its details, deadline,
 * subject, class, and student submissions.
 */
public class Homework {
    private String id; // Unique identifier for the homework
    private String description; // Description of the assignment
    private LocalDateTime deadline; // Submission deadline
    private String subject; // Subject of the homework (e.g., Math, History)
    private String classId; // The class to which the homework is assigned (e.g., 11.B)

    // Stores student submissions (Student ID -> Submission)
    private Map<String, Submission> submissions;

    /**
     * Represents an individual student submission,
     * including file path and grade.
     */
    public static class Submission {
        private String filePath;
        private String grade;

        public Submission() {
            this.filePath = "";
            this.grade = "-";
        }

        public Submission(String filePath, String grade) {
            this.filePath = filePath;
            this.grade = grade;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        @Override
        public String toString() {
            return "Submission{" +
                    "filePath='" + filePath + '\'' +
                    ", grade='" + grade + '\'' +
                    '}';
        }
    }

    // Constructor
    public Homework(String id, String description, LocalDateTime deadline, String subject, String classId) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.subject = subject;
        this.classId = classId;
        this.submissions = new HashMap<>();
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Map<String, Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Map<String, Submission> submissions) {
        this.submissions = submissions;
    }

    // Submission-related methods

    /**
     * Adds or replaces a submission for a student.
     *
     * @param studentId The ID of the student
     * @param submission The submission object containing file and grade
     */
    public void addSubmission(String studentId, Submission submission) {
        submissions.put(studentId, submission);
    }

    /**
     * Removes a student's submission.
     *
     * @param studentId The ID of the student to remove
     */
    public void removeSubmission(String studentId) {
        submissions.remove(studentId);
    }

    /**
     * Gets a student's submission object.
     *
     * @param studentId The student ID
     * @return Submission object or null if not found
     */
    public Submission getSubmission(String studentId) {
        return submissions.get(studentId);
    }

    @Override
    public String toString() {
        return "Homework{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", subject='" + subject + '\'' +
                ", classId='" + classId + '\'' +
                ", submissions=" + submissions +
                '}';
    }
}
