package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceResponseDto;

import java.time.YearMonth;

public interface SubjectsPerformanceService {
    SubjectsPerformanceResponseDto getPerformance(Long studentId, YearMonth startDate, YearMonth endDate);
}
