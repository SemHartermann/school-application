package co.inventorsoft.academy.schoolapplication.dto.subjectsperformance;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectsPerformance {
    String subjectName;
    Long subjectId;
    YearMonth yearMonth;
    Double averageGrade;
}
