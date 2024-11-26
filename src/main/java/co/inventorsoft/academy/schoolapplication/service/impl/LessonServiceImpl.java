package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.mapper.LessonMapper;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonServiceImpl implements LessonService {
    LessonRepository lessonRepository;
    LessonMapper lessonMapper;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    @Override
    @Transactional
    public Optional<LessonDto> createLesson(LessonDto dto) {
        Lesson lesson = lessonMapper.toLesson(dto);
        Lesson savedLesson = lessonRepository.save(lesson);

        return Optional.ofNullable(lessonMapper.toDto(savedLesson));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LessonDto> getLesson(Long id) {
        return lessonRepository.findById(id).map(lessonMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<LessonDto> updateLesson(LessonDto dto) {
        return lessonRepository.findById(dto.getId())
                .map(lesson -> {
                    lessonMapper.updateLessonFromDto(dto, lesson);
                    Lesson updatedLesson = lessonRepository.save(lesson);

                    return lessonMapper.toDto(updatedLesson);
                });
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LessonDto> findAllBySubjectId(Long subjectId, Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findAllBySubjectId(subjectId, pageable);

        return lessons.map(lessonMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LessonDto> findAllByModuleId(Long moduleId, Pageable pageable) {
        Page<Lesson> lessons = lessonRepository.findAllByModuleId(moduleId, pageable);

        return lessons.map(lessonMapper::toDto);
    }
}