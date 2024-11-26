package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformance;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static co.inventorsoft.academy.schoolapplication.model.Tables.*;
import static org.jooq.impl.DSL.round;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.field;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectsPerformanceRepository {
    Field<String> YEAR_MONTH = field("TO_CHAR({0}, 'YYYY-MM')", SQLDataType.VARCHAR, LESSONS.DATE);
    DSLContext dslContext;

    @Autowired
    public SubjectsPerformanceRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public List<SubjectsPerformance> findByStudentIdAndYearMonthRange(Long studentId, YearMonth startYearMonth, YearMonth endYearMonth) {
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = endYearMonth.atEndOfMonth();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return dslContext
                .select(
                        SUBJECTS.NAME.as("subject_name"),
                        MODULES.SUBJECT_ID,
                        YEAR_MONTH.as("year_month"),
                        round(avg(GRADES.GRADE_VALUE), 1).as("average_grade")
                )
                .from(GRADES)
                .innerJoin(LESSONS).on(GRADES.LESSON_ID.eq(LESSONS.ID))
                .innerJoin(MODULES).on(LESSONS.MODULE_ID.eq(MODULES.ID))
                .innerJoin(SUBJECTS).on(MODULES.SUBJECT_ID.eq(SUBJECTS.ID))
                .where(GRADES.STUDENT_ID.eq(studentId))
                .and(LESSONS.DATE.between(startDateTime, endDateTime))
                .groupBy(MODULES.SUBJECT_ID, YEAR_MONTH, SUBJECTS.NAME)
                .orderBy(MODULES.SUBJECT_ID.asc(), YEAR_MONTH.asc())
                .fetch()
                .map(record -> {
                    Long subjectId = record.getValue(MODULES.SUBJECT_ID);
                    String subjectName = record.get("subject_name", String.class);
                    YearMonth yearMonth = YearMonth.parse(record.get("year_month", String.class));
                    Double averageGrade = record.get("average_grade", Double.class);

                    return new SubjectsPerformance(subjectName, subjectId, yearMonth, averageGrade);
                });
    }
}