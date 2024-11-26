package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.mapper.LessonMapper;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.LessonServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class LessonServiceImplTest {
    @Mock
    LessonRepository lessonRepository;
    @Mock
    LessonMapper lessonMapper;
    @InjectMocks
    LessonServiceImpl lessonService;

    @Test
    void whenCreateLessonWithValidData_thenExpectedLessonDtoIsReturned() {
        LessonDto dto = LessonDto.builder()
                .name("Differential equations")
                .date(ZonedDateTime.now())
                .period(2)
                .homework("Some homework details")
                .moduleId(1L)
                .build();

        Lesson lesson = new Lesson();
        Lesson savedLesson = new Lesson();

        when(lessonMapper.toLesson(dto)).thenReturn(lesson);
        when(lessonRepository.save(lesson)).thenReturn(savedLesson);
        when(lessonMapper.toDto(savedLesson)).thenReturn(dto);

        Optional<LessonDto> resultDto = lessonService.createLesson(dto);

        assertThat(resultDto).isPresent().contains(dto);

        verify(lessonRepository).save(lesson);
        verify(lessonMapper).toLesson(dto);
        verify(lessonMapper).toDto(savedLesson);
    }

    @Test
    void whenCreateLessonWithInvalidData_thenThrowException() {
        LessonDto incorrectInputDto = LessonDto.builder()
                .name(null) //exception related field
                .date(ZonedDateTime.now())
                .period(2)
                .homework("Some homework details")
                .moduleId(1L)
                .build();

        when(lessonMapper.toLesson(incorrectInputDto)).thenThrow(new IllegalArgumentException("Invalid data"));

        assertThrows(IllegalArgumentException.class, () -> lessonService.createLesson(incorrectInputDto));
    }

    @Test
    void whenGetLessonWithExistingId_thenExpectedLessonDtoIsReturned() {
        Long id = 1L;
        Lesson lesson = new Lesson();
        lesson.setId(id);

        LessonDto dto = new LessonDto();

        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));
        when(lessonMapper.toDto(lesson)).thenReturn(dto);

        Optional<LessonDto> result = lessonService.getLesson(id);

        assertThat(result).isPresent().contains(dto);

        verify(lessonRepository).findById(id);
        verify(lessonMapper).toDto(lesson);
    }

    @Test
    void givenInvalidId_whenGetLesson_thenResultEmpty() {
        Long invalidId = 15000L;

        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<LessonDto> result = lessonService.getLesson(invalidId);

        assertThat(result).isEmpty();
        verify(lessonRepository).findById(invalidId);
    }

    @Test
    void whenUpdateLessonWithExistingId_thenUpdatedLessonDtoIsReturned() {
        LessonDto dto = LessonDto.builder().id(5L).build();

        Lesson lesson = new Lesson();
        Lesson updatedLesson = new Lesson();
        updatedLesson.setId(5L);

        when(lessonRepository.findById(dto.getId())).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(lesson)).thenReturn(updatedLesson);
        when(lessonMapper.toDto(updatedLesson)).thenReturn(dto);

        Optional<LessonDto> resultDto = lessonService.updateLesson(dto);

        assertThat(resultDto).isPresent().contains(dto);

        verify(lessonRepository).findById(dto.getId());
        verify(lessonMapper).updateLessonFromDto(dto, lesson);
        verify(lessonRepository).save(lesson);
        verify(lessonMapper).toDto(updatedLesson);
    }

    @Test
    void givenValidLessonDtoWithInvalidId_whenUpdateLesson_thenResultEmpty() {
        Long invalidId = 15000L;
        LessonDto validDto = LessonDto.builder()
                .id(invalidId)
                .name("Test Lesson")
                .date(ZonedDateTime.now())
                .period(1)
                .moduleId(1L)
                .build();

        when(lessonRepository.findById(invalidId)).thenReturn(Optional.empty());

        Optional<LessonDto> result = lessonService.updateLesson(validDto);

        assertThat(result).isEmpty();
        verify(lessonRepository).findById(invalidId);
        verify(lessonMapper, never()).updateLessonFromDto(any(LessonDto.class), any(Lesson.class));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void whenDeleteLessonWithValidId_thenRepositoryDeleteByIdIsCalled() {
        Long id = 7L;
        lessonService.deleteLesson(id);

        verify(lessonRepository).deleteById(id);
    }

    @ParameterizedTest
    @MethodSource("provideModules")
    void whenGetAllLessonsByModuleId_thenCorrectLessonDtosReturned(Long moduleId, int startRange, int endRange, int expectedSize) {
        List<Lesson> lessons = IntStream.rangeClosed(startRange, endRange)
                .mapToObj(this::lessonGenerator)
                .toList();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> page = new PageImpl<>(lessons, pageable, lessons.size());

        when(lessonRepository.findAllByModuleId(moduleId, pageable)).thenReturn(page);
        when(lessonMapper.toDto(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson lesson = invocation.getArgument(0);
            LessonDto dto = new LessonDto();
            dto.setName(lesson.getName());
            return dto;
        });

        Page<LessonDto> result = lessonService.findAllByModuleId(moduleId, pageable);

        assertThat(result.getContent())
                .hasSize(expectedSize)
                .extracting("name")
                .containsExactlyElementsOf(lessons.stream().map(Lesson::getName).toList());
    }

    static Stream<Arguments> provideModules() {
        return Stream.of(
                Arguments.of(1L, 1, 4, 4), // Math, Module 1
                Arguments.of(2L, 5, 7, 3)  // Math, Module 2
        );
    }

    @Test
    void whenGetAllLessonsByModuleIdWithEmptyRepository_thenEmptyPageReturned() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Long moduleId = 1L;

        when(lessonRepository.findAllByModuleId(moduleId, pageable)).thenReturn(emptyPage);

        Page<LessonDto> result = lessonService.findAllByModuleId(moduleId, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideSubjects")
    void whenGetAllLessonsBySubjectId_thenCorrectLessonDtosReturned(Long subjectId, int startRange, int endRange, int expectedSize) {
        List<Lesson> lessons = IntStream.rangeClosed(startRange, endRange)
                .mapToObj(this::lessonGenerator)
                .toList();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> page = new PageImpl<>(lessons, pageable, lessons.size());

        when(lessonRepository.findAllBySubjectId(subjectId, pageable)).thenReturn(page);
        when(lessonMapper.toDto(any(Lesson.class))).thenAnswer(invocation -> {
            Lesson lesson = invocation.getArgument(0);
            LessonDto dto = new LessonDto();
            dto.setName(lesson.getName());
            return dto;
        });

        Page<LessonDto> result = lessonService.findAllBySubjectId(subjectId, pageable);

        assertThat(result.getContent())
                .hasSize(expectedSize)
                .extracting("name")
                .containsExactlyElementsOf(lessons.stream().map(Lesson::getName).toList());
    }

    static Stream<Arguments> provideSubjects() {
        return Stream.of(
                Arguments.of(1L, 1, 7, 7), // Subject 1: Math
                Arguments.of(2L, 8, 10, 3)  // Subject 2: Chemistry
        );
    }

    @Test
    void whenGetAllLessonsBySubjectIdWithEmptyRepository_thenEmptyPageReturned() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Long subjectId = 1L;

        when(lessonRepository.findAllBySubjectId(subjectId, pageable)).thenReturn(emptyPage);

        Page<LessonDto> result = lessonService.findAllBySubjectId(subjectId, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    private Lesson lessonGenerator(Integer index) {
        Long module1Id = 1L;
        Long module2Id = 2L;

        return switch (index) {
            //Subject (id = 1): "Math"; module (id = 1): "Quadratic function. Function properties. Transform graphs of functions"
            case 1 -> lessonBuilder(1L, "Function. Function properties", ZonedDateTime.now().minusDays(1), 1, "Homework 1", module1Id);
            case 2 -> lessonBuilder(2L, "Zeros of functions, intervals of identity", ZonedDateTime.now(), 2, "Homework 2", module1Id);
            case 3 -> lessonBuilder(3L, "The rise and fall of the function, the largest and smallest value of the function", ZonedDateTime.now().plusDays(1), 3, "Homework 3", module1Id);
            case 4 -> lessonBuilder(4L, "Transformation of graphs of functions. Solving exercises", ZonedDateTime.now().plusDays(2), 3, "Homework 4", module1Id);

            //Subject (id = 1): "Math"; module (id = 2): "Quadratic inequality. A system of two equations with two variables"
            case 5 -> lessonBuilder(5L, "A system of two equations with two variables as a mathematical model of an applied problem", ZonedDateTime.now().plusDays(45), 3, "Homework 5", module2Id);
            case 6 -> lessonBuilder(6L, "Quadratic inequality. Solving quadratic inequalities", ZonedDateTime.now().plusDays(46), 3, "Homework 6", module2Id);
            case 7 -> lessonBuilder(7L, "Systems of two equations with two variables. Solving exercises", ZonedDateTime.now().plusDays(47), 3, "Homework 7", module2Id);

            //Subject (id = 2): "Chemistry"; module (id = 1): "Solutions"
            case 8 -> lessonBuilder(8L, "Concept of dispersed systems. Colloidal and true solutions", ZonedDateTime.now().plusDays(15), 2, "Homework 8", module1Id);
            case 9 -> lessonBuilder(9L, "The structure of a water molecule, the concept of a hydrogen bond", ZonedDateTime.now().plusDays(16), 2, "Homework 9", module1Id);
            case 10 -> lessonBuilder(10L, "The concept of crystal hydrates. The mass fraction of a solute in a solution", ZonedDateTime.now().plusDays(17), 2, "Homework 10", module1Id);
            default -> throw new IllegalArgumentException("There is no Lesson with index " + index);
        };
    }

    private Lesson lessonBuilder(Long id, String name, ZonedDateTime date, int period, String homework, Long moduleId) {
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setName(name);
        lesson.setDate(date);
        lesson.setPeriod(period);
        lesson.setHomework(homework);
        lesson.setModuleId(moduleId);
        return lesson;
    }
}