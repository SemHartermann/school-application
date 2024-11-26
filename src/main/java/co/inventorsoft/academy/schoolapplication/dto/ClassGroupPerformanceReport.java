package co.inventorsoft.academy.schoolapplication.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupPerformanceReport {
    Long id;
    String student;
    String classGroup;
    String subject;
    BigDecimal averageGrade;
}
