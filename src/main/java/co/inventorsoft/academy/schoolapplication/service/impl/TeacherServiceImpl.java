package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import co.inventorsoft.academy.schoolapplication.entity.Subject;
import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import co.inventorsoft.academy.schoolapplication.entity.TeacherSubjectClass;
import co.inventorsoft.academy.schoolapplication.mapper.TeacherMapper;
import co.inventorsoft.academy.schoolapplication.repository.TeacherRepository;
import co.inventorsoft.academy.schoolapplication.service.TeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    TeacherRepository teacherRepository;
    TeacherMapper teacherMapper = TeacherMapper.MAPPER;

    @Override
    public Page<TeacherDto> getAllTeachers(Pageable pageable) {
        Page<Teacher> teachers = teacherRepository.findAll(pageable);

        return teachers.map(t -> {
            toTeacherDtoMapHelper(t);
            return teacherMapper.toTeacherDto(t);
        });
    }

    @Override
    public TeacherDto getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow();

        toTeacherDtoMapHelper(teacher);

        return teacherMapper.toTeacherDto(teacher);
    }

    @Override
    public void addNewTeacher(TeacherDto teacherDto) {
        teacherRepository.save(teacherMapper.toTeacherEntity(teacherDto));
    }

    @Override
    public void updateTeacherById(Long id, TeacherDto teacherDto) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow();

        teacherMapper.mergeToTeacherEntity(teacherDto, teacher);

        teacherRepository.save(teacher);
    }

    @Override
    public void deleteTeacherById(Long id) {
        teacherRepository.deleteById(id);
    }

    private void toTeacherDtoMapHelper(Teacher teacher) {
        List<Subject> subjects = new ArrayList<>();
        List<ClassGroup> classes = new ArrayList<>();

        List<TeacherSubjectClass> tscList = teacher.getTeacherSubjectClasses();

        if (tscList != null) {
            tscList.forEach(tsc -> {
                subjects.add(tsc.getSubject());
                classes.add(tsc.getClassGroup());
            });
        }

        teacher.setSubjects(subjects);
        teacher.setClasses(classes);
    }
}
