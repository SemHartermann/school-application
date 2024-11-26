package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ClassGroupPerformanceReport;
import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupReportParams;

import java.util.List;

public interface ReportService {
    List<ClassGroupPerformanceReport> getClassGroupPerformance(RequestClassGroupReportParams groupPerformance);
}
