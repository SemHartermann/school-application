package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {
    Page<TeacherDto> getAllTeachers(Pageable pageable);

    TeacherDto getTeacherById(Long id);

    void addNewTeacher(TeacherDto teacherDto);

    void updateTeacherById(Long id, TeacherDto teacherDto);

    void deleteTeacherById(Long id);
}
