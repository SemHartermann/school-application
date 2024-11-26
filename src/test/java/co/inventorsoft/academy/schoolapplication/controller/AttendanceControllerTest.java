package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import co.inventorsoft.academy.schoolapplication.repository.AttendanceRepository;
import co.inventorsoft.academy.schoolapplication.repository.ClassGroupRepository;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AttendanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AttendanceRepository attendanceRepository;

    @Test
    void testGetAllAttendances() throws Exception {
        Page<AttendanceResponseDto> page = new PageImpl<>(List.of(
                new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT),
                new AttendanceResponseDto(2L, 1L, 2L, AttendanceType.ABSENT)
        ));

        when(attendanceService.getAllAttendances(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/attendances"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(attendanceService).getAllAttendances(any(Pageable.class));
    }

    @Test
    void testGetAttendanceById() throws Exception {
        Long attendanceId = 1L;

        AttendanceResponseDto attendance = new AttendanceResponseDto(attendanceId, 1L, 1L, AttendanceType.PRESENT);

        when(attendanceService.getAttendanceById(attendanceId)).thenReturn(attendance);

        mockMvc.perform(get("/api/attendances/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(attendance.id()));

        verify(attendanceService).getAttendanceById(1L);
    }

    @Test
    void testAddNewAttendance() throws Exception {
        AttendanceRequestDto requestDto = new AttendanceRequestDto(1L, 1L, AttendanceType.PRESENT);
        AttendanceResponseDto attendance = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT);

        when(attendanceService.addNewAttendance(requestDto)).thenReturn(attendance);

        mockMvc.perform(post("/api/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(attendance.id()))
                .andExpect(jsonPath("$.attendanceType").value(attendance.attendanceType().toString()));

        verify(attendanceService).addNewAttendance(requestDto);
    }

    @Test
    void testUpdateAttendanceById() throws Exception {
        AttendanceRequestDto requestDto = new AttendanceRequestDto(1L, 1L, AttendanceType.LATE);
        AttendanceResponseDto updatedAttendance = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.LATE);

        when(attendanceService.updateAttendanceById(1L, requestDto)).thenReturn(updatedAttendance);

        mockMvc.perform(put("/api/attendances/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedAttendance.id()))
                .andExpect(jsonPath("$.attendanceType").value(updatedAttendance.attendanceType().toString()));

        verify(attendanceService).updateAttendanceById(1L, requestDto);
    }

    @Test
    void testDeleteAttendanceById() throws Exception {
        mockMvc.perform(delete("/api/attendances/1"))
                .andExpect(status().isNoContent());

        verify(attendanceService).deleteAttendanceById(1L);
    }

    @Test
    void testGetAttendancesByLessonId() throws Exception {
        Page<AttendanceResponseDto> page = new PageImpl<>(List.of(
                new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT),
                new AttendanceResponseDto(2L, 1L, 2L, AttendanceType.ABSENT)
        ));

        when(attendanceService.getAttendancesByLessonId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/attendances/lesson/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(attendanceService).getAttendancesByLessonId(eq(1L), any(Pageable.class));
    }

    @Test
    void testCreateAttendancesForLesson() throws Exception {
        Long lessonId = 1L;
        Long classGroupId = 1L;

        List<Student> students = List.of(new Student(), new Student());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(new Lesson()));
        when(studentRepository.findAllByClassGroupId(classGroupId)).thenReturn(students);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(new Attendance());

        mockMvc.perform(post("/api/attendances/lessons/1")
                        .param("classGroupId", String.valueOf(classGroupId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(attendanceService, times(1)).createAttendancesForLesson(lessonId, classGroupId);
    }

    @Test
    void testBulkEditAttendances() throws Exception {
        String attendanceType = "PRESENT";

        List<Long> attendanceIds = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(post("/attendances/bulkEdit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("attendanceType", attendanceType)
                        .content(objectMapper.writeValueAsString(attendanceIds)))
                .andExpect(status().isOk());

        verify(attendanceService, times(1)).bulkEdit(attendanceIds, eq(AttendanceType.PRESENT));
    }

}