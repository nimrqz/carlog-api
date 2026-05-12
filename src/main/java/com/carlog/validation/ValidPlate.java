package com.carlog.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlate {

    String message() default "Placa invalida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
