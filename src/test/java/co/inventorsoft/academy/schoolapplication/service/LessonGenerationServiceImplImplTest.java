package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import co.inventorsoft.academy.schoolapplication.mapper.LessonMapper;
import co.inventorsoft.academy.schoolapplication.mapper.LessonMapperImpl;
import co.inventorsoft.academy.schoolapplication.mapper.ModuleMapper;
import co.inventorsoft.academy.schoolapplication.mapper.ModuleMapperImpl;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import co.inventorsoft.academy.schoolapplication.service.ModuleService;
import co.inventorsoft.academy.schoolapplication.service.SubjectService;
import co.inventorsoft.academy.schoolapplication.util.lessongeneration.LessonGenerationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonGenerationServiceImplImplTest {

    @Mock
    private ModuleService moduleService;

    @Mock
    private LessonService lessonService;

    @Mock
    private SubjectService subjectService;

    @InjectMocks
    private LessonGenerationServiceImpl lessonGenerationService;

    @Test
    void generateNextWeekLessonsByModuleSchedule() {
        ModuleMapper moduleMapper = new ModuleMapperImpl();
        LessonMapper lessonMapper = new LessonMapperImpl();

        ZonedDateTime startOfWeek = ZonedDateTime.now();
        ZonedDateTime endOfWeek = startOfWeek.plusDays(5);

        Set<ModuleDto> modulesForNextWeek = new HashSet<>();

        for (int i = 1; i <= 5; i++) {
            Module module = new Module();
            module.setSubjectId((long) i);
            module.setClassRoomId((long) i);
            module.setTeacherId((long) i);
            module.setName("Module" + i);
            module.setStartDate(ZonedDateTime.now().minusDays(1));
            module.setEndDate(ZonedDateTime.now().plusDays(6));
            module.setDeleted(false);

            module.setSchedule(Map.of(DayOfWeek.MONDAY, new HashSet<>(Arrays.asList(1, 3, 5)),
                    DayOfWeek.WEDNESDAY, new HashSet<>(Arrays.asList(2, 4))));

            modulesForNextWeek.add(moduleMapper.toDto(module));
        }

        when(lessonService.createLesson(any(LessonDto.class))).thenAnswer((Answer<Optional<LessonDto>>)
                invocation -> Optional.of(invocation.getArgument(0)));

        when(moduleService.findModulesForNextWeek(any(), any())).thenReturn(modulesForNextWeek);

        when(subjectService.getSubjectById(any())).thenReturn(new SubjectDto(1L,"Math"));

        List<LessonDto> expectedLessonDtos = new ArrayList<>();

        modulesForNextWeek
                .forEach(moduleDto -> moduleDto
                        .getSchedule()
                        .forEach((lessonDayOfWeek, periods) -> periods
                                .forEach(period -> expectedLessonDtos.add(lessonService.createLesson(
                                        LessonDto.builder()
                                                .moduleId(moduleDto.getId())
                                                .date(startOfWeek.plusDays(lessonDayOfWeek.getValue() - 1L))
                                                .name("Math")
                                                .period(period)
                                                .build()).get()))));

        List<Lesson> expectedLessonEntities = lessonMapper.listOfLessonDtosToListOfLessons(expectedLessonDtos);

        List<LessonDto> actualLessonDtos = lessonGenerationService.generateNextWeekLessonsByModuleSchedule(startOfWeek, endOfWeek);

        List<Lesson> actualLessonEntities = lessonMapper.listOfLessonDtosToListOfLessons(actualLessonDtos);

        assertThat(actualLessonEntities).isNotEmpty();
        assertThat(actualLessonEntities).isEqualTo(expectedLessonEntities);
    }
}