package co.inventorsoft.academy.schoolapplication.util.excepion;

import co.inventorsoft.academy.schoolapplication.util.validation.ValidationResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ModelValidationException extends RuntimeException {
    private final ValidationResult validationResult;
}