package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import co.inventorsoft.academy.schoolapplication.mapper.TeacherMapper;
import co.inventorsoft.academy.schoolapplication.repository.TeacherRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.TeacherServiceImpl;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherServiceImplTest {

    final TeacherMapper teacherLiveMapper = TeacherMapper.MAPPER;

    @Mock
    TeacherRepository teacherRepository;

    @InjectMocks
    TeacherServiceImpl teacherServiceImpl;


    @Test
    public void testGetAllTeachersReturnsAllTeachers() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<TeacherDto> mockTeacherDtoPage = new PageImpl<>(
                List.of(
                        getTeacherDto("Didi",
                                "Kira",
                                "dkira0@nsw.gov.au",
                                "672-852-2193"),

                        getTeacherDto("Kristo",
                                "Blacksland",
                                "kblacksland5@microsoft.com",
                                "342-115-7222"),

                        getTeacherDto("Matias",
                                "Sketch",
                                "msketch9@unc.edu",
                                "700-359-5577")
                )
        );

        List<Teacher> mockTeacherEntityList = mockTeacherDtoPage.stream()
                .map(teacherLiveMapper::toTeacherEntity)
                .toList();

        when(teacherRepository.findAll(pageable)).thenReturn(
                new PageImpl<>(mockTeacherEntityList)
        );

        Page<TeacherDto> actualResult = teacherServiceImpl.getAllTeachers(pageable);

        assertThat(mockTeacherDtoPage.getContent()).isEqualTo(actualResult.getContent());

        verify(teacherRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testThatGetTeacherByIdReturnsTeacherById() {
        long validId = 1L;

        TeacherDto mockTeacherDto = getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193");

        when(teacherRepository.findById(validId)).thenReturn(
                Optional.of(teacherLiveMapper.toTeacherEntity(mockTeacherDto))
        );

        TeacherDto actualResult = teacherServiceImpl.getTeacherById(validId);

        assertThat(mockTeacherDto).isEqualTo(actualResult);

        verify(teacherRepository).findById(validId);
    }

    @Test
    public void testThatGetTeacherByIdThrowsNoSuchElementExceptionForInvalidId() {
        long invalidId = -1L;

        assertThatThrownBy(() -> teacherServiceImpl.getTeacherById(invalidId))
                .isInstanceOf(NoSuchElementException.class);

        verify(teacherRepository).findById(invalidId);
    }

    @Test
    public void testThatAddNewTeacherAddsTeacher() {
        TeacherDto mockTeacherDto = getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193");

        teacherServiceImpl.addNewTeacher(mockTeacherDto);

        verify(teacherRepository).save(teacherLiveMapper.toTeacherEntity(mockTeacherDto));
    }

    @Test
    public void testThatUpdateTeacherByIdUpdatesTeacher() {
        long validId = 1L;

        TeacherDto mockTeacherDto = getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193");

        when(teacherRepository.findById(validId)).thenReturn(
                Optional.of(teacherLiveMapper.toTeacherEntity(mockTeacherDto))
        );

        mockTeacherDto.setFirstName("Diddi");
        mockTeacherDto.setLastName("Kirra");

        teacherServiceImpl.updateTeacherById(validId, mockTeacherDto);

        verify(teacherRepository).save(teacherLiveMapper.toTeacherEntity(mockTeacherDto));
    }

    @Test
    public void testThatUpdateTeacherThrowsExceptionForInvalidId() {
        long invalidId = -1L;

        TeacherDto mockTeacherDto = getTeacherDto("Didi",
                "Kira",
                "dkira0@nsw.gov.au",
                "672-852-2193");

        when(teacherRepository.findById(invalidId))
                .thenThrow(new NoSuchElementException());

        assertThatThrownBy(() -> teacherServiceImpl.updateTeacherById(invalidId, mockTeacherDto))
                .isInstanceOf(NoSuchElementException.class);

        verify(teacherRepository).findById(invalidId);
    }

    @Test
    public void testThatDeleteTeacherByIdDeletesTeacherForValidId() {
        long validId = 1L;

        teacherServiceImpl.deleteTeacherById(validId);

        verify(teacherRepository).deleteById(validId);
    }

    private TeacherDto getTeacherDto(String firstName, String lastName, String email, String phone) {
        return TeacherDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .subjects(List.of())
                .classes(List.of())
                .build();
    }
}