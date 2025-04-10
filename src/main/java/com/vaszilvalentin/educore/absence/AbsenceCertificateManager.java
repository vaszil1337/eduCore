/*
 * Manages absence certificates, including adding, retrieving, and deleting certificates.
 * This class interacts with the AbsenceCertificateDatabase for data persistence.
 */

package com.vaszilvalentin.educore.absence;

/**
 * @author vaszilvalentin
 */

import com.vaszilvalentin.educore.data.AbsenceCertificateDatabase;
import java.util.ArrayList;
import java.util.List;

public class AbsenceCertificateManager {

    /**
     * Adds a new absence certificate to the database.
     * 
     * @param certificate The absence certificate to be added
     */
    public static void addCertificate(AbsenceCertificate certificate) {
        // Load existing certificates from the database
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();

        // Assign a unique ID to the new certificate
        certificate.setId(AbsenceCertificateDatabase.generateCertificateId());

        // Add the new certificate to the list
        certificates.add(certificate);

        // Save the updated list back to the database
        AbsenceCertificateDatabase.saveCertificates(certificates);
    }

    /**
     * Retrieves all absence certificates from the database.
     * 
     * @return List of all absence certificates
     */
    public static List<AbsenceCertificate> getAllCertificates() {
        return AbsenceCertificateDatabase.loadCertificates();
    }

    /**
     * Retrieves all absence certificates associated with a specific user.
     * 
     * @param userId The ID of the user whose certificates are being retrieved
     * @return List of absence certificates belonging to the specified user
     */
    public static List<AbsenceCertificate> getCertificatesByUser(String userId) {
        // Load all certificates from the database
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();

        // Filter certificates belonging to the given user ID
        List<AbsenceCertificate> userCertificates = new ArrayList<>();
        for (AbsenceCertificate certificate : certificates) {
            if (certificate.getUserId().equals(userId)) {
                userCertificates.add(certificate);
            }
        }
        return userCertificates;
    }

    /**
     * Deletes an absence certificate by its unique ID.
     * 
     * @param certificateId The ID of the certificate to be deleted
     */
    public static void deleteCertificate(String certificateId) {
        // Load all certificates from the database
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();

        // Remove the certificate that matches the given ID
        certificates.removeIf(cert -> cert.getId().equals(certificateId));

        // Save the updated list back to the database
        AbsenceCertificateDatabase.saveCertificates(certificates);
    }
}