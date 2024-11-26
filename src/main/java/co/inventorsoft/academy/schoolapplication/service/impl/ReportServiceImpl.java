package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.ClassGroupPerformanceReport;
import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupReportParams;
import co.inventorsoft.academy.schoolapplication.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static co.inventorsoft.academy.schoolapplication.model.Tables.CLASS_GROUP;
import static co.inventorsoft.academy.schoolapplication.model.Tables.CLASS_GROUP_STUDENTS;
import static co.inventorsoft.academy.schoolapplication.model.Tables.CLASS_GROUP_SUBJECT;
import static co.inventorsoft.academy.schoolapplication.model.Tables.GRADES;
import static co.inventorsoft.academy.schoolapplication.model.Tables.LESSONS;
import static co.inventorsoft.academy.schoolapplication.model.Tables.MODULES;
import static co.inventorsoft.academy.schoolapplication.model.Tables.STUDENTS;
import static co.inventorsoft.academy.schoolapplication.model.Tables.SUBJECTS;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.concat;
import static org.jooq.impl.DSL.inline;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImpl implements ReportService {

    DSLContext context;

    @Override
    public List<ClassGroupPerformanceReport> getClassGroupPerformance(RequestClassGroupReportParams groupPerformance) {
        LocalDate startDate = groupPerformance.getStartDate();
        LocalDate endDate = groupPerformance.getEndDate();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return context.select(
                        STUDENTS.ID,
                        concat(STUDENTS.FIRST_NAME, inline(" "), STUDENTS.LAST_NAME).as("student"),
                        CLASS_GROUP.NAME.as("classGroup"),
                        SUBJECTS.NAME.as("subject"),
                        avg(GRADES.GRADE_VALUE).as("averageGrade"))
                .from(CLASS_GROUP_STUDENTS)
                .join(STUDENTS).on(CLASS_GROUP_STUDENTS.STUDENTS_ID.eq(STUDENTS.ID))
                .join(CLASS_GROUP).on(CLASS_GROUP_STUDENTS.CLASS_GROUP_ID.eq(CLASS_GROUP.ID))
                .join(CLASS_GROUP_SUBJECT).on(CLASS_GROUP_STUDENTS.CLASS_GROUP_ID.eq(CLASS_GROUP_SUBJECT.CLASS_GROUP_ID))
                .join(SUBJECTS).on(CLASS_GROUP_SUBJECT.CLASS_GROUP_ID.eq(SUBJECTS.ID))
                .join(MODULES).on(SUBJECTS.ID.eq(MODULES.SUBJECT_ID))
                .join(LESSONS).on(MODULES.ID.eq(LESSONS.MODULE_ID))
                .join(GRADES).on(LESSONS.ID.eq(GRADES.LESSON_ID))
                .and(GRADES.STUDENT_ID.eq(STUDENTS.ID))
                .where(CLASS_GROUP_STUDENTS.CLASS_GROUP_ID.eq(groupPerformance.getClassGroupId()))
                .and(CLASS_GROUP_SUBJECT.SUBJECT_ID.eq(groupPerformance.getSubjectId()))
                .and(LESSONS.DATE.between(startDateTime, endDateTime))
                .groupBy(
                        STUDENTS.ID,
                        concat(STUDENTS.FIRST_NAME, inline(" "), STUDENTS.LAST_NAME),
                        CLASS_GROUP.NAME,
                        SUBJECTS.NAME
                ).fetchInto(ClassGroupPerformanceReport.class);
    }
}