package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformance;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceResponseDto;
import co.inventorsoft.academy.schoolapplication.service.SubjectsPerformanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SubjectsPerformanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubjectsPerformanceService service;

    @ParameterizedTest
    @MethodSource("providerOfParametersForPerformance")
    void whenGivenValidParameters_whenGetPerformance_thenReturnsCorrectData(Long studentId, YearMonth fromDate, YearMonth toDate,
                                                                            List<SubjectsPerformance> expectedPerformances) throws Exception {

        SubjectsPerformanceRequestDto requestDto = requestDtoBuilder(fromDate, toDate);

        when(service.getPerformance(studentId, fromDate, toDate))
                .thenReturn(new SubjectsPerformanceResponseDto(expectedPerformances));

        ResultActions result = mockMvc.perform(post("/api/reports/students/{studentId}/performance", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        for (int i = 0; i < expectedPerformances.size(); i++) {
            checkSubjectsPerformance(result, expectedPerformances.get(i), i);
        }
    }

    private static Stream<Arguments> providerOfParametersForPerformance() {
        List<SubjectsPerformance> dateRangeBasedPerformances = List.of(
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 9), 10.0),
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 10), 12.0),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 9), 9.5),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 10), 11.5)
        );

        List<SubjectsPerformance> monthBasedPerformances = List.of(
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 9), 10.0),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 9), 9.5)
        );

        return Stream.of(
                Arguments.of(3L, YearMonth.parse("2023-01"), YearMonth.parse("2023-12"), dateRangeBasedPerformances),
                Arguments.of(3L, YearMonth.parse("2023-09"), YearMonth.parse("2023-09"), monthBasedPerformances)
        );
    }

    @Test
    void whenGivenInvalidStudentId_whenGetPerformance_thenStatusOkButEmptyResponse() throws Exception {
        Long invalidStudentId = 15000L;

        SubjectsPerformanceRequestDto requestDto =
                requestDtoBuilder(YearMonth.parse("2023-01"), YearMonth.parse("2023-12"));

        String expectedErrorMsg = "Student with id 15000 wasn't found!";
        when(service.getPerformance(eq(invalidStudentId), any(), any()))
                .thenThrow(new NoSuchElementException(expectedErrorMsg));

        mockMvc.perform(post("/api/reports/students/{studentId}/performance", invalidStudentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedErrorMsg));
    }

    @Test
    void whenGivenMissingRangeParameters_whenGetPerformance_thenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/api/reports/students/{studentId}/performance", 3))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGivenNoAllowedMappingMethod_whenGetPerformance_thenStatusMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/reports/students/{studentId}/performance", 3))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void whenGivenNoDataFound_whenGetPerformance_thenStatusOkWithEmptyResponse() throws Exception {
        Long studentId = 3L;
        SubjectsPerformanceRequestDto requestDto =
                requestDtoBuilder(YearMonth.parse("2023-01"), YearMonth.parse("2023-12"));

        when(service.getPerformance(any(), any(), any()))
                .thenReturn(new SubjectsPerformanceResponseDto(List.of()));

        mockMvc.perform(post("/api/reports/students/{studentId}/performance", studentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subjectPerformances").isEmpty());
    }

    private void checkSubjectsPerformance(ResultActions result, SubjectsPerformance expected, int index) throws Exception {
        String basePath = "$.subjectPerformances[" + index + "]";

        result.andExpect(jsonPath(basePath + ".subjectId").value(expected.getSubjectId()))
                .andExpect(jsonPath(basePath + ".subjectName").value(expected.getSubjectName()))
                .andExpect(jsonPath(basePath + ".yearMonth").value(expected.getYearMonth().toString()))
                .andExpect(jsonPath(basePath + ".averageGrade").value(expected.getAverageGrade()));
    }

    private SubjectsPerformanceRequestDto requestDtoBuilder(YearMonth from, YearMonth to) {
        SubjectsPerformanceRequestDto dto = new SubjectsPerformanceRequestDto();
        dto.setFrom(from);
        dto.setTo(to);

        return dto;
    }
}