package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import co.inventorsoft.academy.schoolapplication.event.AttendanceEvent;
import co.inventorsoft.academy.schoolapplication.mapper.AttendanceMapper;
import co.inventorsoft.academy.schoolapplication.repository.AttendanceRepository;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.AttendanceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceServiceImpl implements AttendanceService {
    AttendanceRepository attendanceRepository;
    StudentRepository studentRepository;
    LessonRepository lessonRepository;
    ApplicationEventPublisher eventPublisher;

    AttendanceMapper attendanceMapper = AttendanceMapper.MAPPER;

    @Override
    public AttendanceResponseDto addNewAttendance(AttendanceRequestDto attendanceRequestDto) {
        Attendance entity = attendanceMapper.toEntity(attendanceRequestDto);

        lessonRepository.findById(attendanceRequestDto.lessonId()).ifPresent(entity::setLesson);
        studentRepository.findById(attendanceRequestDto.studentId()).ifPresent(entity::setStudent);

        Attendance savedAttendance = attendanceRepository.save(entity);

        if (savedAttendance.getAttendanceType() == AttendanceType.ABSENT) {
            publishAttendanceEvent(savedAttendance);
        }

        return attendanceMapper.toResponseDto(savedAttendance);
    }

    @Override
    public Page<AttendanceResponseDto> getAllAttendances(Pageable pageable) {
        Page<Attendance> attendances = attendanceRepository.findAll(pageable);

        return attendances.map(attendanceMapper::toResponseDto);
    }


    @Override
    public AttendanceResponseDto getAttendanceById(Long id) {
        Attendance existingStudent = getExistingAttendanceById(id);

        return attendanceMapper.toResponseDto(existingStudent);
    }

    @Override
    public AttendanceResponseDto updateAttendanceById(Long id, AttendanceRequestDto attendanceRequestDto) {
        Attendance entity = getExistingAttendanceById(id);

        AttendanceType originalType = entity.getAttendanceType();
        attendanceMapper.updateEntity(attendanceRequestDto, entity);
        Attendance updatedAttendance = attendanceRepository.save(entity);

        if (originalType != AttendanceType.ABSENT && updatedAttendance.getAttendanceType() == AttendanceType.ABSENT) {
            publishAttendanceEvent(updatedAttendance);
        }

        return attendanceMapper.toResponseDto(updatedAttendance);
    }

    @Override
    public void deleteAttendanceById(Long id) {
        attendanceRepository.findById(id).ifPresent(attendance -> {
            attendance.setDeleted(true);
            attendanceRepository.save(attendance);
        });
    }

    @Override
    public Page<AttendanceResponseDto> getAttendancesByLessonId(Long lessonId, Pageable pageable) {
        Page<Attendance> attendances = attendanceRepository.findAttendancesByLessonId(pageable, lessonId);

        return attendances.map(attendanceMapper::toResponseDto);
    }

    @Override
    public void createAttendancesForLesson(Long lessonId, Long classGroupId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));

        List<Student> students = studentRepository.findAllByClassGroupId(classGroupId);

        for (Student student : students) {
            Attendance attendance = new Attendance();
            attendance.setLesson(lesson);
            attendance.setStudent(student);
            attendance.setAttendanceType(AttendanceType.UNDEFINED);
            attendanceRepository.save(attendance);
        }
    }

    @Override
    public void bulkEdit(List<Long> attendanceIds, AttendanceType attendanceType) {
        List<Attendance> attendances = attendanceRepository.findAllById(attendanceIds);

        for (Attendance attendance : attendances) {
            attendance.setAttendanceType(attendanceType);
            attendanceRepository.save(attendance);

            if (attendanceType == AttendanceType.ABSENT) {
                publishAttendanceEvent(attendance);
            }
        }
    }

    @Override
    public void publishAttendanceEvent(Attendance attendance) {
        AttendanceEvent attendanceEvent = new AttendanceEvent(this, attendance);
        eventPublisher.publishEvent(attendanceEvent);
    }

    private Attendance getExistingAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found"));
    }
}
