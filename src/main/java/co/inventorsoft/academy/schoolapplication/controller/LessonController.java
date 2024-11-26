package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {
    LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/subjects/{subjectId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<LessonDto> getAllLessonsBySubjectId(@PathVariable Long subjectId, @PageableDefault Pageable pageable) {
        return lessonService.findAllBySubjectId(subjectId, pageable);
    }

    @GetMapping("/modules/{moduleId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<LessonDto> getAllLessonsByModuleId(@PathVariable Long moduleId, @PageableDefault Pageable pageable) {
        return lessonService.findAllByModuleId(moduleId, pageable);
    }

    @GetMapping("/{id}")
    public LessonDto getLesson(@PathVariable Long id) {
        return lessonService.getLesson(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<LessonDto> createLesson(@RequestBody @Valid LessonDto lessonDto) {
        return lessonService.createLesson(lessonDto);
    }

    @PutMapping
    public LessonDto updateLesson(@RequestBody @Valid LessonDto lessonDto) {
        return lessonService.updateLesson(lessonDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lesson not found! Can't perform update operation")
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
    }
}
