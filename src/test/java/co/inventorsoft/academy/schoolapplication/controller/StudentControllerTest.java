package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.service.impl.StudentServiceImpl;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentServiceImpl studentService;

    @Test
    void testGetAllStudents() throws Exception {
        Page<StudentResponseDto> page = new PageImpl<>(List.of(
                new StudentResponseDto(1L, "John", "Doe", "john@example.com", "1234567890"),
                new StudentResponseDto(2L, "Jane", "Smith", "jane@example.com", "9876543210")
        ));

        when(studentService.getAllStudents(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(studentService).getAllStudents(any(Pageable.class));
    }

    @Test
    void testGetStudentById() throws Exception {
        Long studentId = 1L;

        StudentResponseDto student = new StudentResponseDto(studentId, "John", "Doe", "john@example.com", "1234567890");

        when(studentService.getStudentById(studentId)).thenReturn(student);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(student.id()));

        verify(studentService).getStudentById(1L);
    }

    @Test
    void testAddNewStudent() throws Exception {
        StudentRequestDto requestDto = new StudentRequestDto("John", "Doe", "john@example.com", "1234567890");
        StudentResponseDto student = new StudentResponseDto(1L, "John", "Doe", "john@example.com", "1234567890");

        when(studentService.addNewStudent(requestDto)).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(student.id()))
                .andExpect(jsonPath("$.firstName").value(student.firstName()));

        verify(studentService).addNewStudent(requestDto);
    }

    @Test
    void testUpdateStudentById() throws Exception {
        StudentRequestDto requestDto = new StudentRequestDto("Updated_John", "Updated_Doe", "john@example.com", "1234567890");
        StudentResponseDto updatedStudent = new StudentResponseDto(1L, "Updated_John", "Updated_Doe", "john@example.com", "1234567890");

        when(studentService.updateStudentById(1L, requestDto)).thenReturn(updatedStudent);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedStudent.id()))
                .andExpect(jsonPath("$.firstName").value(updatedStudent.firstName()));

        verify(studentService).updateStudentById(1L, requestDto);
    }

    @Test
    void testDeleteStudentById() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudentById(1L);
    }
}