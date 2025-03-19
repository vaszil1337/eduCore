/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vaszilvalentin.schoolmanagementsystemv2.certificates;

/**
 *
 * @author vaszilvalentin
 */

import java.util.Date;

public class AbsenceCertificate {
    private String id;
    private String userId;
    private String filePath;
    private Date date;
    private String name;
    private String certificateType;

    public AbsenceCertificate(String userId, String filePath, Date date, String name, String certificateType) {
        this.userId = userId;
        this.filePath = filePath;
        this.date = date;
        this.name = name;
        this.certificateType = certificateType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }
}
