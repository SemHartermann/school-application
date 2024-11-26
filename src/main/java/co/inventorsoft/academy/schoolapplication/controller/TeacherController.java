package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import co.inventorsoft.academy.schoolapplication.service.TeacherService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherController {

    TeacherService teacherService;

    @GetMapping
    public Page<TeacherDto> getAllTeachers(@PageableDefault Pageable pageable) {
        return teacherService.getAllTeachers(pageable);
    }

    @GetMapping("/{id}")
    public TeacherDto getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewTeacher(@RequestBody @Valid TeacherDto teacherDto) {
        teacherService.addNewTeacher(teacherDto);
    }

    @PutMapping("/{id}")
    public void updateTeacherById(@PathVariable Long id, @RequestBody @Valid TeacherDto teacherDto) {
        teacherService.updateTeacherById(id, teacherDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTeacherById(@PathVariable Long id) {
        teacherService.deleteTeacherById(id);
    }
}
