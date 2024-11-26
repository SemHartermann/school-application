package co.inventorsoft.academy.schoolapplication.util.validation.validator;

import co.inventorsoft.academy.schoolapplication.util.validation.ValidationResult;

public interface Validator<T> {
    void validate(T entity, Integer rowNumber, ValidationResult result);
}
