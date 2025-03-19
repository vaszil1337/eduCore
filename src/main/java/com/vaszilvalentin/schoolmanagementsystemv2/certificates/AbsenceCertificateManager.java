/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author vaszilvalentin
 */
package com.vaszilvalentin.schoolmanagementsystemv2.certificates;

import com.vaszilvalentin.schoolmanagementsystemv2.data.AbsenceCertificateDatabase;
import java.util.ArrayList;
import java.util.List;

public class AbsenceCertificateManager {

    public static void addCertificate(AbsenceCertificate certificate) {
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();
        certificate.setId(AbsenceCertificateDatabase.generateCertificateId());
        certificates.add(certificate);
        AbsenceCertificateDatabase.saveCertificates(certificates);
    }

    public static List<AbsenceCertificate> getAllCertificates() {
        return AbsenceCertificateDatabase.loadCertificates();
    }

    public static List<AbsenceCertificate> getCertificatesByUser(String userId) {
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();
        List<AbsenceCertificate> userCertificates = new ArrayList<>();
        for (AbsenceCertificate certificate : certificates) {
            if (certificate.getUserId().equals(userId)) {
                userCertificates.add(certificate);
            }
        }
        return userCertificates;
    }

    public static void deleteCertificate(String certificateId) {
        List<AbsenceCertificate> certificates = AbsenceCertificateDatabase.loadCertificates();
        certificates.removeIf(cert -> cert.getId().equals(certificateId));
        AbsenceCertificateDatabase.saveCertificates(certificates);
    }
}

