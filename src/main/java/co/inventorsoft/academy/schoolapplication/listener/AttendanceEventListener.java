package co.inventorsoft.academy.schoolapplication.listener;

import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.event.AttendanceEvent;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.ParentNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AttendanceEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceEventListener.class);

    private final ParentNotificationService parentNotificationService;
    private final StudentRepository studentRepository;

    public AttendanceEventListener(ParentNotificationService notificationService, StudentRepository studentRepository) {
        this.parentNotificationService = notificationService;
        this.studentRepository = studentRepository;
    }

    @EventListener
    public void onAttendanceEvent(AttendanceEvent event) {
        try {
            Attendance attendance = event.getAttendance();
            Long studentId = attendance.getStudent().getId();

            studentRepository.findById(studentId).ifPresent(student -> {
                String parentEmail = studentRepository.findParentEmailByStudentId(studentId).orElse(null);
                String studentFullName = student.getFirstName() + " " + student.getLastName();
                String message = "Your child " + studentFullName + " was absent for " + attendance.getLesson().getName();
                parentNotificationService.sendAttendanceNotification(parentEmail, message);
            });
        } catch (Exception e) {
            logger.error("Error processing attendance event: {}", e.getMessage(), e);
        }
    }
}
