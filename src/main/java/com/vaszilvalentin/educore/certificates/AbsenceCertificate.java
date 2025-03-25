/*
 * Represents an absence certificate for a user.
 * Stores details such as the user ID, file location, date, name, and type of certificate.
 */
package com.vaszilvalentin.educore.certificates;

/**
 * @author vaszilvalentin
 */

import java.util.Date;

public class AbsenceCertificate {

    // Unique identifier for the certificate
    private String id;

    // ID of the user associated with this certificate
    private String userId;

    // File path where the certificate document is stored
    private String filePath;

    // Date when the absence was recorded
    private Date date;

    // Name of the certificate (e.g., "Medical Certificate")
    private String name;

    // Type of certificate (e.g., "Medical", "Parental Justification")
    private String certificateType;

    /**
     * Constructs an AbsenceCertificate instance.
     *
     * @param userId          ID of the user who owns this certificate
     * @param filePath        Path to the stored certificate file
     * @param date            Date of the absence
     * @param name            Name of the certificate
     * @param certificateType Type of the certificate
     */
    public AbsenceCertificate(String userId, String filePath, Date date, String name, String certificateType) {
        this.userId = userId;
        this.filePath = filePath;
        this.date = date;
        this.name = name;
        this.certificateType = certificateType;
    }

    /**
     * Retrieves the certificate ID.
     *
     * @return Certificate ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the certificate ID.
     *
     * @param id The unique identifier for this certificate
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the user ID associated with this certificate.
     *
     * @return User ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for this certificate.
     *
     * @param userId The ID of the user who owns this certificate
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the file path where the certificate is stored.
     *
     * @return File path as a string
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path for the certificate.
     *
     * @param filePath The path where the certificate document is stored
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Retrieves the date of the absence.
     *
     * @return Absence date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date for this certificate.
     *
     * @param date The date when the absence was recorded
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Retrieves the name of the certificate.
     *
     * @return Certificate name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the certificate.
     *
     * @param name The name of the certificate (e.g., "Medical Certificate")
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the type of certificate.
     *
     * @return Certificate type (e.g., "Medical", "Parental Justification")
     */
    public String getCertificateType() {
        return certificateType;
    }

    /**
     * Sets the type of certificate.
     *
     * @param certificateType The type of the certificate
     */
    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }
}