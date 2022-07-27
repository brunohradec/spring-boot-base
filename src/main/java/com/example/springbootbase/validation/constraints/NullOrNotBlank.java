package com.example.springbootbase.validation.constraints;

import com.example.springbootbase.validation.NullOrNotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Documented
public @interface NullOrNotBlank {
    String message() default "Provided field must be null or not blank";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}
