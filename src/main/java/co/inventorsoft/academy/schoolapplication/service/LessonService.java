package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LessonService {
    Optional<LessonDto> getLesson(Long id);
    Optional<LessonDto> createLesson(LessonDto dto);
    Optional<LessonDto> updateLesson(LessonDto dto);
    void deleteLesson(Long id);
    Page<LessonDto> findAllBySubjectId(Long subjectId, Pageable pageable);
    Page<LessonDto> findAllByModuleId(Long moduleId, Pageable pageable);
}
