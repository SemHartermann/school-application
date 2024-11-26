package co.inventorsoft.academy.schoolapplication.dto.attendance;

import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;

public record AttendanceResponseDto(
        Long id,
        Long lessonId,
        Long studentId,
        AttendanceType attendanceType
) {
}
