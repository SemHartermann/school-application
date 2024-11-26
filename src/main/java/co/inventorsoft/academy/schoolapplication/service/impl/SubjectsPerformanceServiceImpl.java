package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformance;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceResponseDto;
import co.inventorsoft.academy.schoolapplication.repository.SubjectsPerformanceRepository;
import co.inventorsoft.academy.schoolapplication.service.StudentService;
import co.inventorsoft.academy.schoolapplication.service.SubjectsPerformanceService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectsPerformanceServiceImpl implements SubjectsPerformanceService {
    SubjectsPerformanceRepository subjectsAverageRepository;
    StudentService studentService;

    @Transactional(readOnly = true)
    public SubjectsPerformanceResponseDto getPerformance(Long studentId, YearMonth startDate, YearMonth endDate) {
        validateParameters(studentId, startDate, endDate);

        List<SubjectsPerformance> subjectsAverages = subjectsAverageRepository.findByStudentIdAndYearMonthRange(studentId, startDate, endDate);

        return new SubjectsPerformanceResponseDto(subjectsAverages);
    }

    private void validateParameters(Long studentId, YearMonth startDate, YearMonth endDate) {
        try {
            studentService.getStudentById(studentId);
        } catch (ResponseStatusException e) {
            String errorMsg = String.format("Student with id %s wasn't found!", studentId);
            log.error(errorMsg);

            throw new NoSuchElementException(errorMsg);
        }

        if (startDate.isAfter(endDate)) {
            String errorMsg = String.format("Invalid date range: start date '%s' can't be after '%s'!", endDate, startDate);
            log.error(errorMsg);

            throw new IllegalArgumentException(errorMsg);
        }
    }
}