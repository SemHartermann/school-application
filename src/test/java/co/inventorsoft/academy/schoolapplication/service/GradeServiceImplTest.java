package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import co.inventorsoft.academy.schoolapplication.repository.GradeRepository;
import co.inventorsoft.academy.schoolapplication.repository.LessonRepository;
import co.inventorsoft.academy.schoolapplication.repository.ModuleRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.GradeServiceImpl;
import co.inventorsoft.academy.schoolapplication.service.impl.LessonServiceImpl;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class GradeServiceImplTest {

    @Mock
    GradeRepository gradeRepository;

    @Mock
    StudentRepository studentRepository;

    @Mock
    LessonRepository lessonRepository;

    @Mock
    ModuleRepository moduleRepository;

    @Mock
    LessonServiceImpl lessonService;

    @InjectMocks
    GradeServiceImpl gradeService;

    @Test
    void addGradeTest() {
        Long studentId = 1L;
        Long lessonId = 2L;

        Student student = new Student();
        student.setId(studentId);
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        GradeDto expectedGradeDto = new GradeDto(studentId, lessonId, 12, GradeType.TEST, ZonedDateTime.now());

        when(studentRepository.getReferenceById(student.getId())).thenReturn(student);
        when(lessonRepository.getReferenceById(expectedGradeDto.getLessonId())).thenReturn(lesson);


        GradeDto actualGradeDto = gradeService.addGrade(expectedGradeDto);

        assertThat(expectedGradeDto.getLessonId()).isEqualTo(actualGradeDto.getLessonId());
        assertThat(expectedGradeDto.getStudentId()).isEqualTo(actualGradeDto.getStudentId());
        assertThat(expectedGradeDto.getGradeValue()).isEqualTo(actualGradeDto.getGradeValue());
        assertThat(expectedGradeDto.getGradeType()).isEqualTo(actualGradeDto.getGradeType());

        verify(studentRepository, times(1)).getReferenceById(expectedGradeDto.getStudentId());
        verify(lessonRepository, times(1)).getReferenceById(expectedGradeDto.getLessonId());
    }

    @Test
    void addModuleGradeTest() {
        Long studentId = 1L;
        Long moduleId = 2L;

        Student student = new Student();
        student.setId(studentId);

        Module module = new Module();
        module.setId(moduleId);
        ZonedDateTime moduleGradeDate = ZonedDateTime.of(2023, 12, 1, 8, 0, 0, 0 ,ZoneId.of("Europe/Kiev"));
        module.setEndDate(moduleGradeDate);

        Lesson lessonFirst = new Lesson();
        lessonFirst.setModuleId(moduleId);
        Lesson lessonSecond = new Lesson();
        lessonSecond.setModuleId(moduleId);
        Lesson lessonThird = new Lesson();
        lessonThird.setModuleId(moduleId);

        List<Grade> gradeList = List.of(
                new Grade(student, lessonFirst, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(student, lessonSecond, 7, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(student, lessonThird, 9, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByModuleIdAndStudentId(moduleId, studentId, pageable)).thenReturn(gradePage);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        Grade exceptedModuleGrade = new Grade(student, null, 9, GradeType.MODULE, module.getEndDate());

        Grade actualModuleGrade = gradeService.addModuleGrade(moduleId, studentId, pageable);

        assertThat(actualModuleGrade.getStudent()).isEqualTo(exceptedModuleGrade.getStudent());
        assertThat(actualModuleGrade.getGradeValue()).isEqualTo(exceptedModuleGrade.getGradeValue());
        assertThat(actualModuleGrade.getGradeType()).isEqualTo(exceptedModuleGrade.getGradeType());
        assertThat(actualModuleGrade.getGradeDate()).isEqualTo(exceptedModuleGrade.getGradeDate());
    }

    @Test
    void getGradeByIdTest() {
        Long studentId = 1L;
        Long lessonId = 2L;
        Long gradeId = 3L;

        Student student = new Student();
        student.setId(studentId);
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);
        Grade expectedGrade = new Grade(student, lesson, 12, GradeType.TEST, ZonedDateTime.now());
        expectedGrade.setId(gradeId);

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(expectedGrade));

        GradeDto actualGradeDto = gradeService.getGradeById(gradeId);

        assertThat(expectedGrade.getStudent().getId())
                .isEqualTo(actualGradeDto.getStudentId());
        assertThat(expectedGrade.getLesson().getId())
                .isEqualTo(actualGradeDto.getLessonId());
        assertThat(expectedGrade.getGradeValue())
                .isEqualTo(actualGradeDto.getGradeValue());
        assertThat(expectedGrade.getGradeType())
                .isEqualTo(actualGradeDto.getGradeType());

        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    void getAllGradesByStudentIdTest() {
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);

        Lesson lessonFirst = new Lesson();
        lessonFirst.setId(4L);
        Lesson lessonSecond = new Lesson();
        lessonSecond.setId(5L);
        Lesson lessonThird = new Lesson();
        lessonThird.setId(5L);

        List<Grade> gradeList = List.of(
                new Grade(student, lessonFirst, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(student, lessonSecond, 8, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(student, lessonThird, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByStudentId(studentId, pageable)).thenReturn(gradePage);

        List<GradeDto> gradeDtoList = gradeService.getGradesByStudentId(studentId, pageable).toList();

        assertThat(gradeDtoList.size()).isEqualTo(gradeList.size());

        for (int i = 0; i < gradeDtoList.size(); i++) {
            assertThat(gradeList.get(i).getStudent().getId())
                    .isEqualTo(gradeDtoList.get(i).getStudentId());
            assertThat(gradeList.get(i).getLesson().getId())
                    .isEqualTo(gradeDtoList.get(i).getLessonId());
            assertThat(gradeList.get(i).getGradeValue())
                    .isEqualTo(gradeDtoList.get(i).getGradeValue());
        }

        verify(gradeRepository, times(1)).getGradesByStudentId(studentId, pageable);
    }

    @Test
    void getAllGradesByLessonIdTest() {
        Long lessonId = 1L;
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);

        Student studentFirst = new Student();
        studentFirst.setId(4L);
        Student studentSecond = new Student();
        studentSecond.setId(5L);
        Student studentThird = new Student();
        studentThird.setId(6L);

        List<Grade> gradeList = List.of(
                new Grade(studentFirst, lesson, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(studentFirst, lesson, 8, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(studentFirst, lesson, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByLessonId(lessonId, pageable)).thenReturn(gradePage);

        List<GradeDto> gradeDtoList = gradeService.getGradesByLessonId(lessonId, pageable).toList();

        assertThat(gradeDtoList.size()).isEqualTo(gradeList.size());

        for (int i = 0; i < gradeDtoList.size(); i++) {
            assertThat(gradeList.get(i).getStudent().getId())
                    .isEqualTo(gradeDtoList.get(i).getStudentId());
            assertThat(gradeList.get(i).getLesson().getId())
                    .isEqualTo(gradeDtoList.get(i).getLessonId());
            assertThat(gradeList.get(i).getGradeValue())
                    .isEqualTo(gradeDtoList.get(i).getGradeValue());
        }

        verify(gradeRepository, times(1)).getGradesByLessonId(lessonId, pageable);
    }

    @Test
    void getAllGradesByModuleIdTest() {
        Long moduleId = 1L;
        Long gradeListSize = 3L;

        List<Grade> gradeList = new ArrayList<>();
        Integer gradeValue = 9;

        for (Long i = 0L; i < gradeListSize; i++) {
            Lesson lesson = new Lesson();
            lesson.setId(i);

            Student student = new Student();
            student.setId(i);
            gradeList.add(new Grade(student, lesson, ++gradeValue, GradeType.MODULE, ZonedDateTime.now()));
        }

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByModuleId(moduleId, pageable)).thenReturn(gradePage);

        List<GradeDto> gradeDtoList = gradeService.getGradesByModuleId(moduleId, pageable).toList();

        assertThat(gradeDtoList.size()).isEqualTo(gradeList.size());

        for (int i = 0; i < gradeDtoList.size(); i++) {
            assertThat(gradeList.get(i).getStudent().getId())
                    .isEqualTo(gradeDtoList.get(i).getStudentId());
            assertThat(gradeList.get(i).getLesson().getId())
                    .isEqualTo(gradeDtoList.get(i).getLessonId());
            assertThat(gradeList.get(i).getGradeValue())
                    .isEqualTo(gradeDtoList.get(i).getGradeValue());
        }

        verify(gradeRepository, times(1)).getGradesByModuleId(moduleId, pageable);
    }

    @Test
    void getAllGradesByModuleIdAndStudentIdTest() {
        Long studentId = 1L;
        Long moduleId = 2L;

        Student student = new Student();
        student.setId(studentId);

        Lesson lessonFirst = new Lesson();
        lessonFirst.setId(4L);
        Lesson lessonSecond = new Lesson();
        lessonSecond.setId(5L);
        Lesson lessonThird = new Lesson();
        lessonThird.setId(5L);

        List<Grade> gradeList = List.of(
                new Grade(student, lessonFirst, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(student, lessonSecond, 8, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(student, lessonThird, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByModuleIdAndStudentId(moduleId, studentId, pageable)).thenReturn(gradePage);

        List<GradeDto> gradeDtoList = gradeService.
                getGradesByModuleIdAndStudentId(moduleId, studentId, pageable).
                toList();

        for (int i = 0; i < gradeDtoList.size(); i++) {
            assertThat(gradeList.get(i).getStudent().getId())
                    .isEqualTo(gradeDtoList.get(i).getStudentId());
            assertThat(gradeList.get(i).getLesson().getId())
                    .isEqualTo(gradeDtoList.get(i).getLessonId());
            assertThat(gradeList.get(i).getGradeValue())
                    .isEqualTo(gradeDtoList.get(i).getGradeValue());
        }

        verify(gradeRepository, times(1)).
                getGradesByModuleIdAndStudentId(moduleId, studentId, pageable);
    }

    @Test
    void getAllGradesByLessonIdAndStudentIdTest() {
        Long studentId = 1L;
        Long lessonId = 2L;

        Student student = new Student();
        student.setId(studentId);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId);

        List<Grade> gradeList = List.of(
                new Grade(student, lesson, 10, GradeType.TEST, ZonedDateTime.now()),
                new Grade(student, lesson, 8, GradeType.MINI_TEST, ZonedDateTime.now()),
                new Grade(student, lesson, 7, GradeType.HOMEWORK, ZonedDateTime.now())
        );

        Pageable pageable = Pageable.ofSize(gradeList.size());

        Page<Grade> gradePage = new PageImpl<>(gradeList,
                pageable,
                gradeList.size());

        when(gradeRepository.getGradesByLessonIdAndStudentId(lessonId, studentId, pageable))
                .thenReturn(gradePage);

        List<GradeDto> gradeDtoList = gradeService.
                getGradesByLessonIdAndStudentId(lessonId, studentId, pageable).
                toList();

        assertThat(gradeDtoList.size()).isEqualTo(gradeList.size());

        for (int i = 0; i < gradeDtoList.size(); i++) {
            assertThat(gradeList.get(i).getStudent().getId())
                    .isEqualTo(gradeDtoList.get(i).getStudentId());
            assertThat(gradeList.get(i).getLesson().getId())
                    .isEqualTo(gradeDtoList.get(i).getLessonId());
            assertThat(gradeList.get(i).getGradeValue())
                    .isEqualTo(gradeDtoList.get(i).getGradeValue());
        }

        verify(gradeRepository, times(1)).
                getGradesByLessonIdAndStudentId(lessonId, studentId, pageable);
    }

    @Test
    void updateGradeTest() {
        Integer oldGradeValue = 10;
        Integer newGradeValue = 8;
        Long gradeId = 7L;
        Grade grade = new Grade(new Student(), new Lesson(), oldGradeValue, GradeType.HOMEWORK, ZonedDateTime.now());

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        GradeDto gradeDto = gradeService.updateGrade(gradeId, new GradeDto(1L,
                1L,
                newGradeValue,
                GradeType.TEST,
                ZonedDateTime.now()));

        assertThat(gradeDto).isNotNull();
        assertThat(grade.getGradeValue()).isEqualTo(gradeDto.getGradeValue());

        verify(gradeRepository, times(1)).findById(gradeId);
    }

    @Test
    void deleteGradeTest() {
        Long gradeId = 10L;

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        gradeService.deleteGrade(gradeId);

        assertThatThrownBy(() -> gradeService.getGradeById(gradeId))
                .isInstanceOf(NoSuchElementException.class);
    }
}