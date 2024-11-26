package co.inventorsoft.academy.schoolapplication.dto;

import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GradeDto {

    @NotNull(message = "studentId is mandatory!")
    Long studentId;

    @NotNull(message = "lessonId is mandatory!")
    Long lessonId;

    @NotNull(message = "gradeValue is mandatory!")
    @Min(value = 1, message = "gradeValue can't be less than 1")
    @Max(value = 12, message = "gradeValue can't be greater than 12")
    Integer gradeValue;

    @NotNull(message = "gradeType is mandatory!")
    GradeType gradeType;

    ZonedDateTime gradeDate;
}
