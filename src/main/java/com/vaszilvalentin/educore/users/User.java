package com.vaszilvalentin.educore.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user entity in the education system with role-based attributes.
 * Supports three main roles: student, teacher, and admin.
 * Uses the Builder pattern for flexible object creation.
 * Includes validation to ensure subjects and taughtClasses are only set for teachers.
 */
public class User {
    private String id;                   // Unique identifier for the user
    private String name;                 // Full name of the user
    private String email;                // Email address (used as username)
    private String password;             // Encrypted password
    private int age;                     // Age (primarily for students)
    private String role;                 // Role: "student", "teacher", or "admin"
    private String classId;              // Class identifier (for students)
    private List<String> subjects;       // List of teaching subjects (for teachers)
    private List<String> taughtClasses;  // List of classes taught (for teachers)

    /**
     * Builder class for creating User instances with a fluent interface.
     * Ensures required fields are provided and handles optional fields.
     * Includes validation for role-specific fields.
     */
    public static class Builder {
        // Required parameters
        private final String name;
        private final String email;
        private final String role;

        // Optional parameters with defaults
        private String id;
        private String password;
        private int age;
        private String classId;
        private List<String> subjects = new ArrayList<>();
        private List<String> taughtClasses = new ArrayList<>();

        public Builder(String name, String email, String role) {
            this.name = Objects.requireNonNull(name, "Name cannot be null");
            this.email = Objects.requireNonNull(email, "Email cannot be null");
            this.role = Objects.requireNonNull(role, "Role cannot be null");
            
            // Initialize lists only if the user is a teacher
            if (!"teacher".equals(role)) {
                this.subjects = null;
                this.taughtClasses = null;
            }
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder classId(String classId) {
            this.classId = classId;
            return this;
        }

        public Builder subjects(List<String> subjects) {
            if (!"teacher".equals(role)) {
                throw new IllegalArgumentException("Subjects can only be set for teachers.");
            }
            if (subjects != null) {
                this.subjects = new ArrayList<>(subjects);
            }
            return this;
        }

        public Builder taughtClasses(List<String> taughtClasses) {
            if (!"teacher".equals(role)) {
                throw new IllegalArgumentException("Taught classes can only be set for teachers.");
            }
            if (taughtClasses != null) {
                this.taughtClasses = new ArrayList<>(taughtClasses);
            }
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.age = builder.age;
        this.role = builder.role;
        this.classId = builder.classId;
        this.subjects = builder.subjects;
        this.taughtClasses = builder.taughtClasses;
    }

    // Getters and Setters (with defensive copies where needed)

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }

    public String getRole() { return role; }

    public void setRole(String role) { 
        this.role = role;
        // If role changes to non-teacher, clear teacher-specific fields
        if (!"teacher".equals(role)) {
            this.subjects = null;
            this.taughtClasses = null;
        }
    }

    public String getClassId() { return classId; }

    public void setClassId(String classId) { this.classId = classId; }

    public List<String> getSubjects() {
        return subjects == null ? null : new ArrayList<>(subjects);
    }

    public void setSubjects(List<String> subjects) {
        if (!"teacher".equals(role)) {
            throw new IllegalArgumentException("Subjects can only be set for teachers.");
        }
        this.subjects = subjects == null ? null : new ArrayList<>(subjects);
    }

    public void addSubject(String subject) {
        if (!"teacher".equals(role)) {
            throw new IllegalArgumentException("Subjects can only be added for teachers.");
        }
        if (this.subjects == null) {
            this.subjects = new ArrayList<>();
        }
        this.subjects.add(subject);
    }

    public List<String> getTaughtClasses() {
        return taughtClasses == null ? null : new ArrayList<>(taughtClasses);
    }

    public void setTaughtClasses(List<String> taughtClasses) {
        if (!"teacher".equals(role)) {
            throw new IllegalArgumentException("Taught classes can only be set for teachers.");
        }
        this.taughtClasses = taughtClasses == null ? null : new ArrayList<>(taughtClasses);
    }

    public void addTaughtClass(String classId) {
        if (!"teacher".equals(role)) {
            throw new IllegalArgumentException("Taught classes can only be added for teachers.");
        }
        if (this.taughtClasses == null) {
            this.taughtClasses = new ArrayList<>();
        }
        this.taughtClasses.add(classId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", classId='" + classId + '\'' +
                ", subjects=" + subjects +
                ", taughtClasses=" + taughtClasses +
                '}';
    }
}