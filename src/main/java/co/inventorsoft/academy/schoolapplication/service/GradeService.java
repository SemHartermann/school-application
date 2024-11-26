package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface GradeService {
    @Transactional
    GradeDto addGrade(GradeDto gradeDto);

    Grade addModuleGrade(Long studentId, Long moduleId, Pageable pageable);

    @Transactional(readOnly = true)
    GradeDto getGradeById(Long gradeId);

    @Transactional(readOnly = true)
    Page<GradeDto> getGradesByStudentId(Long studentId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<GradeDto> getGradesByLessonId(Long lessonId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<GradeDto> getGradesByModuleId(Long moduleId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<GradeDto> getGradesByModuleIdAndStudentId(Long moduleId, Long studentId, Pageable pageable);

    @Transactional(readOnly = true)
    Page<GradeDto> getGradesByLessonIdAndStudentId(Long lessonId, Long studentId, Pageable pageable);

    @Transactional(readOnly = true)
    List<GradeDto> getGradesByStudentIdAndDate(Long studentId, LocalDate date);

    @Transactional
    GradeDto updateGrade(Long gradeId, GradeDto gradeDto);

    @Transactional
    void deleteGrade(Long gradeId);
}
