package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.service.GradeService;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import co.inventorsoft.academy.schoolapplication.service.MailService;
import co.inventorsoft.academy.schoolapplication.service.ParentNotificationService;
import co.inventorsoft.academy.schoolapplication.service.StudentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ParentNotificationServiceImpl implements ParentNotificationService {

    private final GradeService gradeService;
    private final MailService emailService;
    private final StudentService studentService;

    private final LessonService lessonService;

    public ParentNotificationServiceImpl(GradeService gradeService, MailService emailService, StudentService studentService, LessonService lessonService) {
        this.gradeService = gradeService;
        this.emailService = emailService;
        this.studentService = studentService;
        this.lessonService = lessonService;
    }

    @Override
    public void sendDailyGradesReport() {
        LocalDate today = LocalDate.now();
        List<StudentResponseDto> students = studentService.getAllStudents(Pageable.unpaged()).getContent();

        for (StudentResponseDto student : students) {
            Long studentId = student.getId();
            String parentEmail = studentService.getParentEmailByStudentId(studentId);

            if (parentEmail != null) {
                List<GradeDto> grades = gradeService.getGradesByStudentIdAndDate(studentId, today);

                if (!grades.isEmpty()) {
                    String report = createReportForStudent(grades);
                    emailService.sendEmail(parentEmail, "Щоденний звіт оцінок", report);
                }
            }
        }
    }

    @Override
    public String createReportForStudent(List<GradeDto> grades) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Щоденний звіт оцінок:\n");
        for (GradeDto grade : grades) {
            Optional<LessonDto> lessonOpt = lessonService.getLesson(grade.getLessonId());
            String lessonName = lessonOpt.get().getName();
            reportBuilder.append(String.format("%s: %d\n", lessonName, grade.getGradeValue()));
        }
        return reportBuilder.toString();
    }

    public void sendAttendanceNotification(String to, String message) {
        String subject = "Attendance Notification";
        emailService.sendEmail(to, subject, message);
    }
}


