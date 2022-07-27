package com.example.springbootbase.validation;

import com.example.springbootbase.validation.constraints.ValuesEqual;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static com.example.springbootbase.utility.ReflectionUtility.getFieldValue;

/**
 * A class level validator used to validate that provided fields
 * in the validated object have equal values.
 */
public class ValuesEqualValidator implements ConstraintValidator<ValuesEqual, Object> {
    private List<String> fields;

    @Override
    public void initialize(ValuesEqual constraintAnnotation) {
        fields = Arrays.asList(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object validatedObject, ConstraintValidatorContext context) {
        if (validatedObject == null) {
            return true;
        }

        return fields.stream()
                .map(field -> getFieldValue(field, validatedObject))
                .distinct()
                .count() == 1;
    }
}
