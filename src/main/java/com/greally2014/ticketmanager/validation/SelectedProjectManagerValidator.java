package com.greally2014.ticketmanager.validation;

import com.greally2014.ticketmanager.formModel.ProfileFormUser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class SelectedProjectManagerValidator implements ConstraintValidator<SelectedProjectManager,
        List<ProfileFormUser>> {
    @Override
    public boolean isValid(List<ProfileFormUser> profileFormUsers, ConstraintValidatorContext context) {
        for (ProfileFormUser profileFormUser : profileFormUsers) {
            if (profileFormUser.getFlag()) {
                return true;
            }
        }
        return false;
    }
}
