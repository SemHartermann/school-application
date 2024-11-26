package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import co.inventorsoft.academy.schoolapplication.service.GradeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeController {

    GradeService gradeService;

    @PostMapping("/grades")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public GradeDto addGrade(@RequestBody @Valid GradeDto gradeDto) {
        return gradeService.addGrade(gradeDto);
    }

    @PostMapping("/grades/modules/{moduleId}/students/{studentId}")
    public Grade addModuleGrade(@PathVariable Long studentId,
                                @PathVariable Long moduleId,
                                Pageable pageable) {
        return gradeService.addModuleGrade(studentId, moduleId, pageable);
    }

    @GetMapping("/grades/{gradeId}")

    public GradeDto getGradeById(@PathVariable Long gradeId) {
        return gradeService.getGradeById(gradeId);
    }

    @GetMapping("/students/{studentId}/grades")
    public Page<GradeDto> getAllGradesByStudentId(@PathVariable Long studentId, Pageable pageable) {
        return gradeService.getGradesByStudentId(studentId, pageable);
    }

    @GetMapping("/lessons/{lessonId}/grades")
    public Page<GradeDto> getAllGradesByLessonId(@PathVariable Long lessonId, Pageable pageable) {
        return gradeService.getGradesByLessonId(lessonId, pageable);
    }

    @GetMapping("/modules/{moduleId}/grades")
    public Page<GradeDto> getAllGradesByModuleId(@PathVariable Long moduleId, Pageable pageable) {
        return gradeService.getGradesByModuleId(moduleId, pageable);
    }

    @GetMapping("/modules/{moduleId}/students/{studentId}/grades")
    public Page<GradeDto> getAllGradesByModuleIdAndStudentId(@PathVariable Long moduleId,
                                                             @PathVariable Long studentId,
                                                             Pageable pageable) {
        return gradeService.getGradesByModuleIdAndStudentId(moduleId, studentId, pageable);
    }

    @GetMapping("/lessons/{lessonId}/students/{studentId}/grades")
    public Page<GradeDto> getAllGradesByLessonIdAndStudentId(@PathVariable Long lessonId,
                                                             @PathVariable Long studentId,
                                                             Pageable pageable) {
        return gradeService.getGradesByLessonIdAndStudentId(lessonId, studentId, pageable);
    }

    @PutMapping("/grades/{gradeId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public GradeDto updateGrade(@RequestBody @Valid GradeDto gradeDto, @PathVariable Long gradeId) {
        return gradeService.updateGrade(gradeId, gradeDto);
    }

    @DeleteMapping("/grades/{gradeId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public void deleteGrade(@PathVariable Long gradeId) {
        gradeService.deleteGrade(gradeId);
    }
}
