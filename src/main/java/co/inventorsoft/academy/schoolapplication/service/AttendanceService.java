package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttendanceService {
    AttendanceResponseDto addNewAttendance(AttendanceRequestDto attendanceRequestDto);

    Page<AttendanceResponseDto> getAllAttendances(Pageable pageable);

    AttendanceResponseDto getAttendanceById(Long id);

    AttendanceResponseDto updateAttendanceById(Long id, AttendanceRequestDto attendanceRequestDto);

    void deleteAttendanceById(Long id);

    Page<AttendanceResponseDto> getAttendancesByLessonId(Long lessonId, Pageable pageable);

    void createAttendancesForLesson(Long lessonId, Long classGroupId);

    void bulkEdit(List<Long> attendanceIds, AttendanceType attendanceType);

    void publishAttendanceEvent(Attendance attendance);
}
