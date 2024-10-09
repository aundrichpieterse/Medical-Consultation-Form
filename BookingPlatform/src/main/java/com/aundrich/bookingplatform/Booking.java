package com.aundrich.bookingplatform;

import java.util.Date;

public class Booking {
    private String name;
    private String surname;
    private Date consultationDate;
    private String consultationPeriod;
    private String consultationReason;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getConsultationDate() {
        return consultationDate;
    }

    public void setConsultationDate(Date consultationDate) {
        this.consultationDate = consultationDate;
    }

    public String getConsultationPeriod() {
        return consultationPeriod;
    }

    public void setConsultationPeriod(String consultationPeriod) {
        this.consultationPeriod = consultationPeriod;
    }

    public String getConsultationReason() {
        return consultationReason;
    }

    public void setConsultationReason(String consultationReason) {
        this.consultationReason = consultationReason;
    }
}
