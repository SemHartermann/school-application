package co.inventorsoft.academy.schoolapplication.util.validation.validator;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.util.validation.ValidationResult;
import co.inventorsoft.academy.schoolapplication.util.validation.ValidationUtil;

public class SubjectValidator implements Validator<SubjectDto> {
    private static final String INVALID_SUBJECT_PATTERN_NAME = "^[a-zа-я].*";

    @Override
    public void validate(SubjectDto entity, Integer rowNumber, ValidationResult result) {
        String subjectName = ValidationUtil.trimField(entity.getName());
        ValidationUtil.validateField(subjectName, INVALID_SUBJECT_PATTERN_NAME, "name", rowNumber, result);
    }
}
