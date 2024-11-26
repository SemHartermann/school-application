package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ClassGroupPerformanceReport;
import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupReportParams;
import co.inventorsoft.academy.schoolapplication.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {

    ReportService reportService;

    @GetMapping("/class-group-performance")
    public List<ClassGroupPerformanceReport> getClassGroupPerformance(
            RequestClassGroupReportParams groupPerformanceParams) {
        return reportService.getClassGroupPerformance(groupPerformanceParams);
    }
}
