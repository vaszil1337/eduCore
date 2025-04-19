/*
 * Represents an absence certificate for a user.
 * Stores details such as the user ID, file location, date range, name, and type of certificate.
 */
package com.vaszilvalentin.educore.absence;

import java.time.LocalDate;

/**
 * @author vaszilvalentin
 */
public class AbsenceCertificate {

    // Unique identifier for the certificate
    private String id;

    // ID of the user associated with this certificate
    private String userId;

    // File path where the certificate document is stored
    private String filePath = "-";

    // Start date of the absence period
    private LocalDate startDate;

    // End date of the absence period
    private LocalDate endDate;

    // Type of certificate (e.g., "Medical", "Parental Justification")
    private String certificateType = "-";
    
    private boolean approvedStatus = false;

    /**
     * Constructs an AbsenceCertificate instance.
     *
     * @param userId ID of the user who owns this certificate
     * @param filePath Path to the stored certificate file
     * @param startDate Start date of the absence period
     * @param endDate End date of the absence period
     * @param certificateType Type of the certificate
     */
    public AbsenceCertificate(String userId, String filePath, LocalDate startDate, LocalDate endDate, String certificateType) {
        this.userId = userId;
        this.filePath = (filePath != null && !filePath.isBlank()) ? filePath : "-";
        this.startDate = startDate;
        this.endDate = endDate;
        this.certificateType = (certificateType != null && !certificateType.isBlank()) ? certificateType : "-";
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
     * Retrieves the start date of the absence period.
     *
     * @return Start date of absence
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date for this certificate.
     *
     * @param startDate The start date of the absence period
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Retrieves the end date of the absence period.
     *
     * @return End date of absence
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date for this certificate.
     *
     * @param endDate The end date of the absence period
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
    
    public boolean getApprovedStatus(){
        return approvedStatus;
    }
    
    public void setApprovedStatus(boolean approvedStatus){
        this.approvedStatus = approvedStatus;
    }

    public boolean hasFile() {
        return filePath != null && !filePath.equals("-") && !filePath.isBlank();
    }

    public boolean hasType() {
        return certificateType != null && !certificateType.equals("-") && !certificateType.isBlank();
    }

}
