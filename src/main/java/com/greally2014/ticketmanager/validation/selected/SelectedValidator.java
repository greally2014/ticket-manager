package com.greally2014.ticketmanager.validation.selected;

import com.greally2014.ticketmanager.dto.user.UserProfileDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SelectedValidator implements ConstraintValidator<Selected, List<UserProfileDto>> {
    @Override
    public boolean isValid(List<UserProfileDto> userProfileDtos, ConstraintValidatorContext context) {
        if (userProfileDtos == null) {
            return true; // @NotNull checks for null
        }

        for (UserProfileDto userProfileDto : userProfileDtos) {
            if (userProfileDto.getFlag()) {
                return true; // returns true if any user was selected
            }
        }
        return false;
    }
}
