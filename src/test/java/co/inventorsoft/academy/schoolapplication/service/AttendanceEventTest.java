package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import co.inventorsoft.academy.schoolapplication.event.AttendanceEvent;
import co.inventorsoft.academy.schoolapplication.listener.AttendanceEventListener;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceEventTest {

    @Mock
    private ParentNotificationService reportService;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private AttendanceEventListener attendanceEventListener;

    @Test
    public void shouldHandleAttendanceEvent() {
        // Arrange
        Attendance mockAttendance = createMockAttendance();
        AttendanceEvent attendanceEvent = new AttendanceEvent(this, mockAttendance);
        Long studentId = mockAttendance.getStudent().getId();
        String parentEmail = "parent@example.com";

        when(studentRepository.findParentEmailByStudentId(studentId)).thenReturn(Optional.of(parentEmail));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockAttendance.getStudent()));

        // Act
        attendanceEventListener.onAttendanceEvent(attendanceEvent);

        // Assert
        verify(reportService).sendAttendanceNotification(eq(parentEmail), anyString());
    }

    @Test
    public void shouldHandleExceptionInAttendanceEvent() {
        // Arrange
        Attendance mockAttendance = createMockAttendance();
        AttendanceEvent attendanceEvent = new AttendanceEvent(this, mockAttendance);
        Long studentId = mockAttendance.getStudent().getId();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(mockAttendance.getStudent()));
        doThrow(new RuntimeException("Database access error")).when(studentRepository).findParentEmailByStudentId(studentId);

        // Act
        assertDoesNotThrow(() -> attendanceEventListener.onAttendanceEvent(attendanceEvent));

        // Verify
        verify(reportService, never()).sendAttendanceNotification(anyString(), anyString());
    }
    private Student createMockStudent() {
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("John");
        mockStudent.setLastName("Doe");
        mockStudent.setEmail("john.doe@example.com");
        mockStudent.setPhone("123-456-7890");
        return mockStudent;
    }

    private Attendance createMockAttendance() {
        Attendance mockAttendance = new Attendance();
        mockAttendance.setLesson(createMockLesson());
        mockAttendance.setStudent(createMockStudent());
        mockAttendance.setAttendanceType(AttendanceType.ABSENT);
        mockAttendance.setDeleted(false);
        return mockAttendance;
    }

    private Lesson createMockLesson() {
        Lesson mockLesson = new Lesson();
        mockLesson.setId(1L);
        return mockLesson;
    }
}