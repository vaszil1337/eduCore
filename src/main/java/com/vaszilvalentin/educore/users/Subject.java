package com.vaszilvalentin.educore.users;

/**
 * Enum representing various school subjects.
 * Each subject is associated with a display name that provides a user-friendly string representation.
 */
public enum Subject {

    // Enum constants representing different school subjects and their corresponding display names
    MATH("Mathematics"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    LITERATURE("Literature"),
    HISTORY("History"),
    ART("Art");

    // A field to store the display name for each subject (e.g., "Mathematics")
    private final String displayName;

    /**
     * Constructor to initialize the enum constant with its display name.
     *
     * @param displayName the string representation of the subject's full name
     */
    Subject(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the display name of the subject.
     *
     * @return the string representation of the subject's full name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Overrides the default toString method to return the display name.
     * This provides a convenient string representation of the subject.
     *
     * @return the display name of the subject
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}
