package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.entity.Subject;
import co.inventorsoft.academy.schoolapplication.mapper.SubjectMapper;
import co.inventorsoft.academy.schoolapplication.repository.SubjectRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.SubjectServiceImpl;
import co.inventorsoft.academy.schoolapplication.util.excepion.excel.EmptyExcelFileException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyIterable;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class SubjectServiceImplTest {
    final SubjectMapper subjectLiveMapper = SubjectMapper.MAPPER;
    @Mock
    SubjectRepository subjectRepository;
    @InjectMocks
    SubjectServiceImpl subjectServiceImpl;
    @Captor
    private ArgumentCaptor<List<Subject>> captor;

    @Test
    public void testThatGetAllSubjectsReturnsAllSubjects() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<SubjectDto> mockSubjectsDtoPage = new PageImpl<>(
                List.of(
                        getSubjectDto("Physics"),
                        getSubjectDto("Chemistry"),
                        getSubjectDto("Mathematics")
                )
        );

        List<Subject> mockSubjectEntityList = mockSubjectsDtoPage.stream()
                .map(subjectLiveMapper::toSubjectEntity)
                .toList();

        when(subjectRepository.findAll(pageable)).thenReturn(
                new PageImpl<>(mockSubjectEntityList)
        );

        Page<SubjectDto> actualResult = subjectServiceImpl.getAllSubjects(pageable);

        assertThat(mockSubjectsDtoPage).isEqualTo(actualResult);

        verify(subjectRepository, times(1)).findAll(pageable);

    }

    @Test
    public void testThatGetSubjectByIdReturnsSubjectForValidId() {
        long validId = 1L;

        SubjectDto mockSubject = getSubjectDto("Physics");

        when(subjectRepository.findById(validId)).thenReturn(
                Optional.of(subjectLiveMapper.toSubjectEntity(mockSubject))
        );

        SubjectDto actualResult = subjectServiceImpl.getSubjectById(validId);

        assertThat(mockSubject).isEqualTo(actualResult);

        verify(subjectRepository).findById(validId);
    }

    @Test
    public void testThatGetSubjectByIdThrowsNoSuchElementExceptionForInvalidId() {
        long invalidId = -1L;

        assertThatThrownBy(() -> subjectServiceImpl.getSubjectById(invalidId))
                .isInstanceOf(NoSuchElementException.class);

        verify(subjectRepository).findById(invalidId);
    }


    @Test
    public void testThatAddNewSubjectAddsSubject() {
        SubjectDto mockSubject = getSubjectDto("Physics");

        subjectServiceImpl.addNewSubject(mockSubject);

        verify(subjectRepository).save(subjectLiveMapper.toSubjectEntity(mockSubject));
    }

    @Test
    public void testThatUpdateSubjectByIdUpdatesSubject() {
        long validId = 1L;

        SubjectDto mockSubject = getSubjectDto("Physics Updated");

        when(subjectRepository.findById(validId)).thenReturn(
                Optional.of(subjectLiveMapper.toSubjectEntity(mockSubject))
        );

        subjectServiceImpl.updateSubjectById(validId, mockSubject);

        verify(subjectRepository).save(subjectLiveMapper.toSubjectEntity(mockSubject));
    }


    @Test
    public void testThatUpdateSubjectThrowsExceptionForInvalidId() {
        long invalidId = -1L;

        SubjectDto mockSubject = getSubjectDto("Physics");

        when(subjectRepository.findById(invalidId))
                .thenThrow(new NoSuchElementException());

        assertThatThrownBy(() -> subjectServiceImpl.updateSubjectById(invalidId, mockSubject))
                .isInstanceOf(NoSuchElementException.class);

        verify(subjectRepository).findById(invalidId);
    }

    @Test
    public void testThatDeleteSubjectByIdDeletesSubjectForValidId() {
        long validId = 1L;

        subjectServiceImpl.deleteSubjectById(validId);

        verify(subjectRepository).deleteById(validId);
    }

    @Test
    void testUploadFromFileWithCSVFile() {

        String csvContent = "name\nMath\nHistory";
        MockMultipartFile file = new MockMultipartFile("file"
                , "test.csv"
                , "text/csv"
                , csvContent.getBytes());

        Subject sub1 = new Subject();
        sub1.setName("Math");
        Subject sub2 = new Subject();
        sub2.setName("History");
        List<Subject> subjectArrayList = List.of(sub1, sub2);

        when(subjectRepository.saveAll(anyIterable())).thenReturn(subjectArrayList);

        subjectServiceImpl.uploadFromFile(file);

        verify(subjectRepository, times(1)).saveAll(captor.capture());
        List<Subject> savedEntities = captor.getValue();
        assertThat(savedEntities).contains(sub1, sub2);
    }

    @Test
    void testUploadFromFileWithEmptyExelFile() {

        MockMultipartFile excelFile = new MockMultipartFile("file"
                , "test.xlsx"
                , "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                , new byte[0]);

        assertThrows(EmptyExcelFileException.class, () -> {
            subjectServiceImpl.uploadFromFile(excelFile);
        });

        verify(subjectRepository, never()).saveAll(anyList());
    }

    private SubjectDto getSubjectDto(String name) {
        return SubjectDto.builder()
                .name(name)
                .build();
    }
}