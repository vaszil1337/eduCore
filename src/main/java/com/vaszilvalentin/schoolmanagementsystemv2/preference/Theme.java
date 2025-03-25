/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.preference;

/**
 *
 * @author vaszilvalentin
 */
public enum Theme {
    LIGHT("Világos"),
    DARK("Sötét");

    private final String displayName;

    Theme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Converts a string to a Theme enum value
    public static Theme fromString(String text) {
        for (Theme theme : Theme.values()) {
            if (theme.displayName.equalsIgnoreCase(text)) {
                return theme;
            }
        }
        return LIGHT; // Default to LIGHT if the value is invalid
    }
    
    @Override
    public String toString() {
        return displayName; // Combobox localization
    }
}