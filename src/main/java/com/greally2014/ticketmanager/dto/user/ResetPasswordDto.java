package com.greally2014.ticketmanager.dto.user;

import com.greally2014.ticketmanager.validation.password.ValidPassword;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ResetPasswordDto {

    private String code;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must contain at least 8 characters")
    @ValidPassword // must contain a capital letter and special character
    private String password;
    private String matchingPassword;

    public ResetPasswordDto(String code) {
        this.code = code;
    }

    public ResetPasswordDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }
}
