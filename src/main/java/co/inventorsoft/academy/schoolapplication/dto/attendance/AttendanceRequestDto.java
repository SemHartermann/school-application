package co.inventorsoft.academy.schoolapplication.dto.attendance;

import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequestDto (
        @NotNull(message = "Lesson id can't be null")
        Long lessonId,

        @NotNull(message = "Student id can't be null")
        Long studentId,

        @NotNull(message = "Attendance type can't be null")
        AttendanceType attendanceType
) {
}
