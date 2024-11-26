package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import co.inventorsoft.academy.schoolapplication.service.impl.ParentNotificationServiceImpl;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParentNotificationServiceTest {

    @Mock
    private LessonService lessonService;
    @Mock
    private StudentService studentService;
    @Mock
    private GradeService gradeService;
    @Mock
    private MailService emailService;
    @InjectMocks
    private ParentNotificationServiceImpl reportService;

    @Captor
    private ArgumentCaptor<String> emailCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    @Test
    public void testCreateReportForStudent_WithValidGrades() {
        GradeDto grade1 = new GradeDto(1L, 101L, 5, GradeType.TEST, ZonedDateTime.now());
        GradeDto grade2 = new GradeDto(1L, 102L, 4, GradeType.HOMEWORK, ZonedDateTime.now());
        List<GradeDto> grades = List.of(grade1, grade2);

        when(lessonService.getLesson(101L)).thenReturn(Optional.of(buildLessonDto("Mathematics", 101L)));
        when(lessonService.getLesson(102L)).thenReturn(Optional.of(buildLessonDto("Physics", 102L)));

        String report = reportService.createReportForStudent(grades);

        assertNotNull(report);
        assertTrue(report.contains("Mathematics: 5"));
        assertTrue(report.contains("Physics: 4"));
    }

    @Test
    public void shouldSendEmailWhenGradesAreAvailable() {
        LocalDate today = LocalDate.now();
        StudentResponseDto student = new StudentResponseDto(1L, "John", "Doe", "john.doe@example.com", "12345678");
        GradeDto grade = new GradeDto(1L, 101L, 5, GradeType.TEST, ZonedDateTime.now());
        List<StudentResponseDto> students = List.of(student);

        when(studentService.getAllStudents(Pageable.unpaged())).thenReturn(new PageImpl<>(students));
        when(studentService.getParentEmailByStudentId(1L)).thenReturn("parent@example.com");
        when(gradeService.getGradesByStudentIdAndDate(1L, today)).thenReturn((List.of(grade)));
        when(lessonService.getLesson(101L)).thenReturn(Optional.of(buildLessonDto("Mathematics", 101L)));

        // Act
        reportService.sendDailyGradesReport();

        // Capture
        verify(emailService).sendEmail(emailCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

        // Assert
        assertEquals("parent@example.com", emailCaptor.getValue());
        assertEquals("Щоденний звіт оцінок", subjectCaptor.getValue());
        String expectedBody = "Щоденний звіт оцінок:\nMathematics: 5\n";
        assertEquals(expectedBody, bodyCaptor.getValue());
    }

    @Test
    public void shouldNotSendEmailWhenNoGradesAreAvailable() {
        LocalDate today = LocalDate.now();
        StudentResponseDto student = new StudentResponseDto(1L, "John", "Doe", "john.doe@example.com", "12345678");
        List<StudentResponseDto> students = List.of(student);

        when(studentService.getAllStudents(Pageable.unpaged())).thenReturn(new PageImpl<>(students));
        when(studentService.getParentEmailByStudentId(1L)).thenReturn("parent@example.com");
        when(gradeService.getGradesByStudentIdAndDate(1L, today)).thenReturn(Collections.emptyList());

        reportService.sendDailyGradesReport();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    private LessonDto buildLessonDto(String name, Long id) {
        return LessonDto.builder()
                .id(id)
                .name(name)
                .date(ZonedDateTime.now())
                .period(1)
                .moduleId(1L)
                .build();
    }
}
