/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.users;

/**
 *
 * @author vaszilvalentin
 */
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private int age; // Optional, can be used for students
    private String role; // "student", "teacher", or "admin"
    private String grade; // Optional, for students
    private String subject; // Optional, for teachers
    private String department; // Optional, for admins

    // Constructor
    public User(String name, String email, int age, String role, String grade, String subject, String department) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = role;
        this.grade = grade;
        this.subject = subject;
        this.department = department;
    }

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

    // Getters and setters
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

    @Override
    public String toString() {
        return "User{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", email=" + email
                + ", age=" + age
                + ", role='" + role + '\''
                + ", grade='" + grade + '\''
                + ", subject='" + subject + '\''
                + ", department='" + department + '\''
                + '}';
    }
}
