package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import co.inventorsoft.academy.schoolapplication.repository.AttendanceRepository;
import co.inventorsoft.academy.schoolapplication.repository.ClassGroupRepository;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {
    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ClassGroupRepository classGroupRepository;

    @Test
    void testAddNewAttendance() {
        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        AttendanceRequestDto requestDto = new AttendanceRequestDto(1L, 1L, AttendanceType.LATE);
        AttendanceResponseDto expectedResponse = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.LATE);

        ArgumentCaptor<Attendance> attendanceArgumentCaptor = ArgumentCaptor.forClass(Attendance.class);

        when(lessonRepository.findById(requestDto.lessonId())).thenReturn(Optional.of(lesson1));
        when(studentRepository.findById(requestDto.studentId())).thenReturn(Optional.of(student1));
        when(attendanceRepository.save(attendanceArgumentCaptor.capture())).thenAnswer(invocation -> {
            Attendance captorValue = attendanceArgumentCaptor.getValue();
            captorValue.setId(1L);
            return captorValue;
        });

        AttendanceResponseDto response = attendanceService.addNewAttendance(requestDto);

        assertThat(response).isEqualTo(expectedResponse);

        verify(attendanceRepository, times(1)).save(attendanceArgumentCaptor.capture());
    }

    @Test
    void testGetAllAttendances() {
        Pageable pageable = PageRequest.of(0, 10);

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane@example.com");
        student2.setPhone("9876543210");

        Attendance attendance1 = new Attendance();
        attendance1.setId(1L);
        attendance1.setLesson(lesson1);
        attendance1.setStudent(student1);
        attendance1.setAttendanceType(AttendanceType.PRESENT);

        Attendance attendance2 = new Attendance();
        attendance2.setId(2L);
        attendance2.setLesson(lesson1);
        attendance2.setStudent(student2);
        attendance2.setAttendanceType(AttendanceType.LATE);

        AttendanceResponseDto attendanceDto1 = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT);
        AttendanceResponseDto attendanceDto2 = new AttendanceResponseDto(2L, 1L, 2L, AttendanceType.LATE);

        Page<AttendanceResponseDto> expectedResponse = new PageImpl<>(List.of(
                attendanceDto1, attendanceDto2
        ));

        when(attendanceRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(
                attendance1, attendance2
        )));

        Page<AttendanceResponseDto> response = attendanceService.getAllAttendances(PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response).isEqualTo(expectedResponse);

        verify(attendanceRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAttendanceById() {
        Long attendanceId = 1L;

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Attendance existingAttendance = new Attendance();
        existingAttendance.setId(attendanceId);
        existingAttendance.setLesson(lesson1);
        existingAttendance.setStudent(student1);
        existingAttendance.setAttendanceType(AttendanceType.PRESENT);

        AttendanceResponseDto expectedResponse = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT);

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(existingAttendance));

        AttendanceResponseDto response = attendanceService.getAttendanceById(attendanceId);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(attendanceRepository, times(1)).findById(attendanceId);
    }

    @Test
    void testUpdateAttendanceById() {
        Long attendanceId = 1L;

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Attendance existingAttendance = new Attendance();
        existingAttendance.setId(attendanceId);
        existingAttendance.setLesson(lesson1);
        existingAttendance.setStudent(student1);
        existingAttendance.setAttendanceType(AttendanceType.PRESENT);

        AttendanceRequestDto requestDto = new AttendanceRequestDto(1L, 1L, AttendanceType.LATE);
        AttendanceResponseDto expectedResponse = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.LATE);

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceResponseDto response = attendanceService.updateAttendanceById(attendanceId, requestDto);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(attendanceRepository, times(1)).findById(attendanceId);
        verify(attendanceRepository, times(1)).save(existingAttendance);
    }

    @Test
    void testDeleteAttendanceById() {
        Long attendanceId = 1L;

        Attendance existingAttendance = new Attendance();

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(existingAttendance));

        attendanceService.deleteAttendanceById(attendanceId);

        assertThat(existingAttendance.isDeleted()).isTrue();

        verify(attendanceRepository, times(1)).findById(attendanceId);
    }

    @Test
    void testGetAttendanceById_NotFound() {
        Long attendanceId = 1L;

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.getAttendanceById(attendanceId))
                .isInstanceOf(ResponseStatusException.class);

        verify(attendanceRepository, times(1)).findById(attendanceId);
    }

    @Test
    void testGetAttendancesByLessonId() {
        Long lessonId = 1L;

        Pageable pageable = PageRequest.of(0, 10);

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane@example.com");
        student2.setPhone("9876543210");

        Attendance attendance1 = new Attendance();
        attendance1.setId(1L);
        attendance1.setLesson(lesson1);
        attendance1.setStudent(student1);
        attendance1.setAttendanceType(AttendanceType.PRESENT);

        Attendance attendance2 = new Attendance();
        attendance2.setId(2L);
        attendance2.setLesson(lesson1);
        attendance2.setStudent(student2);
        attendance2.setAttendanceType(AttendanceType.PRESENT);

        AttendanceResponseDto attendanceResponseDto1 = new AttendanceResponseDto(1L, 1L, 1L, AttendanceType.PRESENT);
        AttendanceResponseDto attendanceResponseDto2 = new AttendanceResponseDto(2L, 1L, 2L, AttendanceType.PRESENT);

        Page<AttendanceResponseDto> expectedResponse = new PageImpl<>(List.of(
                attendanceResponseDto1, attendanceResponseDto2
        ));

        when(attendanceRepository.findAttendancesByLessonId(pageable, lessonId)).thenReturn(new PageImpl<>(List.of(
                attendance1, attendance2
        )));

        Page<AttendanceResponseDto> response = attendanceService.getAttendancesByLessonId(lessonId, pageable);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void testCreateAttendancesForLesson() {
        Long lessonId = 1L;
        Long classGroupId = 1L;

        List<Student> students = List.of(new Student(), new Student());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(new Lesson()));
        when(studentRepository.findAllByClassGroupId(classGroupId)).thenReturn(students);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(new Attendance());

        attendanceService.createAttendancesForLesson(lessonId, classGroupId);

        verify(attendanceRepository, times(students.size())).save(any(Attendance.class));
    }

    @Test
    void testBulkEdit() {
        // Mock data
        List<Long> attendanceIds = Arrays.asList(1L, 2L, 3L);
        AttendanceType attendanceType = AttendanceType.PRESENT;

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setName("lesson 1");
        lesson1.setDate(ZonedDateTime.now());
        lesson1.setPeriod(1);
        lesson1.setHomework("Homework 1");
        lesson1.setModuleId(1L);

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane@example.com");
        student2.setPhone("9876543210");

        Student student3 = new Student();
        student2.setId(3L);
        student2.setFirstName("Tom");
        student2.setLastName("Keith");
        student2.setEmail("Tom@example.com");
        student2.setPhone("1234509876");

        Attendance attendance1 = new Attendance();
        attendance1.setId(1L);
        attendance1.setLesson(lesson1);
        attendance1.setStudent(student1);
        attendance1.setAttendanceType(AttendanceType.ABSENT);

        Attendance attendance2 = new Attendance();
        attendance2.setId(2L);
        attendance2.setLesson(lesson1);
        attendance2.setStudent(student2);
        attendance2.setAttendanceType(AttendanceType.ABSENT);

        Attendance attendance3 = new Attendance();
        attendance2.setId(3L);
        attendance2.setLesson(lesson1);
        attendance2.setStudent(student3);
        attendance2.setAttendanceType(AttendanceType.ABSENT);

        List<Attendance> attendances = Arrays.asList(
                attendance1, attendance2, attendance3
        );

        when(attendanceRepository.findAllById(attendanceIds)).thenReturn(attendances);
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        attendanceService.bulkEdit(attendanceIds, attendanceType);

        verify(attendanceRepository, times(attendanceIds.size())).save(any(Attendance.class));

        for (Attendance attendance : attendances) {
            verify(attendanceRepository).save(attendance);
            assertThat(attendanceType).isSameAs(attendance.getAttendanceType());
        }
    }
}