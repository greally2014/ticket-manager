package com.greally2014.ticketmanager.validation.selected;

import com.greally2014.ticketmanager.dto.UserProfileDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SelectedValidator implements ConstraintValidator<Selected,
        List<UserProfileDto>> {
    @Override
    public boolean isValid(List<UserProfileDto> userProfileDtos, ConstraintValidatorContext context) {
        for (UserProfileDto userProfileDto : userProfileDtos) {
            if (userProfileDto.getFlag()) {
                return true;
            }
        }
        return false;
    }
}
