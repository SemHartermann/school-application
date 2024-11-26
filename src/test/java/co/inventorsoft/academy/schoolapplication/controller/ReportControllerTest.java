package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ClassGroupPerformanceReport;
import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupReportParams;
import co.inventorsoft.academy.schoolapplication.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ReportService reportService;

    @Test
    @WithMockUser
    void getClassGroupPerformance() throws Exception {

        RequestClassGroupReportParams groupPerformanceParams = new RequestClassGroupReportParams();
        groupPerformanceParams.setClassGroupId(1L);
        groupPerformanceParams.setStartDate(LocalDate.now().minusDays(1));
        groupPerformanceParams.setEndDate(LocalDate.now());
        groupPerformanceParams.setSubjectId(1L);

        List<ClassGroupPerformanceReport> mockReports = Collections.singletonList(new ClassGroupPerformanceReport());

        when(reportService.getClassGroupPerformance(groupPerformanceParams)).thenReturn(mockReports);

        mockMvc.perform(get("/api/reports/class-group-performance")
                        .param("classGroupId", "1")
                        .param("subjectId", "1")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().toString())
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}