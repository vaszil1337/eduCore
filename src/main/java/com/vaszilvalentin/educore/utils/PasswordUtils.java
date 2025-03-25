/*
 * Utility class for generating random passwords.
 * The generated password can include lowercase, uppercase, numbers, and special characters.
 */

package com.vaszilvalentin.educore.utils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordUtils {
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARACTERS = LOWERCASE_CHARACTERS.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final String ALL_PASSWORD_CHARACTERS = LOWERCASE_CHARACTERS + UPPERCASE_CHARACTERS + DIGITS + SPECIAL_CHARACTERS;
    private static final Random RANDOM = new SecureRandom();

    // Generate a random password with the specified length
    public static String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters.");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(ALL_PASSWORD_CHARACTERS.length());
            password.append(ALL_PASSWORD_CHARACTERS.charAt(randomIndex));
        }
        return password.toString();
    }
}
