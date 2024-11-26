package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;

import java.util.List;

public interface ParentNotificationService {
    void sendDailyGradesReport();

    String createReportForStudent(List<GradeDto> grades);

    void sendAttendanceNotification(String to, String message);
}
