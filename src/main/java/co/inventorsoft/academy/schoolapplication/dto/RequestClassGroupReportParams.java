package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestClassGroupReportParams {
    @NotNull(message = "group id can`t be null")
    Long classGroupId;
    @NotNull(message = "subject id can`t be null")
    Long subjectId;
    @NotNull(message = "start date can`t be null")
    LocalDate startDate;
    @NotNull(message = "end date can`t be null")
    LocalDate endDate;
}
