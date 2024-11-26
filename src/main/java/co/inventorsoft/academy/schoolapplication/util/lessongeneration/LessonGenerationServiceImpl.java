package co.inventorsoft.academy.schoolapplication.util.lessongeneration;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import co.inventorsoft.academy.schoolapplication.service.ModuleService;
import co.inventorsoft.academy.schoolapplication.service.SubjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonGenerationServiceImpl implements LessonGenerationService{
    ModuleService moduleService;
    LessonService lessonService;
    SubjectService subjectService;

    public List<LessonDto> generateNextWeekLessonsByModuleSchedule(ZonedDateTime startOfWeek, ZonedDateTime endOfWeek) {
        Set<ModuleDto> modulesForNextWeek = moduleService.findModulesForNextWeek(startOfWeek, endOfWeek);

        List<LessonDto> lessonsForNextWeek = new ArrayList<>();

        modulesForNextWeek
                .forEach(moduleDto -> moduleDto
                        .getSchedule()
                        .forEach((lessonDayOfWeek, periods) -> periods
                                .forEach(period -> lessonsForNextWeek.add(lessonService.createLesson(
                                        LessonDto.builder()
                                                .moduleId(moduleDto.getId())
                                                .date(startOfWeek.plusDays(lessonDayOfWeek.getValue() - 1))
                                                .name(subjectService.getSubjectById(moduleDto.getSubjectId()).getName())
                                                .period(period)
                                                .build()).orElseThrow()))));

        return lessonsForNextWeek;
    }
}

