package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformance;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceResponseDto;
import co.inventorsoft.academy.schoolapplication.repository.SubjectsPerformanceRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.SubjectsPerformanceServiceImpl;
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

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubjectsPerformanceServiceImplTest {
    @Mock
    private SubjectsPerformanceRepository repository;

    @Mock
    StudentService studentService;

    @InjectMocks
    private SubjectsPerformanceServiceImpl service;

    @ParameterizedTest
    @MethodSource("provideParametersForPerformanceVerification")
    void whenGivenValidParameters_thenVerifyPerformance(Long studentId, YearMonth startDate, YearMonth endDate,
                                                        List<SubjectsPerformance> mockData, List<SubjectsPerformance> expectedResults) {
        StudentResponseDto studentDto = new StudentResponseDto(studentId, "Neo", "Anderson", "neo.anderson@gmail.com", "+0-000-000-000");

        when(studentService.getStudentById(studentId)).thenReturn(studentDto);
        when(repository.findByStudentIdAndYearMonthRange(studentId, startDate, endDate)).thenReturn(mockData);

        SubjectsPerformanceResponseDto result = service.getPerformance(studentId, startDate, endDate);

        assertNotNull(result);
        assertEquals(expectedResults.size(), result.getSubjectPerformances().size());

        for (int i = 0; i < expectedResults.size(); i++) {
            SubjectsPerformance expected = expectedResults.get(i);
            SubjectsPerformance actual = result.getSubjectPerformances().get(i);
            assertSubjectsPerformance(actual, expected.getSubjectName(), expected.getSubjectId(), expected.getYearMonth(), expected.getAverageGrade());
        }

        verify(repository).findByStudentIdAndYearMonthRange(studentId, startDate, endDate);
    }

    private static Stream<Arguments> provideParametersForPerformanceVerification() {
        List<SubjectsPerformance> monthBasedPerformances = List.of(
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 9), 8.0),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 9), 7.5)
        );

        List<SubjectsPerformance> dateRangeBasedPerformances = List.of(
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 9), 8.0),
                new SubjectsPerformance("Math", 1L, YearMonth.of(2023, 10), 12.0),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 9), 11.0),
                new SubjectsPerformance("Chemistry", 2L, YearMonth.of(2023, 10), 12.0)
        );

        return Stream.of(
            Arguments.of(3L,YearMonth.of(2023, 9), YearMonth.of(2023, 9), monthBasedPerformances, monthBasedPerformances),
            Arguments.of(3L, YearMonth.of(2023, 1), YearMonth.of(2023, 12), dateRangeBasedPerformances, dateRangeBasedPerformances)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidParametersForPerformance")
    void whenGivenInvalidParameters_thenExpectException(Long studentId, YearMonth startDate, YearMonth endDate, Class<? extends Exception> expectedException, String expectedMessage) {
        if (expectedException.equals(NoSuchElementException.class)) {
            when(studentService.getStudentById(studentId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        }

        Exception thrown = assertThrows(expectedException, () -> service.getPerformance(studentId, startDate, endDate));
        assertEquals(expectedMessage, thrown.getMessage());

        verify(repository, never()).findByStudentIdAndYearMonthRange(eq(studentId), any(YearMonth.class), any(YearMonth.class));
    }

    private static Stream<Arguments> provideInvalidParametersForPerformance() {
        YearMonth startDate = YearMonth.of(2023,1);
        YearMonth endDate = YearMonth.of(2023,12);
        return Stream.of(
                Arguments.of(3L, endDate, startDate,
                        IllegalArgumentException.class, String.format("Invalid date range: start date '%s' can't be after '%s'!", startDate, endDate)),
                Arguments.of(15000L, startDate, endDate,
                        NoSuchElementException.class, "Student with id 15000 wasn't found!")
        );
    }

    @Test
    void whenValidDateRangeHasNoData_thenExpectEmptyPerformanceData() {
        Long studentId = 3L;
        YearMonth startDate = YearMonth.parse("2025-01");
        YearMonth endDate = YearMonth.parse("2025-12");

        when(repository.findByStudentIdAndYearMonthRange(studentId, startDate, endDate)).thenReturn(Collections.emptyList());

        SubjectsPerformanceResponseDto result = service.getPerformance(studentId, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.getSubjectPerformances().isEmpty());

        verify(repository).findByStudentIdAndYearMonthRange(studentId, startDate, endDate);
    }

    private void assertSubjectsPerformance(SubjectsPerformance actual, String expectedSubjectName, Long expectedSubjectId,
                                           YearMonth expectedYearMonth, Double expectedAverageGrade) {
        assertThat(actual)
                .hasFieldOrPropertyWithValue("subjectName", expectedSubjectName)
                .hasFieldOrPropertyWithValue("subjectId", expectedSubjectId)
                .hasFieldOrPropertyWithValue("yearMonth", expectedYearMonth)
                .hasFieldOrPropertyWithValue("averageGrade", expectedAverageGrade);
    }
}