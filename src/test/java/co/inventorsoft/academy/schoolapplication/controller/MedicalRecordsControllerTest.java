package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsStudentDto;
import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.HealthGroup;
import co.inventorsoft.academy.schoolapplication.mapper.MedicalRecordsMapper;
import co.inventorsoft.academy.schoolapplication.mapper.MedicalRecordsStudentMapper;
import co.inventorsoft.academy.schoolapplication.repository.MedicalRecordsRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalRecordsControllerTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    MedicalRecordsMapper medicalRecordsMapper = MedicalRecordsMapper.MAPPER;
    MedicalRecordsStudentMapper medicalRecordsStudentMapper = MedicalRecordsStudentMapper.MAPPER;

    MedicalRecordsRepository medicalRecordsRepository;
    StudentRepository studentRepository;

    @Autowired
    public MedicalRecordsControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
                                        MedicalRecordsRepository medicalRecordsRepository,
                                        StudentRepository studentRepository) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.studentRepository = studentRepository;
    }

    @Test
    public void testThatGetAllRecordsReturnsAllRecords() throws Exception {
        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsStudentDto student1 = getStudent(
                "Nichol",
                "Fenelow",
                "nfenelow4@youtube.com",
                "987654321");

        MedicalRecordsStudentDto student2 = getStudent(
                "Shayne",
                "Timony",
                "sotimony0@bravesites.com",
                "123789456");


        studentRepository.saveAll(List.of(
                medicalRecordsStudentMapper.toStudentEntity(student0),
                medicalRecordsStudentMapper.toStudentEntity(student1),
                medicalRecordsStudentMapper.toStudentEntity(student2)
        ));

        student0.setId(1L);
        student1.setId(2L);
        student2.setId(3L);

        List<MedicalRecordsDto> mockRecordsDtoList = List.of(

                getMedicalRecordsDto(
                        "Peanuts, shellfish",
                        HealthGroup.GROUP_A,
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        student0),

                getMedicalRecordsDto(
                        "Pollen, shellfish",
                        HealthGroup.GROUP_B,
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        student1),

                getMedicalRecordsDto(
                        "Pollen, peanuts",
                        HealthGroup.GROUP_C,
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                        student2)
        );

        medicalRecordsRepository.saveAll(mockRecordsDtoList.stream()
                .map(medicalRecordsMapper::toMedicalRecordsEntity)
                .collect(Collectors.toList()));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/medical-records")

        ).andExpect(status().isOk()
        ).andExpectAll(MockMvcResultMatchers.jsonPath("$.content[0].healthGroup")
                                .value("GROUP_A"),

                        MockMvcResultMatchers.jsonPath("$.content[0].allergies")
                                .value("Peanuts, shellfish"),

                        MockMvcResultMatchers.jsonPath("$.content[0].info")
                                .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                        MockMvcResultMatchers.jsonPath("$.content[0].student")
                                .value(student0)

        ).andExpectAll(MockMvcResultMatchers.jsonPath("$.content[1].healthGroup")
                                .value("GROUP_B"),

                        MockMvcResultMatchers.jsonPath("$.content[1].allergies")
                                .value("Pollen, shellfish"),

                        MockMvcResultMatchers.jsonPath("$.content[1].info")
                                .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                        MockMvcResultMatchers.jsonPath("$.content[1].student")
                                .value(student1)

        ).andExpectAll(MockMvcResultMatchers.jsonPath("$.content[2].healthGroup")
                                .value("GROUP_C"),

                        MockMvcResultMatchers.jsonPath("$.content[2].allergies")
                                .value("Pollen, peanuts"),

                        MockMvcResultMatchers.jsonPath("$.content[2].info")
                                .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                        MockMvcResultMatchers.jsonPath("$.content[2].student")
                                .value(student2)
        );

    }

    @Test
    public void testThaGetRecordsByIdReturnsRecord() throws Exception {

        long recordsId = 1;
        long studentId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        studentRepository.save(medicalRecordsStudentMapper.toStudentEntity(student0));

        student0.setId(studentId);

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);


        medicalRecordsRepository.save(medicalRecordsMapper.toMedicalRecordsEntity(mockRecordsDto));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/medical-records/" + recordsId)

        ).andExpectAll(

                MockMvcResultMatchers.jsonPath("$.healthGroup")
                        .value("GROUP_A"),

                MockMvcResultMatchers.jsonPath("$.allergies")
                        .value("Peanuts, shellfish"),

                MockMvcResultMatchers.jsonPath("$.info")
                        .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                MockMvcResultMatchers.jsonPath("$.student")
                        .value(student0)
        );
    }

    @Test
    public void testThatAddNewRecordsByStudentIdAddsNewRecords() throws Exception {

        long studentId = 1;
        long recordsId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);


        studentRepository.save(medicalRecordsStudentMapper.toStudentEntity(student0));

        String recordsJson = objectMapper.writeValueAsString(mockRecordsDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/medical-records/" + studentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(recordsJson)

        ).andExpect(status().isCreated());


        student0.setId(studentId);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/medical-records/" + recordsId)

        ).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.healthGroup")
                        .value("GROUP_A"),

                MockMvcResultMatchers.jsonPath("$.allergies")
                        .value("Peanuts, shellfish"),

                MockMvcResultMatchers.jsonPath("$.info")
                        .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                MockMvcResultMatchers.jsonPath("$.student")
                        .value(student0)
        );
    }

    @Test
    public void testThatUpdateRecordsByIdUpdatesRecords() throws Exception {

        long studentId = 1;
        long recordsId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);


        studentRepository.save(medicalRecordsStudentMapper.toStudentEntity(student0));

        String recordsJson = objectMapper.writeValueAsString(mockRecordsDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/medical-records/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordsJson)

        ).andExpect(status().isCreated());


        student0.setId(studentId);

        mockRecordsDto.setAllergies("Shellfish, Peanuts");

        mockRecordsDto.setHealthGroup(HealthGroup.GROUP_B);

        recordsJson = objectMapper.writeValueAsString(mockRecordsDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/medical-records/" + recordsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordsJson)

        ).andExpect(MockMvcResultMatchers.status().isOk());


        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/medical-records/" + recordsId)

        ).andExpectAll(
                MockMvcResultMatchers.jsonPath("$.healthGroup")
                        .value("GROUP_B"),

                MockMvcResultMatchers.jsonPath("$.allergies")
                        .value("Shellfish, Peanuts"),

                MockMvcResultMatchers.jsonPath("$.info")
                        .value("Lorem ipsum dolor sit amet, consectetur adipiscing elit."),

                MockMvcResultMatchers.jsonPath("$.student")
                        .value(student0)
        );
    }

    @Test
    public void testThatDeleteRecordsByIdDeletesRecords() throws Exception {

        long recordsId = 1;
        long studentId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        studentRepository.save(medicalRecordsStudentMapper.toStudentEntity(student0));

        student0.setId(studentId);

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);

        medicalRecordsRepository.save(medicalRecordsMapper.toMedicalRecordsEntity(mockRecordsDto));

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/medical-records/" + recordsId)

        ).andExpect(status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/medical-records/" + recordsId)

        ).andExpect(result -> assertThat(result
                .getResolvedException() instanceof NoSuchElementException)
                .isTrue());
    }
    private MedicalRecordsDto getMedicalRecordsDto(String allergies,
                                                   HealthGroup healthGroup,
                                                   String info,
                                                   MedicalRecordsStudentDto studentDto) {

        return MedicalRecordsDto.builder()
                .allergies(allergies)
                .healthGroup(healthGroup)
                .info(info)
                .student(studentDto)
                .build();
    }

    private MedicalRecordsStudentDto getStudent(String firstName, String lastName, String email, String phone) {
        return MedicalRecordsStudentDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}
