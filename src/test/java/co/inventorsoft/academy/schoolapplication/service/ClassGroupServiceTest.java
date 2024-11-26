package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.repository.ClassGroupRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.ClassGroupServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class ClassGroupServiceTest {
    @Mock
    private ClassGroupRepository classGroupRepository;
    @Mock
    private StudentRepository studentRepository;
    @InjectMocks
    private ClassGroupServiceImpl classGroupService;

    @Test
    void createCorrectClassGroupTest() {
        RequestClassGroupDto requestDto = new RequestClassGroupDto();
        requestDto.setName("10-A");

        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(1L);
        classGroup.setName("10-A");
        classGroup.setDeleted(false);

        when(classGroupRepository.save(any(ClassGroup.class)))
                .thenReturn(classGroup);

        ResponseClassGroupDto createdResponseDto = classGroupService.createClassGroup(requestDto);

        assertThat(createdResponseDto.getName()).isEqualTo("10-A");
        verify(classGroupRepository, times(1)).save(any(ClassGroup.class));
    }

    @Test
    void getClassGroupByIdTest() {
        Long id = 1L;
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(id);
        classGroup.setName("10-A");

        when(classGroupRepository.findById(id))
                .thenReturn(Optional.of(classGroup));

        ResponseClassGroupDto foundClassGroupDto = classGroupService.getClassGroupById(id);

        assertThat(foundClassGroupDto.getId()).isEqualTo(id);
        assertThat(foundClassGroupDto.getName()).isEqualTo("10-A");
    }


    @Test
    void deleteTest() {
        Long id = 1L;
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(id);

        when(classGroupRepository.findById(id)).thenReturn(Optional.of(classGroup));
        when(classGroupRepository.save(any(ClassGroup.class))).thenReturn(classGroup);

        classGroupService.delete(id);

        assertTrue(classGroup.isDeleted());
        verify(classGroupRepository, times(1)).save(classGroup);
    }

    @Test
    void getAllTest() {
        Pageable pageable = mock(Pageable.class);
        ClassGroup classGroup = new ClassGroup();
        List<ClassGroup> classGroupList = List.of(classGroup);
        Page<ClassGroup> classGroupPage = new PageImpl<>(classGroupList, pageable, classGroupList.size());

        when(classGroupRepository.findAll(pageable)).thenReturn(classGroupPage);

        Page<ResponseClassGroupDto> dtoPage = classGroupService.getAll(pageable);

        assertThat(dtoPage).isNotNull();
        assertThat(dtoPage.getContent()).hasSize(1);
        verify(classGroupRepository).findAll(pageable);
    }

    @Test
    void deleteWhenClassGroupNotFound() {
        Long id = 1L;
        when(classGroupRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            classGroupService.delete(id);
        });

        String expectedMessage = String.format("Class group with Id %s was not found", id);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(classGroupRepository, never()).save(any(ClassGroup.class));
    }

    @Test
    void updateTest() {
        Long id = 1L;
        RequestClassGroupDto requestDto = new RequestClassGroupDto();
        requestDto.setId(id);
        requestDto.setName("11-B");

        ClassGroup existingClassGroup = new ClassGroup();
        existingClassGroup.setId(id);
        existingClassGroup.setName("10-A");

        when(classGroupRepository.findById(id)).thenReturn(Optional.of(existingClassGroup));
        when(classGroupRepository.save(any(ClassGroup.class))).thenReturn(existingClassGroup);

        ResponseClassGroupDto updatedClassGroupDto = classGroupService.update(requestDto);

        assertThat("11-B").isEqualTo(updatedClassGroupDto.getName());
        verify(classGroupRepository, times(1)).save(existingClassGroup);
    }

    @Test
    void addStudentToClassGroup() {
        Long classGroupId = 1L;
        Long studentId = 2L;
        ClassGroup classGroup = new ClassGroup();
        classGroup.setId(classGroupId);
        classGroup.setStudents(new ArrayList<>());
        Student student = new Student();

        when(classGroupRepository.findById(anyLong())).thenReturn(Optional.of(classGroup));
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        classGroupService.addStudentToClassGroup(classGroupId, studentId);

        assertThat(classGroup.getStudents()).contains(student);
    }
}