package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ParentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.ParentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Parent;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.repository.ParentRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.ParentServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ParentServiceTest {
    @InjectMocks
    ParentServiceImpl parentService;

    @Mock
    ParentRepository parentRepository;

    @Mock
    StudentRepository studentRepository;

    @Test
    void testAddNewParents() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFirstName("Parent Name");
        parent.setLastName("Parent Surname");
        parent.setEmail("parent@gmail.com");

        Student student1 = new Student();
        student1.setId(101L);

        Student student2 = new Student();
        student2.setId(102L);

        parent.setChildren(Arrays.asList(student1, student2));

        ParentRequestDto parentRequestDto = new ParentRequestDto("Parent Name", "Parent Surname", "parent@gmail.com", Arrays.asList(101L, 102L));

        when(parentRepository.save(any())).thenReturn(parent);

        ParentResponseDto responseDto = parentService.addParent(parentRequestDto);

        assertEquals(parent.getId(), responseDto.id());
        assertEquals(parent.getFirstName(), responseDto.firstName());
        assertEquals(parent.getLastName(), responseDto.lastName());
        assertEquals(parent.getEmail(), responseDto.email());
        assertEquals(Arrays.asList(101L, 102L), responseDto.childrenId());

        verify(parentRepository, times(1)).save(any());
    }

    @Test
    void testGetAllParents() {
        Pageable pageable = PageRequest.of(0, 10);

        ParentResponseDto parentResponseDto1 = new ParentResponseDto(1L, "Parent1", "Parent1", "parent1@gmail.com", List.of(1L));
        ParentResponseDto parentResponseDto2 = new ParentResponseDto(2L, "Parent2", "Parent2", "parent2@gmail.com", List.of(2L));

        Parent parent1 = new Parent();
        parent1.setId(1L);
        parent1.setFirstName("Parent1");
        parent1.setLastName("Parent1");
        parent1.setEmail("parent1@gmail.com");
        Student child1 = new Student();
        child1.setId(1L);
        parent1.setChildren(List.of(child1));

        Parent parent2 = new Parent();
        parent2.setId(2L);
        parent2.setFirstName("Parent2");
        parent2.setLastName("Parent2");
        parent2.setEmail("parent2@gmail.com");
        Student child2 = new Student();
        child2.setId(2L);
        parent2.setChildren(List.of(child2));

        Page<ParentResponseDto> exceptedResponse = new PageImpl<>(List.of(parentResponseDto1, parentResponseDto2));

        when(parentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(parent1, parent2)));

        Page<ParentResponseDto> actualResponse = parentService.getAllParents(PageRequest.of(0, 10));

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getTotalElements()).isEqualTo(2);
        assertThat(actualResponse).isEqualTo(exceptedResponse);

        verify(parentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetParentById() {
        Long parentId = 1L;

        Parent parent1 = new Parent();
        parent1.setId(parentId);
        parent1.setFirstName("ParentFirstName");
        parent1.setLastName("ParentLastName");
        parent1.setEmail("parent@gmail.com");
        Student student = new Student();
        student.setId(1L);
        parent1.setChildren(List.of(student));

        ParentResponseDto exceptedResponse = new ParentResponseDto(parentId, "ParentFirstName", "ParentLastName", "parent@gmail.com", List.of(1L));

        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent1));

        ParentResponseDto actualResponse = parentService.getParentById(parentId);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(exceptedResponse);

        verify(parentRepository, times(1)).findById(parentId);
    }

    @Test
    void testGetParentById_notFound() {
        Long parentId = 1L;

        when(parentRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.getParentById(parentId)).isInstanceOf(ResponseStatusException.class);

        verify(parentRepository, times(1)).findById(parentId);
    }

    @Test
    void testUpdateParentById() {
        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFirstName("Parent Name");
        parent.setLastName("Parent Surname");
        parent.setEmail("parent@example.com");

        Student student1 = new Student();
        student1.setId(101L);

        Student student2 = new Student();
        student2.setId(102L);

        parent.setChildren(Arrays.asList(student1, student2));

        ParentRequestDto parentRequestDto = new ParentRequestDto("John", "Doe", "john.doe@example.com", Arrays.asList(101L, 102L));

        when(parentRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(studentRepository.findByIds(Arrays.asList(101L, 102L))).thenReturn(parent.getChildren());
        when(parentRepository.save(any())).thenReturn(parent);

        ParentResponseDto responseDto = parentService.updateParentById(1L, parentRequestDto);

        assertEquals(parent.getId(), responseDto.id());
        assertEquals(parentRequestDto.firstName(), responseDto.firstName());
        assertEquals(parentRequestDto.lastName(), responseDto.lastName());
        assertEquals(parentRequestDto.email(), responseDto.email());
        assertEquals(parentRequestDto.childrenId(), responseDto.childrenId());

        verify(parentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).findByIds(Arrays.asList(101L, 102L));
        verify(parentRepository, times(1)).save(any());
    }

    @Test
    void testDeleteParentById() {
        Long parentId = 1L;


        when(parentRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parentService.getParentById(parentId))
                .isInstanceOf(ResponseStatusException.class);

        verify(parentRepository, times(1)).findById(parentId);
    }
}