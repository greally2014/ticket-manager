package com.greally2014.ticketmanager.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

    @Column
    protected String line1;

    @Column
    protected String line2;

    @Column
    protected String city;

    @Column
    protected String county;


    public Address(String line1, String line2, String city, String county) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.county = county;
    }

    public Address() {
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}
