package com.greally2014.ticketmanager.dto;

import com.greally2014.ticketmanager.entity.Address;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddressDto {

    @NotNull(message = "Address line 1 is required")
    @Size(min = 1, max = 50, message = "Address line 1 is required")
    private String line1;

    private String line2;

    @NotNull(message = "City is required")
    @Size(min = 1, max = 50, message = "City is required")
    private String city;

    @NotNull(message = "County is required")
    @Size(min = 1, max = 50, message = "County is required")
    private String county;

    public AddressDto(Address address) {
        this.line1 = address.getLine1();
        this.line2 = address.getLine2();
        this.city = address.getCity();
        this.county = address.getCounty();
    }

    public AddressDto() {
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
