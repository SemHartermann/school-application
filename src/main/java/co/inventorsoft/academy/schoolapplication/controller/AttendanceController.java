package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.attendance.AttendanceResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.attendance.AttendanceType;
import co.inventorsoft.academy.schoolapplication.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendances")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {
    AttendanceService attendanceService;

    @GetMapping
    public Page<AttendanceResponseDto> getAllAttendances(@PageableDefault Pageable pageable) {
        return attendanceService.getAllAttendances(pageable);
    }

    @GetMapping("/{id}")
    public AttendanceResponseDto getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id);
    }

    @GetMapping("/lessons/{lessonId}")
    public Page<AttendanceResponseDto> getAttendancesByLessonId(
            @PathVariable Long lessonId,
            @PageableDefault Pageable pageable
    ) {
        return attendanceService.getAttendancesByLessonId(lessonId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponseDto addNewAttendance(@RequestBody @Valid AttendanceRequestDto attendanceRequestDto) {
        return attendanceService.addNewAttendance(attendanceRequestDto);
    }

    @PutMapping("/{id}")
    public AttendanceResponseDto updateAttendanceById(
            @PathVariable("id") Long id,
            @RequestBody @Valid AttendanceRequestDto attendanceRequestDto
    ) {
        return attendanceService.updateAttendanceById(id, attendanceRequestDto);
    }

    @PostMapping("/lessons/{lessonId}")
    public void createAttendancesForLesson(
            @PathVariable Long lessonId,
            @RequestParam(value = "classGroupId") String classGroupId
    ) {
        attendanceService.createAttendancesForLesson(lessonId, Long.valueOf(classGroupId));
    }

    @PostMapping("/attendances/bulkEdit")
    public void bulkEditAttendances(
            @RequestBody List<Long> attendanceIds,
            @RequestParam(value = "attendanceType") String attendanceType
    ) {
        attendanceService.bulkEdit(attendanceIds, AttendanceType.valueOf(attendanceType));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttendanceById(@PathVariable Long id) {
        attendanceService.deleteAttendanceById(id);
    }
}
