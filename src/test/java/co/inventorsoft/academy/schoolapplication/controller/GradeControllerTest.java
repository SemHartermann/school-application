package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import co.inventorsoft.academy.schoolapplication.service.impl.GradeServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FieldDefaults(level = AccessLevel.PRIVATE)
class GradeControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @MockBean
    GradeServiceImpl gradeService;

    @BeforeAll
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void addGradeTest() throws Exception {
        GradeDto gradeDto = new GradeDto(1L, 1L, 10, GradeType.TEST, ZonedDateTime.now());

        when(gradeService.addGrade(any(GradeDto.class))).thenReturn(gradeDto);

        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeDto)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.studentId").value(gradeDto.getStudentId()))
                .andExpect(jsonPath("$.lessonId").value(gradeDto.getLessonId()))
                .andExpect(jsonPath("$.gradeValue").value(gradeDto.getGradeValue()))
                .andExpect(jsonPath("$.gradeType").value(gradeDto.getGradeType().toString()));

        verify(gradeService, times(1)).addGrade(any(GradeDto.class));
    }

    @Test
    void addModuleGradeTest() throws Exception {
        Long studentId = 1L;
        Long moduleId = 2L;

        Module module = new Module();
        module.setEndDate(ZonedDateTime.of(2023, 12, 1, 8, 0, 0, 0, ZoneId.of("Europe/Kiev")));

        Student student = new Student();
        student.setId(studentId);

        Lesson lesson1 = new Lesson();
        lesson1.setModuleId(moduleId);
        Lesson lesson2 = new Lesson();
        lesson2.setModuleId(moduleId);
        Lesson lesson3 = new Lesson();
        lesson3.setModuleId(moduleId);

        List<Grade> gradeList = List.of(
                new Grade(student, lesson1, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(student, lesson2, 7, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(student, lesson3, 9, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Grade moduleGrade = new Grade(student, null, 9, GradeType.MODULE, module.getEndDate());

        when(gradeService.addModuleGrade(moduleId, studentId, pageable)).thenReturn(moduleGrade);

        mockMvc.perform(post("/grades/module/" + moduleId + "/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moduleGrade)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.studentId").value(moduleGrade.getStudent().getId()))
                .andExpect(jsonPath("$.gradeValue").value(moduleGrade.getGradeValue()))
                .andExpect(jsonPath("$.gradeType").value(moduleGrade.getGradeType().toString()));

        verify(gradeService, times(1)).addGrade(any(GradeDto.class));
    }

    @Test
    void getGradeByIdTest() throws Exception {
        Long gradeId = 1L;
        GradeDto gradeDto = new GradeDto(1L, 1L, 10, GradeType.TEST, ZonedDateTime.now());

        when(gradeService.getGradeById(gradeId)).thenReturn(gradeDto);

        mockMvc.perform(get("/api/grades/" + gradeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.studentId").value(gradeDto.getStudentId()))
                .andExpect(jsonPath("$.lessonId").value(gradeDto.getLessonId()))
                .andExpect(jsonPath("$.gradeValue").value(gradeDto.getGradeValue()))
                .andExpect(jsonPath("$.gradeType").value(gradeDto.getGradeType().toString()));

        verify(gradeService, times(1)).getGradeById(gradeId);
    }

    @Test
    void getAllGradesByStudentIdTest() throws Exception {
        Long studentId = 1L;
        Long lessonId = 0L;
        List<GradeDto> gradeDtoList = List.of(
                new GradeDto(studentId, ++lessonId, 10, GradeType.TEST, ZonedDateTime.now()),
                new GradeDto(studentId, ++lessonId, 11, GradeType.MINI_TEST, ZonedDateTime.now()),
                new GradeDto(studentId, ++lessonId, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );
        Pageable pageable = Pageable.ofSize(gradeDtoList.size());
        Page<GradeDto> gradesPage = new PageImpl<>(gradeDtoList, pageable, gradeDtoList.size());

        when(gradeService.getGradesByStudentId(eq(studentId), any(Pageable.class))).thenReturn(gradesPage);

        mockMvc.perform(get("/api/students/" + studentId + "/grades"))
                .andExpect(jsonPath("$.content", hasSize(gradeDtoList.size())))

                .andExpect(jsonPath("$.content[0].studentId").value(gradeDtoList.get(0).getStudentId()))
                .andExpect(jsonPath("$.content[0].lessonId").value(gradeDtoList.get(0).getLessonId()))
                .andExpect(jsonPath("$.content[0].gradeValue").value(gradeDtoList.get(0).getGradeValue()))
                .andExpect(jsonPath("$.content[0].gradeType").value(gradeDtoList.get(0).getGradeType().toString()))

                .andExpect(jsonPath("$.content[1].studentId").value(gradeDtoList.get(1).getStudentId()))
                .andExpect(jsonPath("$.content[1].lessonId").value(gradeDtoList.get(1).getLessonId()))
                .andExpect(jsonPath("$.content[1].gradeValue").value(gradeDtoList.get(1).getGradeValue()))
                .andExpect(jsonPath("$.content[1].gradeType").value(gradeDtoList.get(1).getGradeType().toString()))

                .andExpect(jsonPath("$.content[2].studentId").value(gradeDtoList.get(2).getStudentId()))
                .andExpect(jsonPath("$.content[2].lessonId").value(gradeDtoList.get(2).getLessonId()))
                .andExpect(jsonPath("$.content[2].gradeValue").value(gradeDtoList.get(2).getGradeValue()))
                .andExpect(jsonPath("$.content[2].gradeType").value(gradeDtoList.get(2).getGradeType().toString()));

        verify(gradeService, times(1)).getGradesByStudentId(eq(studentId), any(Pageable.class));
    }

    @Test
    void getAllGradesByLessonIdTest() throws Exception {
        Long lessonId = 1L;
        Long studentId = 1L;
        List<GradeDto> gradeDtoList = List.of(
                new GradeDto(++studentId, lessonId, 10, GradeType.TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, lessonId, 11, GradeType.MINI_TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, lessonId, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );
        Pageable pageable = Pageable.ofSize(gradeDtoList.size());
        Page<GradeDto> gradesPage = new PageImpl<>(gradeDtoList, pageable, gradeDtoList.size());

        when(gradeService.getGradesByLessonId(eq(lessonId), any(Pageable.class))).thenReturn(gradesPage);

        mockMvc.perform(get("/api/lessons/" + lessonId + "/grades"))
                .andExpect(jsonPath("$.content", hasSize(gradeDtoList.size())))

                .andExpect(jsonPath("$.content[0].studentId").value(gradeDtoList.get(0).getStudentId()))
                .andExpect(jsonPath("$.content[0].lessonId").value(gradeDtoList.get(0).getLessonId()))
                .andExpect(jsonPath("$.content[0].gradeValue").value(gradeDtoList.get(0).getGradeValue()))
                .andExpect(jsonPath("$.content[0].gradeType").value(gradeDtoList.get(0).getGradeType().toString()))

                .andExpect(jsonPath("$.content[1].studentId").value(gradeDtoList.get(1).getStudentId()))
                .andExpect(jsonPath("$.content[1].lessonId").value(gradeDtoList.get(1).getLessonId()))
                .andExpect(jsonPath("$.content[1].gradeValue").value(gradeDtoList.get(1).getGradeValue()))
                .andExpect(jsonPath("$.content[1].gradeType").value(gradeDtoList.get(1).getGradeType().toString()))

                .andExpect(jsonPath("$.content[2].studentId").value(gradeDtoList.get(2).getStudentId()))
                .andExpect(jsonPath("$.content[2].lessonId").value(gradeDtoList.get(2).getLessonId()))
                .andExpect(jsonPath("$.content[2].gradeValue").value(gradeDtoList.get(2).getGradeValue()))
                .andExpect(jsonPath("$.content[2].gradeType").value(gradeDtoList.get(2).getGradeType().toString()));

        verify(gradeService, times(1)).getGradesByLessonId(eq(lessonId), any(Pageable.class));
    }

    @Test
    void getAllGradesByModuleIdTest() throws Exception {
        Long lessonId = 1L;
        Long studentId = 1L;
        Long moduleId = 10L;
        List<GradeDto> gradeDtoList = List.of(
                new GradeDto(++studentId, ++lessonId, 10, GradeType.TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, ++lessonId, 11, GradeType.MINI_TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, ++lessonId, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );
        Pageable pageable = Pageable.ofSize(gradeDtoList.size());
        Page<GradeDto> gradesPage = new PageImpl<>(gradeDtoList, pageable, gradeDtoList.size());

        when(gradeService.getGradesByModuleId(eq(moduleId), any(Pageable.class))).thenReturn(gradesPage);

        mockMvc.perform(get("/api/modules/" + moduleId + "/grades"))
                .andExpect(jsonPath("$.content", hasSize(gradeDtoList.size())))

                .andExpect(jsonPath("$.content[0].studentId").value(gradeDtoList.get(0).getStudentId()))
                .andExpect(jsonPath("$.content[0].lessonId").value(gradeDtoList.get(0).getLessonId()))
                .andExpect(jsonPath("$.content[0].gradeValue").value(gradeDtoList.get(0).getGradeValue()))
                .andExpect(jsonPath("$.content[0].gradeType").value(gradeDtoList.get(0).getGradeType().toString()))

                .andExpect(jsonPath("$.content[1].studentId").value(gradeDtoList.get(1).getStudentId()))
                .andExpect(jsonPath("$.content[1].lessonId").value(gradeDtoList.get(1).getLessonId()))
                .andExpect(jsonPath("$.content[1].gradeValue").value(gradeDtoList.get(1).getGradeValue()))
                .andExpect(jsonPath("$.content[1].gradeType").value(gradeDtoList.get(1).getGradeType().toString()))

                .andExpect(jsonPath("$.content[2].studentId").value(gradeDtoList.get(2).getStudentId()))
                .andExpect(jsonPath("$.content[2].lessonId").value(gradeDtoList.get(2).getLessonId()))
                .andExpect(jsonPath("$.content[2].gradeValue").value(gradeDtoList.get(2).getGradeValue()))
                .andExpect(jsonPath("$.content[2].gradeType").value(gradeDtoList.get(2).getGradeType().toString()));

        verify(gradeService, times(1)).getGradesByModuleId(eq(moduleId), any(Pageable.class));
    }

    @Test
    void getAllGradesByModuleIdAndStudentIdTest() throws Exception {
        Long lessonId = 1L;
        Long studentId = 1L;
        Long moduleId = 10L;
        List<GradeDto> gradeDtoList = List.of(
                new GradeDto(studentId, ++lessonId, 10, GradeType.TEST, ZonedDateTime.now()),
                new GradeDto(studentId, ++lessonId, 11, GradeType.MINI_TEST, ZonedDateTime.now()),
                new GradeDto(studentId, ++lessonId, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );
        Pageable pageable = Pageable.ofSize(gradeDtoList.size());
        Page<GradeDto> gradesPage = new PageImpl<>(gradeDtoList, pageable, gradeDtoList.size());

        when(gradeService.getGradesByModuleIdAndStudentId(eq(moduleId), eq(studentId), any(Pageable.class))).thenReturn(gradesPage);

        mockMvc.perform(get("/api/modules/" + moduleId + "/students/" + studentId + "/grades"))
                .andExpect(jsonPath("$.content", hasSize(gradeDtoList.size())))

                .andExpect(jsonPath("$.content[0].studentId").value(gradeDtoList.get(0).getStudentId()))
                .andExpect(jsonPath("$.content[0].lessonId").value(gradeDtoList.get(0).getLessonId()))
                .andExpect(jsonPath("$.content[0].gradeValue").value(gradeDtoList.get(0).getGradeValue()))
                .andExpect(jsonPath("$.content[0].gradeType").value(gradeDtoList.get(0).getGradeType().toString()))

                .andExpect(jsonPath("$.content[1].studentId").value(gradeDtoList.get(1).getStudentId()))
                .andExpect(jsonPath("$.content[1].lessonId").value(gradeDtoList.get(1).getLessonId()))
                .andExpect(jsonPath("$.content[1].gradeValue").value(gradeDtoList.get(1).getGradeValue()))
                .andExpect(jsonPath("$.content[1].gradeType").value(gradeDtoList.get(1).getGradeType().toString()))

                .andExpect(jsonPath("$.content[2].studentId").value(gradeDtoList.get(2).getStudentId()))
                .andExpect(jsonPath("$.content[2].lessonId").value(gradeDtoList.get(2).getLessonId()))
                .andExpect(jsonPath("$.content[2].gradeValue").value(gradeDtoList.get(2).getGradeValue()))
                .andExpect(jsonPath("$.content[2].gradeType").value(gradeDtoList.get(2).getGradeType().toString()));

        verify(gradeService, times(1))
                .getGradesByModuleIdAndStudentId(eq(moduleId), eq(studentId), any(Pageable.class));
    }

    @Test
    void getAllGradesByLessonIdAndStudentIdTest() throws Exception {
        Long lessonId = 1L;
        Long studentId = 1L;
        List<GradeDto> gradeDtoList = List.of(
                new GradeDto(++studentId, lessonId, 10, GradeType.TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, lessonId, 11, GradeType.MINI_TEST, ZonedDateTime.now()),
                new GradeDto(++studentId, lessonId, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );
        Pageable pageable = Pageable.ofSize(gradeDtoList.size());
        Page<GradeDto> gradesPage = new PageImpl<>(gradeDtoList, pageable, gradeDtoList.size());

        when(gradeService.getGradesByLessonIdAndStudentId(eq(lessonId), eq(studentId), any(Pageable.class))).thenReturn(gradesPage);

        mockMvc.perform(get("/api/lessons/" + lessonId + "/students/" + studentId + "/grades"))
                .andExpect(jsonPath("$.content", hasSize(gradeDtoList.size())))

                .andExpect(jsonPath("$.content[0].studentId").value(gradeDtoList.get(0).getStudentId()))
                .andExpect(jsonPath("$.content[0].lessonId").value(gradeDtoList.get(0).getLessonId()))
                .andExpect(jsonPath("$.content[0].gradeValue").value(gradeDtoList.get(0).getGradeValue()))
                .andExpect(jsonPath("$.content[0].gradeType").value(gradeDtoList.get(0).getGradeType().toString()))

                .andExpect(jsonPath("$.content[1].studentId").value(gradeDtoList.get(1).getStudentId()))
                .andExpect(jsonPath("$.content[1].lessonId").value(gradeDtoList.get(1).getLessonId()))
                .andExpect(jsonPath("$.content[1].gradeValue").value(gradeDtoList.get(1).getGradeValue()))
                .andExpect(jsonPath("$.content[1].gradeType").value(gradeDtoList.get(1).getGradeType().toString()))

                .andExpect(jsonPath("$.content[2].studentId").value(gradeDtoList.get(2).getStudentId()))
                .andExpect(jsonPath("$.content[2].lessonId").value(gradeDtoList.get(2).getLessonId()))
                .andExpect(jsonPath("$.content[2].gradeValue").value(gradeDtoList.get(2).getGradeValue()))
                .andExpect(jsonPath("$.content[2].gradeType").value(gradeDtoList.get(2).getGradeType().toString()));

        verify(gradeService, times(1))
                .getGradesByLessonIdAndStudentId(eq(lessonId), eq(studentId), any(Pageable.class));
    }

    @Test
    void updateGradeTest() throws Exception {
        Long gradeId = 1L;
        Integer newGradeValue = 11;
        GradeDto gradeDtoToGet = new GradeDto(1L, 2L, 10, GradeType.TEST, ZonedDateTime.now());

        when(gradeService.getGradeById(gradeId)).thenReturn(gradeDtoToGet);

        String responseBody = mockMvc.perform(get("/api/grades/" + gradeId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.studentId").value(gradeDtoToGet.getStudentId()))
                .andExpect(jsonPath("$.lessonId").value(gradeDtoToGet.getLessonId()))
                .andExpect(jsonPath("$.gradeValue").value(gradeDtoToGet.getGradeValue()))
                .andExpect(jsonPath("$.gradeType").value(gradeDtoToGet.getGradeType().toString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        GradeDto gradeDtoToChange = objectMapper.readValue(responseBody, GradeDto.class);
        gradeDtoToChange.setGradeValue(newGradeValue);

        when(gradeService.updateGrade(gradeId, gradeDtoToChange)).thenReturn(gradeDtoToChange);

        mockMvc.perform(put("/api/grades/" + gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeDtoToChange)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.studentId").value(gradeDtoToChange.getStudentId()))
                .andExpect(jsonPath("$.lessonId").value(gradeDtoToChange.getLessonId()))
                .andExpect(jsonPath("$.gradeValue").value(gradeDtoToChange.getGradeValue()))
                .andExpect(jsonPath("$.gradeType").value(gradeDtoToChange.getGradeType().toString()));

        verify(gradeService, times(1)).getGradeById(gradeId);
        verify(gradeService, times(1)).updateGrade(gradeId, gradeDtoToChange);
    }

    @Test
    void deleteGradeTest() throws Exception {
        Long gradeId = 1L;

        when(gradeService.getGradeById(gradeId)).thenThrow(NoSuchElementException.class);
        doNothing().when(gradeService).deleteGrade(gradeId);

        mockMvc.perform(delete("/api/grades/" + gradeId))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/api/grades/" + gradeId))
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(NoSuchElementException.class));
    }
}