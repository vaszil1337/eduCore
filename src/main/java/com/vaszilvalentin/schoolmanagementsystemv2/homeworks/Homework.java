/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.homeworks;

/**
 *
 * @author vaszilvalentin
 */

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Homework {
    private String id;
    private String description;
    private LocalDateTime deadline;
    private String grade;
    private Map<String, String> submissions; // Student ID -> File Path

    // Constructor
    public Homework(String id, String description, LocalDateTime deadline, String grade) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.grade = grade;
        this.submissions = new HashMap<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public Map<String, String> getSubmissions() { return submissions; }
    public void setSubmissions(Map<String, String> submissions) { this.submissions = submissions; }

    // Add a submission for a student
    public void addSubmission(String studentId, String filePath) {
        submissions.put(studentId, filePath);
    }

    // Remove a submission for a student
    public void removeSubmission(String studentId) {
        submissions.remove(studentId);
    }

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
