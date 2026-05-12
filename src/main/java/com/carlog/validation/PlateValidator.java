package com.carlog.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PlateValidator implements ConstraintValidator<ValidPlate, String> {

    // Aceita formatos: AAA-0000, AAA0000, AAA0A00 (Mercosul)
    private static final Pattern PLATE_PATTERN = Pattern.compile(
            "^[A-Z]{3}[-]?[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$"
    );

    @Override
    public boolean isValid(String plate, ConstraintValidatorContext context) {
        if (plate == null || plate.isBlank()) {
            return true; // Deixa o @NotBlank tratar
        }
        String normalized = plate.trim().toUpperCase();
        return PLATE_PATTERN.matcher(normalized).matches();
    }
}
