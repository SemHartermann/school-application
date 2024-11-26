package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.util.lessongeneration.LessonGenerationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/generation")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonGenerationController {
    LessonGenerationService lessonGenerationService;

    @PostMapping("/lesson")
    @ResponseStatus(HttpStatus.OK)
    public void triggerLessonGenerationJob() {
        lessonGenerationService.generateNextWeekLessonsByModuleSchedule(
                ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(4L));
    }
}