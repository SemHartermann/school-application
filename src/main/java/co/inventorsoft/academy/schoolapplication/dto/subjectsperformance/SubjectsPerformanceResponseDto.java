package co.inventorsoft.academy.schoolapplication.dto.subjectsperformance;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectsPerformanceResponseDto {
    List<SubjectsPerformance> subjectPerformances;
}
