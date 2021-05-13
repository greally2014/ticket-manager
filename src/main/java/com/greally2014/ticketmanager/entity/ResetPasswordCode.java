package com.greally2014.ticketmanager.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "reset_password_code")
public class ResetPasswordCode {
    private static final int EXPIRATION = 60 * 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "expiry_date")
    private Date expiryDate;

    private Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(calendar.getTime().getTime()));
        calendar.add(Calendar.MINUTE, EXPIRATION);
        return new Date(calendar.getTime().getTime());
    }

    public ResetPasswordCode(String code, Date expiryDate) {
        this.code = code;
        this.expiryDate = expiryDate;
    }

    public ResetPasswordCode(String code) {
        this.code = code;
        this.expiryDate = calculateExpiryDate();
    }

    public ResetPasswordCode() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
