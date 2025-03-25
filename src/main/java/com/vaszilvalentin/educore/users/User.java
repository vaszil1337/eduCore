/*
 * Represents a User in the system.
 * This class contains all the attributes related to a user, including id, name, email, password, age, role, grade, subject, department,
 * and the classes taught by the user. It also provides methods to access and modify these attributes.
 */

package com.vaszilvalentin.educore.users;

import java.util.List;
import java.util.ArrayList;

public class User {

    // Attributes of the User class
    private String id;
    private String name;
    private String email;
    private String password;
    private int age; // Optional, can be used for students
    private String role; // "student", "teacher", or "admin"
    private String grade; // Optional, for students
    private String subject; // Optional, for teachers
    private String department; // Optional, for admins
    private List<String> taughtClasses;

    /**
     * Constructor to initialize a User object.
     * 
     * @param name        The name of the user.
     * @param email       The email of the user.
     * @param age         The age of the user (optional, used for students).
     * @param role        The role of the user ("student", "teacher", or "admin").
     * @param grade       The grade of the student (optional).
     * @param subject     The subject the teacher teaches (optional).
     * @param department  The department of the admin (optional).
     * @param taughtClasses The list of classes the teacher teaches (optional).
     */
    public User(String name, String email, int age, String role, String grade, String subject, String department, List<String> taughtClasses) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = role;
        this.grade = grade;
        this.subject = subject;
        this.department = department;
        this.taughtClasses = (taughtClasses != null) ? taughtClasses : new ArrayList<>(); // Ensure it's never null
    }

    // Getters and setters for all attributes

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<String> getTaughtClasses() {
        return taughtClasses;
    }

    public void setTaughtClasses(List<String> taughtClasses) {
        this.taughtClasses = taughtClasses;
    }

    @Override
    public String toString() {
        return "User{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", age=" + age
                + ", role='" + role + '\''
                + ", grade='" + grade + '\''
                + ", subject='" + subject + '\''
                + ", department='" + department + '\''
                + ", taughtClasses=" + taughtClasses
                + '}';
    }
}