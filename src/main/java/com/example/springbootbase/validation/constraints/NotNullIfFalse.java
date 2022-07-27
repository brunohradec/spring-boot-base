package com.example.springbootbase.validation.constraints;

import com.example.springbootbase.validation.ValuesEqualValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesEqualValidator.class)
@Repeatable(NotNullIfFalse.List.class)
@Documented
public @interface NotNullIfFalse {
    String[] fields() default {""};
    String targetField() default "";

    String message() default "Provided fields must not be null if target field is set to false";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        NotNullIfFalse[] value();
    }
}
