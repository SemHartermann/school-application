package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import co.inventorsoft.academy.schoolapplication.mapper.GradeMapper;
import co.inventorsoft.academy.schoolapplication.mapper.LessonMapper;
import co.inventorsoft.academy.schoolapplication.repository.GradeRepository;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.repository.ModuleRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.GradeService;
import co.inventorsoft.academy.schoolapplication.service.LessonService;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.factory.Mappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    LessonService lessonService;
    GradeRepository gradeRepository;
    StudentRepository studentRepository;
    LessonRepository lessonRepository;
    ModuleRepository moduleRepository;
    GradeMapper gradeMapper = Mappers.getMapper(GradeMapper.class);
    LessonMapper lessonMapper = Mappers.getMapper(LessonMapper.class);

    @Transactional
    @Override
    public GradeDto addGrade(GradeDto gradeDto) {
        Student student = studentRepository.getReferenceById(gradeDto.getStudentId());
        Lesson lesson = lessonRepository.getReferenceById(gradeDto.getLessonId());
        checkIfGradeAlreadyExists(gradeDto);
        Grade grade = new Grade(student, lesson, gradeDto.getGradeValue(), gradeDto.getGradeType(), ZonedDateTime.now());
        try {
            gradeRepository.save(grade);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("lesson id or student id are not correct!");
        }

        return gradeMapper.toDto(grade);
    }

    private void checkIfGradeAlreadyExists(GradeDto gradeDto) {
        Optional<Grade> checkingGrade = gradeRepository.
                getGradeByLessonIdAndStudentIdAndGradeType(gradeDto.getLessonId(),
                        gradeDto.getStudentId(),
                        gradeDto.getGradeType());
        System.out.println(checkingGrade);
        if (checkingGrade.isPresent()) {
            throw new IllegalArgumentException("grade with such parameters already exists");
        }
    }

    @Override
    public Grade addModuleGrade(Long moduleId, Long studentId, Pageable pageable) {
        Grade moduleGrade = calculateModuleGrade(moduleId, studentId, pageable);
        gradeRepository.save(moduleGrade);
        return moduleGrade;
    }

    @Transactional(readOnly = true)
    @Override
    public GradeDto getGradeById(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId).orElseThrow();

        return gradeMapper.toDto(grade);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GradeDto> getGradesByStudentId(Long studentId, Pageable pageable) {
        return gradeRepository.getGradesByStudentId(studentId, pageable)
                .map(gradeMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GradeDto> getGradesByLessonId(Long lessonId, Pageable pageable) {
        return gradeRepository.getGradesByLessonId(lessonId, pageable)
                .map(gradeMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GradeDto> getGradesByModuleId(Long moduleId, Pageable pageable) {
        return gradeRepository.getGradesByModuleId(moduleId, pageable)
                .map(gradeMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GradeDto> getGradesByModuleIdAndStudentId(Long moduleId, Long studentId, Pageable pageable) {
        return gradeRepository.getGradesByModuleIdAndStudentId(moduleId, studentId, pageable)
                .map(gradeMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<GradeDto> getGradesByLessonIdAndStudentId(Long lessonId, Long studentId, Pageable pageable) {
        return gradeRepository.getGradesByLessonIdAndStudentId(lessonId, studentId, pageable)
                .map(gradeMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GradeDto> getGradesByStudentIdAndDate(Long studentId, LocalDate date) {
        List<Grade> gradeList = gradeRepository.findByStudentIdAndLessonDate(studentId, date);
        return gradeList.stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public GradeDto updateGrade(Long gradeId, GradeDto gradeDto) {
        Grade grade = gradeRepository.findById(gradeId).orElseThrow();

        if (!grade.getGradeType().equals(gradeDto.getGradeType())) {
            checkIfGradeAlreadyExists(gradeDto);
        }
        grade.setGradeValue(gradeDto.getGradeValue());
        grade.setGradeType(gradeDto.getGradeType());
        gradeRepository.save(grade);

        return gradeMapper.toDto(grade);
    }

    @Transactional
    @Override
    public void deleteGrade(Long gradeId) {
        gradeRepository.deleteById(gradeId);
    }

    private Grade calculateModuleGrade(Long moduleId ,Long studentId, Pageable pageable) {
        Page<GradeDto> gradesByModuleIdAndStudentId = getGradesByModuleIdAndStudentId(moduleId, studentId, pageable);
        Student student = studentRepository.findById(studentId).orElseThrow();

        Module module = moduleRepository.findById(moduleId).orElseThrow();

        ZonedDateTime moduleGradeDate = module.getEndDate();

        if (moduleGradeDate.isAfter(ZonedDateTime.now())) {
            throw new RuntimeException("Today isn't date for module grade");
        }


        Integer moduleGradeValue = Math.toIntExact(Math.round(gradesByModuleIdAndStudentId.stream()
                .map(GradeDto::getGradeValue)
                .mapToDouble(Integer::doubleValue)
                .average()
                .getAsDouble()));

        Grade moduleGrade = new Grade(student, null, moduleGradeValue, GradeType.MODULE, moduleGradeDate);

        return moduleGrade;
    }

}