package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsStudentDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.HealthGroup;
import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.MedicalRecords;
import co.inventorsoft.academy.schoolapplication.mapper.MedicalRecordsMapper;
import co.inventorsoft.academy.schoolapplication.repository.MedicalRecordsRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.MedicalRecordsServiceImpl;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordsServiceImplTest {

    final MedicalRecordsMapper medicalRecordsLiveMapper = MedicalRecordsMapper.MAPPER;
    @Mock
    StudentRepository studentRepository;
    @Mock
    MedicalRecordsRepository medicalRecordsRepository;
    @InjectMocks
    MedicalRecordsServiceImpl medicalRecordsServiceImpl;

    @Test
    public void testThatGetAllRecordsReturnsAllRecords() {

        Pageable pageable = PageRequest.of(0, 10);

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsStudentDto student1 = getStudent(
                "Nichol",
                "Fenelow",
                "nfenelow4@youtube.com",
                "987654321");

        MedicalRecordsStudentDto student2 = getStudent(
                "Shayne",
                "Timony",
                "sotimony0@bravesites.com",
                "123789456");

        Page<MedicalRecordsDto> mockRecordsDtoPage = new PageImpl<>(
                List.of(getMedicalRecordsDto(
                                "Peanuts, shellfish",
                                HealthGroup.GROUP_A,
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                student0),

                        getMedicalRecordsDto(
                                "Pollen, shellfish",
                                HealthGroup.GROUP_B,
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                student1),

                        getMedicalRecordsDto(
                                "Pollen, peanuts",
                                HealthGroup.GROUP_C,
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                student2))
        );

        List<MedicalRecords> mockRecordsEntityList = mockRecordsDtoPage.stream()
                .map(medicalRecordsLiveMapper::toMedicalRecordsEntity)
                .toList();

        when(medicalRecordsRepository.findAll(pageable)).thenReturn(new PageImpl<>(mockRecordsEntityList));

        Page<MedicalRecordsDto> actualResult = medicalRecordsServiceImpl.getAllRecords(pageable);

        assertThat(mockRecordsDtoPage).isEqualTo(actualResult);

        verify(medicalRecordsRepository).findAll(pageable);

    }

    @Test
    public void testThatGetRecordsByIdReturnsRecordsForValidId() {

        long validId = 1L;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);

        when(medicalRecordsRepository.findById(validId)).thenReturn(
                Optional.of(medicalRecordsLiveMapper.toMedicalRecordsEntity(mockRecordsDto))
        );


        MedicalRecordsDto actualResult = medicalRecordsServiceImpl.getRecordsById(validId);

        assertThat(mockRecordsDto).isEqualTo(actualResult);

        verify(medicalRecordsRepository).findById(validId);

    }

    @Test
    public void testThatGetRecordsByIdThrowsNoSuchElementExceptionForInvalidId() {

        long invalidId = -1L;

        assertThatThrownBy(() -> medicalRecordsServiceImpl.getRecordsById(invalidId))
                .isInstanceOf(NoSuchElementException.class);

        verify(medicalRecordsRepository).findById(invalidId);

    }

    @Test
    public void testThatAddNewRecordsByStudentIdAddsNewRecords() {

        long studentId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);

        MedicalRecords mockRecordsEntity = medicalRecordsLiveMapper.toMedicalRecordsEntity(mockRecordsDto);

        Student studentEntity = mockRecordsEntity.getStudent();

        when(studentRepository.findById(studentId)).thenReturn(
                Optional.of(studentEntity)
        );

        medicalRecordsServiceImpl.addNewRecordsByStudentId(studentId, mockRecordsDto);

        verify(medicalRecordsRepository, times(1)).save(mockRecordsEntity);
    }

    @Test
    public void testThatUpdateRecordsByRecordsIdUpdatesRecords() {

        long recordsId = 1;

        MedicalRecordsStudentDto student0 = getStudent(
                "Annalise",
                "Rolles",
                "arolles0@tmall.com",
                "123456789");

        MedicalRecordsDto mockRecordsDto = getMedicalRecordsDto(
                "Peanuts, shellfish",
                HealthGroup.GROUP_A,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                student0);

        MedicalRecords mockRecordsEntity = medicalRecordsLiveMapper.toMedicalRecordsEntity(mockRecordsDto);

        when(medicalRecordsRepository.findById(recordsId)).thenReturn(
                Optional.of(mockRecordsEntity)
        );

        medicalRecordsServiceImpl.updateRecordsByRecordsId(1L, mockRecordsDto);

        verify(medicalRecordsRepository, times(1)).save(mockRecordsEntity);
    }

    @Test
    public void testThatDeleteRecordsByIdDeletesRecordsById() {

        long recordsId = 1;

        medicalRecordsServiceImpl.deleteRecordsByRecordsId(recordsId);

        verify(medicalRecordsRepository).deleteById(recordsId);
    }

    private MedicalRecordsDto getMedicalRecordsDto(String allergies,
                                                   HealthGroup healthGroup,
                                                   String info,
                                                   MedicalRecordsStudentDto studentDto) {

        return MedicalRecordsDto.builder()
                .allergies(allergies)
                .healthGroup(healthGroup)
                .info(info)
                .student(studentDto)
                .build();
    }

    private MedicalRecordsStudentDto getStudent(String firstName, String lastName, String email, String phone) {
        return MedicalRecordsStudentDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}
