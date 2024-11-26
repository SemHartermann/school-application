package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.service.impl.LessonServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class LessonControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    LessonServiceImpl lessonService;

    @Test
    void givenValidId_whenGetLesson_thenStatusOkAndLessonDtoReturned() throws Exception {
        Long id = 5L;
        LessonDto lessonDto = LessonDto.builder()
                .id(id)
                .name("Test Lesson")
                .date(getFixedDateTime(1))
                .moduleId(1L)
                .period(1)
                .build();

        when(lessonService.getLesson(id)).thenReturn(Optional.of(lessonDto));

        ResultActions result = mockMvc.perform(get("/api/lessons/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        checkExpectedFields(result, lessonDto);
    }

    @Test
    void givenInvalidId_whenGetLesson_thenStatusNotFound() throws Exception {
        Long invalidId = 15000L;

        when(lessonService.getLesson(invalidId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/lessons/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detail").value("Lesson not found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidLessonDto_whenCreateLesson_thenStatusCreatedAndLessonDtoReturned() throws Exception {
        Long createdId = 3L;
        LessonDto requestDto = LessonDto.builder()
                .name("Test Lesson")
                .date(getFixedDateTime(0))
                .moduleId(1L)
                .period(1)
                .build();

        LessonDto responseDto = LessonDto.builder()
                .id(createdId)
                .name(requestDto.getName())
                .date(requestDto.getDate())
                .moduleId(requestDto.getModuleId())
                .period(requestDto.getPeriod())
                .build();

        when(lessonService.createLesson(any(LessonDto.class))).thenReturn(Optional.of(responseDto));
        ResultActions result = mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated());

        checkExpectedFields(result, responseDto);
    }

    @Test
    void givenInvalidLessonDto_whenCreateLesson_thenStatusBadRequest() throws Exception {
        LessonDto invalidDto = new LessonDto(); // LessonDto without required fields

        mockMvc.perform(post("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidLessonDto_whenUpdateLesson_thenStatusOkAndUpdatedLessonDtoReturned() throws Exception {
        LessonDto lessonDto = LessonDto.builder()
                .id(1L)
                .name("Test Lesson")
                .date(getFixedDateTime(0))
                .moduleId(1L)
                .period(1)
                .build();

        when(lessonService.updateLesson(any(LessonDto.class))).thenReturn(Optional.of(lessonDto));

        ResultActions result = mockMvc.perform(put("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(lessonDto)))
                .andExpect(status().isOk());

        checkExpectedFields(result, lessonDto);
    }

    @Test
    void givenValidLessonDtoWithInvalidId_whenUpdateLesson_thenStatusNotFound() throws Exception {
        Long invalidId = 15000L;
        LessonDto validDto = LessonDto.builder()
                .id(invalidId)
                .name("Valid Name")
                .date(getFixedDateTime(0))
                .moduleId(1L)
                .period(1)
                .build();

        when(lessonService.updateLesson(validDto)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validDto)))
                .andExpect(jsonPath("$.detail").value("Lesson not found! Can't perform update operation"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidId_whenDeleteLesson_thenStatusNoContent() throws Exception {
        Long id = 7L;

        mockMvc.perform(delete("/api/lessons/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(lessonService).deleteLesson(id);
    }

    @ParameterizedTest
    @MethodSource("provideModulesAndRanges")
    void givenModuleId_whenGetAllLessonsByModuleId_thenStatusOkAndLessonDtosReturned(Long moduleId, int startRange, int endRange) throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LessonDto> lessonDtos = IntStream.rangeClosed(startRange, endRange)
                .mapToObj(this::lessonDtoGenerator)
                .toList();

        Page<LessonDto> lessonDtoPage = new PageImpl<>(lessonDtos, pageable, lessonDtos.size());

        when(lessonService.findAllByModuleId(moduleId, pageable)).thenReturn(lessonDtoPage);

        ResultActions result = mockMvc.perform(get("/api/lessons/modules/" + moduleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(lessonDtos.size()));

        for (int i = 0; i < lessonDtos.size(); i++) {
            checkExpectedFieldsByObjectIndex(result, lessonDtos.get(i), "$.content[" + i + "]");
        }
    }

    static Stream<Arguments> provideModulesAndRanges() {
        return Stream.of(
                Arguments.of(1L, 1, 4), // Module 1, Lessons 1-4
                Arguments.of(2L, 5, 7)  // Module 2, Lessons 5-7
        );
    }

    @Test
    void givenInvalidModuleId_whenGetAllLessonsByModuleId_thenEmptyPageReturned() throws Exception {
        Long invalidModuleId = 15000L;
        Pageable pageable = PageRequest.of(0, 10);

        when(lessonService.findAllByModuleId(invalidModuleId, pageable)).thenReturn(Page.empty());

        mockMvc.perform(get("/api/lessons/modules/" + invalidModuleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideSubjectsAndRanges")
    void givenSubjectId_whenGetAllLessonsBySubjectId_thenStatusOkAndLessonDtosReturned(Long subjectId, int startRange, int endRange) throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<LessonDto> lessonDtos = IntStream.rangeClosed(startRange, endRange)
                .mapToObj(this::lessonDtoGenerator)
                .toList();

        Page<LessonDto> lessonDtoPage = new PageImpl<>(lessonDtos, pageable, lessonDtos.size());

        when(lessonService.findAllBySubjectId(subjectId, pageable)).thenReturn(lessonDtoPage);

        ResultActions result = mockMvc.perform(get("/api/lessons/subjects/" + subjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(lessonDtos.size()));

        for (int i = 0; i < lessonDtos.size(); i++) {
            checkExpectedFieldsByObjectIndex(result, lessonDtos.get(i), "$.content[" + i + "]");
        }
    }

    static Stream<Arguments> provideSubjectsAndRanges() {
        return Stream.of(
                Arguments.of(1L, 1, 7), // Subject 1, Lessons 1-7
                Arguments.of(2L, 8, 10)  // Subject 2, Lessons 8-10
        );
    }

    @Test
    void givenInvalidSubjectId_whenGetAllLessonsBySubjectId_thenEmptyPageReturned() throws Exception {
        Long invalidSubjectId = 15000L;
        Pageable pageable = PageRequest.of(0, 10);

        when(lessonService.findAllBySubjectId(invalidSubjectId, pageable)).thenReturn(Page.empty());

        mockMvc.perform(get("/api/lessons/subjects/" + invalidSubjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(status().isOk());
    }

    private LessonDto lessonDtoGenerator(Integer index) {
        Long module1Id = 1L;
        Long module2Id = 2L;
        LessonDto.LessonDtoBuilder builder = LessonDto.builder();

        return switch (index) {
            case 1 ->
                    builder.id(1L).name("Function. Function properties").date(getFixedDateTime(-1)).period(1).homework("Homework 1").moduleId(module1Id).build();
            case 2 ->
                    builder.id(2L).name("Zeros of functions, intervals of identity").date(getFixedDateTime(0)).period(2).homework("Homework 2").moduleId(module1Id).build();
            case 3 ->
                    builder.id(3L).name("The rise and fall of the function, the largest and smallest value of the function").date(getFixedDateTime(1)).period(3).homework("Homework 3").moduleId(module1Id).build();
            case 4 ->
                    builder.id(4L).name("Transformation of graphs of functions. Solving exercises").date(getFixedDateTime(2)).period(3).homework("Homework 4").moduleId(module1Id).build();
            case 5 ->
                    builder.id(5L).name("A system of two equations with two variables as a mathematical model of an applied problem").date(getFixedDateTime(45)).period(3).homework("Homework 5").moduleId(module2Id).build();
            case 6 ->
                    builder.id(6L).name("Quadratic inequality. Solving quadratic inequalities").date(getFixedDateTime(46)).period(3).homework("Homework 6").moduleId(module2Id).build();
            case 7 ->
                    builder.id(7L).name("Systems of two equations with two variables. Solving exercises").date(getFixedDateTime(47)).period(3).homework("Homework 7").moduleId(module2Id).build();
            case 8 ->
                    builder.id(8L).name("Concept of dispersed systems. Colloidal and true solutions").date(getFixedDateTime(15)).period(2).homework("Homework 8").moduleId(module1Id).build();
            case 9 ->
                    builder.id(9L).name("The structure of a water molecule, the concept of a hydrogen bond").date(getFixedDateTime(16)).period(2).homework("Homework 9").moduleId(module1Id).build();
            case 10 ->
                    builder.id(10L).name("The concept of crystal hydrates. The mass fraction of a solute in a solution").date(getFixedDateTime(17)).period(2).homework("Homework 10").moduleId(module1Id).build();
            default -> throw new IllegalArgumentException("There is no Lesson with index " + index);
        };
    }

    private void checkExpectedFields(ResultActions result, LessonDto dto) throws Exception {
        result.andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.date").value(formatZonedDateTimeToSeconds(dto.getDate())))
                .andExpect(jsonPath("$.period").value(dto.getPeriod()))
                .andExpect(jsonPath("$.homework").value(dto.getHomework()))
                .andExpect(jsonPath("$.moduleId").value(dto.getModuleId()));
    }

    private void checkExpectedFieldsByObjectIndex(ResultActions result, LessonDto dto, String jsonPathPrefix) throws Exception {
        result.andExpect(jsonPath(jsonPathPrefix + ".id").value(dto.getId()))
                .andExpect(jsonPath(jsonPathPrefix + ".name").value(dto.getName()))
                .andExpect(jsonPath(jsonPathPrefix + ".date").value(formatZonedDateTimeToSeconds(dto.getDate())))
                .andExpect(jsonPath(jsonPathPrefix + ".period").value(dto.getPeriod()))
                .andExpect(jsonPath(jsonPathPrefix + ".homework").value(dto.getHomework()))
                .andExpect(jsonPath(jsonPathPrefix + ".moduleId").value(dto.getModuleId()));
    }

    private ZonedDateTime getFixedDateTime(int daysOffset) {
        ZoneId zoneId = ZoneId.systemDefault();
        return ZonedDateTime.of(2023, 10, 16, 0, 0, 0, 0, zoneId)
                .plusDays(daysOffset)
                .truncatedTo(ChronoUnit.SECONDS);
    }

    public static String formatZonedDateTimeToSeconds(ZonedDateTime dateTime) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendPattern("XXX")
                .toFormatter();
        return dateTime.format(formatter);
    }

    private String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            JavaTimeModule timeModule = new JavaTimeModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

            timeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(formatter));
            mapper.registerModule(timeModule);

            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}