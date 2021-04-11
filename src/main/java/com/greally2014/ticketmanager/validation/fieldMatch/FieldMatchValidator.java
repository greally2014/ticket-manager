package com.greally2014.ticketmanager.validation.fieldMatch;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    public void initialize(FieldMatch constraint) {
        firstFieldName = constraint.first();
        secondFieldName = constraint.second();
        message = constraint.message();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean valid = true;
        try {
            Object firstObject = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
            Object secondObject = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

            valid = firstObject == null && secondObject == null ||
                    firstObject.toString().length() < 8 ||
                    firstObject.equals(secondObject);

        } catch (Exception placeholder) {
            // nothing
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
