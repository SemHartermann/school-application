package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import co.inventorsoft.academy.schoolapplication.mapper.TeacherMapper;
import co.inventorsoft.academy.schoolapplication.repository.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;
    TeacherRepository teacherRepository;
    TeacherMapper teacherMapper = TeacherMapper.MAPPER;


    @Autowired
    public TeacherControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, TeacherRepository teacherRepository) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.teacherRepository = teacherRepository;
    }

    @Test
    void testThatGetAllTeachersReturnsAllTeachers() throws Exception {

        List<Teacher> teachers = Stream.of(

                        getTeacherDto("Didi",
                                "Kira",
                                "dkira0@nsw.gov.au",
                                "672-852-2193"),

                        getTeacherDto("Kristo",
                                "Blacksland",
                                "kblacksland5@microsoft.com",
                                "342-115-7222"),

                        getTeacherDto("Matias",
                                "Sketch",
                                "msketch9@unc.edu",
                                "700-359-5577"))

                .map(teacherMapper::toTeacherEntity)
                .collect(Collectors.toList());

        teacherRepository.saveAll(teachers);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(
                MockMvcResultMatchers.status().isOk()

        ).andExpectAll(

                MockMvcResultMatchers.jsonPath("$.content[0].firstName")
                        .value("Didi"),
                MockMvcResultMatchers.jsonPath("$.content[0].lastName")
                        .value("Kira"),
                MockMvcResultMatchers.jsonPath("$.content[0].email")
                        .value("dkira0@nsw.gov.au"),
                MockMvcResultMatchers.jsonPath("$.content[0].phone")
                        .value("672-852-2193"),

                MockMvcResultMatchers.jsonPath("$.content[1].firstName")
                        .value("Kristo"),
                MockMvcResultMatchers.jsonPath("$.content[1].lastName")
                        .value("Blacksland"),
                MockMvcResultMatchers.jsonPath("$.content[1].email")
                        .value("kblacksland5@microsoft.com"),
                MockMvcResultMatchers.jsonPath("$.content[1].phone")
                        .value("342-115-7222"),

                MockMvcResultMatchers.jsonPath("$.content[2].firstName")
                        .value("Matias"),
                MockMvcResultMatchers.jsonPath("$.content[2].lastName")
                        .value("Sketch"),
                MockMvcResultMatchers.jsonPath("$.content[2].email")
                        .value("msketch9@unc.edu"),
                MockMvcResultMatchers.jsonPath("$.content[2].phone")
                        .value("700-359-5577")

        );
    }

    @Test
    void testThatGetTeacherByIdReturnsTeacher() throws Exception {

        long teacherId = 1;

        Teacher teacher = teacherMapper.toTeacherEntity(getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193"));

        teacherRepository.save(teacher);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teachers/" + teacherId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpectAll(

                MockMvcResultMatchers.jsonPath("$.firstName")
                        .value("Didi"),
                MockMvcResultMatchers.jsonPath("$.lastName")
                        .value("Kira"),
                MockMvcResultMatchers.jsonPath("$.email")
                        .value("dkira0@nsw.gov.au"),
                MockMvcResultMatchers.jsonPath("$.phone")
                        .value("672-852-2193"));

    }

    @Test
    void testThatAddNewTeacherAddNewTeacher() throws Exception {

        long teacherId = 1;

        TeacherDto teacherDto = getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193");

        String teacherJson = objectMapper.writeValueAsString(teacherDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson)

        ).andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teachers/" + teacherId)

        ).andExpectAll(

                MockMvcResultMatchers.jsonPath("$.firstName")
                        .value("Didi"),
                MockMvcResultMatchers.jsonPath("$.lastName")
                        .value("Kira"),
                MockMvcResultMatchers.jsonPath("$.email")
                        .value("dkira0@nsw.gov.au"),
                MockMvcResultMatchers.jsonPath("$.phone")
                        .value("672-852-2193")
        );
    }

    @Test
    void testThatUpdateTeacherByIdUpdatesTeacher() throws Exception {

        long teacherId = 1;

        Teacher teacher = teacherMapper.toTeacherEntity(getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193"));

        teacherRepository.save(teacher);

        teacher.setFirstName("Diddi");
        teacher.setLastName("Kirra");
        teacher.setEmail("dkira11@nsw.gov.au");
        teacher.setPhone("672-852-2194");

        String teacherJson = objectMapper.writeValueAsString(teacher);


        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/teachers/" + teacherId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(teacherJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teachers/" + teacherId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpectAll(

                MockMvcResultMatchers.jsonPath("$.firstName")
                        .value("Diddi"),
                MockMvcResultMatchers.jsonPath("$.lastName")
                        .value("Kirra"),
                MockMvcResultMatchers.jsonPath("$.email")
                        .value("dkira11@nsw.gov.au"),
                MockMvcResultMatchers.jsonPath("$.phone")
                        .value("672-852-2194")
        );
    }

    @Test
    void testThatDeleteTeacherByIdDeletesTeacher() throws Exception {
        long teacherId = 1;

        Teacher teacher = teacherMapper.toTeacherEntity(getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193"));

        teacherRepository.save(teacher);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/teachers/" + teacherId)

        ).andExpect(status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/teachers/" + teacherId)

        ).andExpect(result -> assertThat(result
                .getResolvedException() instanceof NoSuchElementException)
                .isTrue());

    }

    private TeacherDto getTeacherDto(String firstName, String lastName, String email, String phone) {
        return TeacherDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}