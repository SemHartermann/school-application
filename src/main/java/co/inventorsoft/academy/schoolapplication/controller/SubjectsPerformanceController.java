package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.subjectsperformance.SubjectsPerformanceResponseDto;
import co.inventorsoft.academy.schoolapplication.service.SubjectsPerformanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectsPerformanceController {
    SubjectsPerformanceService performanceService;

    @PostMapping("/students/{studentId}/performance")
    public SubjectsPerformanceResponseDto getPerformanceByStudentId(@PathVariable Long studentId, @RequestBody @Valid SubjectsPerformanceRequestDto requestDto) {
        return performanceService.getPerformance(studentId, requestDto.getFrom(), requestDto.getTo());
    }
}