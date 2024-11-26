package co.inventorsoft.academy.schoolapplication.dto.subjectsperformance;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.YearMonth;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectsPerformanceRequestDto {
    @NotNull
    YearMonth from;

    @NotNull
    YearMonth to;
}
