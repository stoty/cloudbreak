package com.sequenceiq.cloudbreak.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.Range;

public class StackNameLengthValidator implements ConstraintValidator<ValidStackNameLength, String> {

    private static final Integer MIN_LENGTH = 5;

    private static final Integer MAX_LENGTH = 40;

    private final Integer minLength;

    private final Integer maxLength;

    public StackNameLengthValidator() {
        minLength = MIN_LENGTH;
        maxLength = MAX_LENGTH;    }

    public StackNameLengthValidator(Integer minLength, Integer maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Range.between(minLength, maxLength).contains(value.length());
    }
}
