package co.inventorsoft.academy.schoolapplication.util.lessongeneration;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;

import java.time.ZonedDateTime;
import java.util.List;

public interface LessonGenerationService {
    List<LessonDto> generateNextWeekLessonsByModuleSchedule(ZonedDateTime startOfWeek, ZonedDateTime endOfWeek);
}
