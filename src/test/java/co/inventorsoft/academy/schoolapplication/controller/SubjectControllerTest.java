package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.entity.Subject;
import co.inventorsoft.academy.schoolapplication.mapper.SubjectMapper;
import co.inventorsoft.academy.schoolapplication.repository.SubjectRepository;
import co.inventorsoft.academy.schoolapplication.util.excepion.excel.EmptyExcelFileException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper;
    SubjectRepository subjectRepository;
    SubjectMapper subjectMapper;


    @Autowired
    public SubjectControllerTest(MockMvc mockMvc,
                                 ObjectMapper objectMapper,
                                 SubjectRepository subjectRepository) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.subjectRepository = subjectRepository;
        this.subjectMapper = Mappers.getMapper(SubjectMapper.class);
    }

    @Test
    public void testThatGetAllSubjectsReturnsAllSubjects() throws Exception {

        List<Subject> subjects = Stream.of(
                        getSubjectDto("Physics"),
                        getSubjectDto("Chemistry"),
                        getSubjectDto("Mathematics"))

                .map(subjectMapper::toSubjectEntity)
                .collect(Collectors.toList());

        subjectRepository.saveAll(subjects);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(
                MockMvcResultMatchers.status().isOk()

        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[0].name")
                        .value("Physics")

        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[1].name")
                        .value("Chemistry")

        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.content[2].name")
                        .value("Mathematics")
        );

    }

    @Test
    public void testThatAddNewSubjectCreatesNewSubject() throws Exception {

        SubjectDto subjectDto = getSubjectDto("Physics");

        String subjectJson = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        long newSubjectId = 1;

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/subjects/" + newSubjectId)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Physics")
        );
    }

    @Test
    public void testThatUpdateSubjectByIdUpdatesSubject() throws Exception {

        SubjectDto subjectDto = getSubjectDto("Physics");

        String subjectJson = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        long newSubjectId = 1;

        subjectDto = getSubjectDto("Physics Updated");

        subjectJson = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/subjects/" + newSubjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/subjects/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Physics Updated")

        );
    }

    @Test
    public void testThatDeleteSubjectByIdDeletesSubject() throws Exception {

        SubjectDto subjectDto = getSubjectDto("Physics");

        String subjectJson = objectMapper.writeValueAsString(subjectDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson)

        ).andExpect(MockMvcResultMatchers.status().isCreated());

        long newSubjectId = 1;

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/subjects/" + newSubjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subjectJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/subjects")
                                .contentType(MediaType.APPLICATION_JSON)

                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").doesNotExist());
    }

    @Test
    @WithMockUser
    public void testSuccessesUploadCsvFile() throws Exception {

        String csvContent = "name\nMath\nHistory";
        MockMultipartFile file = new MockMultipartFile("file"
                , "test.csv"
                , "text/csv"
                , csvContent.getBytes());

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/subjects/import").file(file)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void testUploadEmptyCsvFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file"
                , "test.csv"
                , "text/csv"
                , new byte[0]);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/subjects/import").file(file)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser
    public void testUploadEmptyExelFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file"
                , "test.xlsx"
                , "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                , new byte[0]);

        ServletException servletException = assertThrows(ServletException.class, () -> {
            mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/subjects/import").file(file)
                            .with(csrf()));
        });

        Throwable rootCause = servletException.getCause();
        assertEquals(EmptyExcelFileException.class, rootCause.getClass());
        assertEquals("The provided file test.xlsx is empty!", rootCause.getMessage());
    }

    private SubjectDto getSubjectDto(String name) {
        return SubjectDto.builder()
                .name(name)
                .build();
    }

}
