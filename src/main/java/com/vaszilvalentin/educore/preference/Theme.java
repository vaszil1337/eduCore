/*
 * Enum that represents the two theme options available: LIGHT and DARK.
 * It provides methods to retrieve the display name for each theme, convert a string to a theme, 
 * and override the `toString` method for proper theme representation.
 */

package com.vaszilvalentin.educore.preference;

public enum Theme {
    
    // Enum constants representing the available themes
    LIGHT("Light"),
    DARK("Dark");

    // A field to hold the display name for each theme
    private final String displayName;

    /**
     * Constructor for the Theme enum.
     * 
     * @param displayName The name that will be displayed for the theme.
     */
    Theme(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the display name of the theme.
     * 
     * @return The display name of the theme.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a string to its corresponding Theme enum value.
     * 
     * @param text The string representation of a theme (e.g., "Light" or "Dark").
     * @return The corresponding Theme enum value.
     */
    public static Theme fromString(String text) {
        for (Theme theme : Theme.values()) {
            if (theme.displayName.equalsIgnoreCase(text)) {
                return theme;
            }
        }
        return LIGHT; // Default to LIGHT if the string doesn't match any theme
    }
    
    /**
     * Overrides the default `toString` method to return the display name.
     * This is useful for comboboxes or any UI components that display the theme name.
     * 
     * @return The display name of the theme.
     */
    @Override
    public String toString() {
        return displayName; // Returns the display name to use in UI components
    }
}