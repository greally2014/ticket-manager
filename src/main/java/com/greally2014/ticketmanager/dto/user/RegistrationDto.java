package com.greally2014.ticketmanager.dto.user;

import com.greally2014.ticketmanager.validation.fieldMatch.FieldMatch;
import com.greally2014.ticketmanager.validation.password.ValidPassword;
import com.greally2014.ticketmanager.validation.username.UniqueUsername;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List(
        @FieldMatch(first = "password", second = "matchingPassword")
)
public class RegistrationDto extends UserDto {

    @NotNull(message = "Username is required")
    @Size(min = 1, max = 50, message = "Username is required")
    @UniqueUsername
    private String username;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must contain at least 8 characters")
    @ValidPassword
    private String password;
    private String matchingPassword;

    @NotNull(message = "Please select a role")
    @Size(min = 1, max = 50, message = "Please select a role")
    private String formRole;

    public RegistrationDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFormRole() {
        return formRole;
    }

    public void setFormRole(String formRole) {
        this.formRole = formRole;
    }

}
