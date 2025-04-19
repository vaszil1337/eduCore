package com.vaszilvalentin.educore.users;

/**
 * Enum representing the different class levels in a school system.
 * Each class level corresponds to a grade and a section, such as 9.A, 9.B, etc.
 */
public enum ClassLevel {

    // Enum constants representing the various class levels and their corresponding display names
    NINE_A("9.A"),
    NINE_B("9.B"),
    TEN_A("10.A"),
    TEN_B("10.B"),
    ELEVEN_A("11.A"),
    ELEVEN_B("11.B"),
    TWELVE_A("12.A"),
    TWELVE_B("12.B");

    // A field to store the display name for each class level (e.g., "9.A")
    private final String displayName;

    /**
     * Constructor to initialize the enum constant with its display name.
     *
     * @param displayName the string representation of the class level
     */
    ClassLevel(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the display name of the class level.
     *
     * @return the string representation of the class level
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Overrides the default toString method to return the display name.
     * This provides a convenient string representation of the class level.
     *
     * @return the display name of the class level
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}
