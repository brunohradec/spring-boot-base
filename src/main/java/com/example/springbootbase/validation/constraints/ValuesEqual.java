package com.example.springbootbase.validation.constraints;

import com.example.springbootbase.validation.ValuesEqualValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesEqualValidator.class)
@Repeatable(ValuesEqual.List.class)
@Documented
public @interface ValuesEqual {
    String[] fields() default {""};

    String message() default "Provided fields must have equal values";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        ValuesEqual[] value();
    }
}
