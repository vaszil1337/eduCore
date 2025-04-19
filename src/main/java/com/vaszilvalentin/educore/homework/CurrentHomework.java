package com.vaszilvalentin.educore.homework;

public class CurrentHomework {

    // Static variables for storing current values
    private static String editHomeworkId;
    private static String viewHomeworkId;

    /**
     * Private constructor to prevent instantiation.
     * This class is designed to be used statically.
     */
    private CurrentHomework() {}

    /**
     * Sets the Homework ID to be edited.
     *
     * @param homeworkId The ID of the homework to be edited
     */
    public static void setEditHomeworkId(String homeworkId) {
        editHomeworkId = homeworkId;
    }

    /**
     * Retrieves the Homework ID to be edited.
     *
     * @return The Homework ID, or null if not set
     */
    public static String getEditHomeworkId() {
        return editHomeworkId;
    }

    /**
     * Checks whether a Homework ID has been set for editing.
     *
     * @return true if a Homework ID is set, false otherwise
     */
    public static boolean hasEditHomeworkId() {
        return editHomeworkId != null;
    }
    
    public static void setViewHomeworkId(String homeworkId) {
        viewHomeworkId = homeworkId;
    }
    
    public static String getViewHomeworkId(){
        return viewHomeworkId;
    }
}
