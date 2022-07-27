package com.example.springbootbase.validation;

import com.example.springbootbase.validation.constraints.NotNullIfFalse;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static com.example.springbootbase.utility.ReflectionUtility.getFieldValue;

/**
 * A class level validator used to validate that the values of provided fields
 * are not null if value of the target field is set to false.
 */
public class NotNullIfFalseValidator implements ConstraintValidator<NotNullIfFalse, Object> {
    private List<String> fields;
    private String targetField;

    @Override
    public void initialize(NotNullIfFalse constraintAnnotation) {
        fields = Arrays.asList(constraintAnnotation.fields());
        targetField = constraintAnnotation.targetField();
    }

    @Override
    public boolean isValid(Object validatedObject, ConstraintValidatorContext context) {
        if (validatedObject == null) {
            return true;
        }

        if ((Boolean) getFieldValue(targetField, validatedObject)) {
            return true;
        }

        for (String field : fields) {
            if (getFieldValue(field, validatedObject) == null) {
                return false;
            }
        }

        return true;
    }
}
