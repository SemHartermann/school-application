package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.Grade;
import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Page<Grade> getGradesByStudentId(Long studentId, Pageable pageable);

    Page<Grade> getGradesByLessonId(Long lessonId, Pageable pageable);

    Page<Grade> getGradesByLessonIdAndStudentId(Long lessonId, Long studentId, Pageable pageable);

    Optional<Grade> getGradeByLessonIdAndStudentIdAndGradeType(Long lessonId, Long studentId, GradeType gradeType);

    @Query("select g from Grade g where g.lesson.moduleId = :moduleId")
    Page<Grade> getGradesByModuleId(@Param("moduleId") Long moduleId, Pageable pageable);

    @Query("select g from Grade g where g.lesson.moduleId = :moduleId and g.student.id = :studentId")
    Page<Grade> getGradesByModuleIdAndStudentId(@Param("moduleId") Long moduleId,
                                                @Param("studentId") Long studentId,
                                                Pageable pageable);

    List<Grade> findByStudentIdAndLessonDate(Long studentId, LocalDate date);
}
