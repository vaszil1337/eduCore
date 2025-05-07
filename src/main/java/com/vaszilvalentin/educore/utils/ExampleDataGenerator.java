package com.vaszilvalentin.educore.utils;

import com.vaszilvalentin.educore.users.ClassLevel;
import com.vaszilvalentin.educore.users.Subject;
import com.vaszilvalentin.educore.users.User;
import com.vaszilvalentin.educore.users.UserManager;

import java.time.LocalDate;
import java.util.*;

/**
 * Utility class for generating and populating the system with example user
 * data. This includes students, teachers, and admin users, each with randomized
 * but realistic attributes.
 */
public class ExampleDataGenerator {

    private static final Random random = new Random();

    /**
     * Populates the system with a specified number of students, teachers, and
     * administrators. Users are generated with random names, emails,
     * birthdates, roles, and class/subject assignments.
     *
     * @param studentCount Number of students to generate
     * @param teacherCount Number of teachers to generate
     * @param adminCount Number of administrators to generate
     */
    public static void populateSystemWithExampleUsers(int studentCount, int teacherCount, int adminCount) {
        for (int i = 0; i < studentCount; i++) {
            UserManager.addUser(generateStudent());
        }
        for (int i = 0; i < teacherCount; i++) {
            UserManager.addUser(generateTeacher());
        }
        for (int i = 0; i < adminCount; i++) {
            UserManager.addUser(generateAdmin());
        }
    }

    /**
     * Generates a student user with a random name, unique email, birth date,
     * and class level.
     *
     * @return a new User object representing a student
     */
    private static User generateStudent() {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);
        String email = generateStudentEmail(firstName, lastName);

        // Generate birth date for ages between 10 and 18
        LocalDate birthDate = LocalDate.now()
                .minusYears(10 + random.nextInt(8))
                .minusMonths(random.nextInt(12))
                .minusDays(random.nextInt(30));

        ClassLevel classLevel = getRandomElement(List.of(ClassLevel.values()));

        return new User.Builder(firstName + " " + lastName, email, "student")
                .birthDate(birthDate)
                .classId(classLevel.toString())
                .build();
    }

    /**
     * Generates a teacher user with random attributes including multiple
     * subjects and classes.
     *
     * @return a new User object representing a teacher
     */
    private static User generateTeacher() {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);
        String email = generateTeacherEmail(firstName, lastName);

        // Generate birth date for ages between 25 and 65
        LocalDate birthDate = LocalDate.now()
                .minusYears(25 + random.nextInt(40))
                .minusMonths(random.nextInt(12))
                .minusDays(random.nextInt(30));

        // Randomly assign 2–4 unique subjects
        List<Subject> teacherSubjects = new ArrayList<>();
        while (teacherSubjects.size() < 2 + random.nextInt(3)) {
            Subject subject = getRandomElement(List.of(Subject.values()));
            if (!teacherSubjects.contains(subject)) {
                teacherSubjects.add(subject);
            }
        }
        List<String> subjectStrings = teacherSubjects.stream()
                .map(Subject::toString)
                .toList();

        // Randomly assign 1–3 unique classes
        List<ClassLevel> taughtClasses = new ArrayList<>();
        while (taughtClasses.size() < 1 + random.nextInt(3)) {
            ClassLevel classLevel = getRandomElement(List.of(ClassLevel.values()));
            if (!taughtClasses.contains(classLevel)) {
                taughtClasses.add(classLevel);
            }
        }
        List<String> classStrings = taughtClasses.stream()
                .map(ClassLevel::toString)
                .toList();

        return new User.Builder(firstName + " " + lastName, email, "teacher")
                .birthDate(birthDate)
                .subjects(subjectStrings)
                .taughtClasses(classStrings)
                .build();
    }

    /**
     * Generates an admin user with a random name and a unique email.
     *
     * @return a new User object representing an administrator
     */
    private static User generateAdmin() {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);
        String email = generateAdminEmail();

        return new User.Builder(firstName + " " + lastName, email, "admin")
                .birthDate(LocalDate.now().minusYears(30 + random.nextInt(20)))
                .build();
    }

    /**
     * Generates a unique student email based on the given name. Ensures no
     * collisions with existing users.
     */
    private static String generateStudentEmail(String firstName, String lastName) {
        String base = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String email = base + "@student.school.edu";
        int counter = 1;
        while (UserManager.userExists(email)) {
            email = base + counter + "@student.school.edu";
            counter++;
        }
        return email;
    }

    /**
     * Generates a unique teacher email based on the given name. Ensures no
     * collisions with existing users.
     */
    private static String generateTeacherEmail(String firstName, String lastName) {
        String base = firstName.toLowerCase().charAt(0) + lastName.toLowerCase();
        String email = base + "@teacher.school.edu";
        int counter = 1;
        while (UserManager.userExists(email)) {
            email = base + counter + "@teacher.school.edu";
            counter++;
        }
        return email;
    }

    /**
     * Generates a unique admin email. Uses the base "admin" and appends numbers
     * if needed.
     */
    private static String generateAdminEmail() {
        String base = "admin";
        String email = base + "@school.edu";
        int counter = 1;
        while (UserManager.userExists(email)) {
            email = base + counter + "@school.edu";
            counter++;
        }
        return email;
    }

    /**
     * Selects a random element from the given list.
     *
     * @param list the list to select from
     * @param <T> the type of elements in the list
     * @return a randomly selected element
     */
    private static <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    // Predefined lists of common first and last names used for generating user names
    private static final List<String> FIRST_NAMES = Arrays.asList(
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer",
            "Michael", "Linda", "William", "Elizabeth", "David", "Barbara",
            "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah",
            "Charles", "Karen", "Christopher", "Nancy", "Daniel", "Lisa",
            "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra",
            "Donald", "Ashley", "Steven", "Kimberly", "Paul", "Emily",
            "Andrew", "Donna", "Joshua", "Michelle", "Kenneth", "Dorothy",
            "Kevin", "Carol", "Brian", "Amanda", "George", "Melissa",
            "Edward", "Deborah", "Ronald", "Stephanie", "Timothy", "Rebecca",
            "Jason", "Sharon", "Jeffrey", "Laura", "Ryan", "Cynthia",
            "Jacob", "Kathleen", "Gary", "Amy", "Nicholas", "Shirley"
    );

    private static final List<String> LAST_NAMES = Arrays.asList(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia",
            "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez",
            "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore",
            "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
            "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
            "Walker", "Young", "Allen", "King", "Wright", "Scott",
            "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams",
            "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
            "Carter", "Roberts", "Gomez", "Phillips", "Evans", "Turner",
            "Diaz", "Parker", "Cruz", "Edwards", "Collins", "Reyes"
    );
}
