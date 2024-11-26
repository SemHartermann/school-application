package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.MedicalRecords;
import co.inventorsoft.academy.schoolapplication.mapper.MedicalRecordsMapper;
import co.inventorsoft.academy.schoolapplication.repository.MedicalRecordsRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.MedicalRecordsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalRecordsServiceImpl implements MedicalRecordsService {

    MedicalRecordsRepository medicalRecordsRepository;
    MedicalRecordsMapper medicalRecordsMapper = MedicalRecordsMapper.MAPPER;
    StudentRepository studentRepository;

    @Override
    public Page<MedicalRecordsDto> getAllRecords(Pageable pageable) {
        Page<MedicalRecords> records = medicalRecordsRepository.findAll(pageable);
        return records.map(medicalRecordsMapper::toMedicalRecordsDto);
    }

    @Override
    public MedicalRecordsDto getRecordsById(Long id) {
        return medicalRecordsMapper.toMedicalRecordsDto(medicalRecordsRepository
                .findById(id)
                .orElseThrow());
    }


    @Override
    public void addNewRecordsByStudentId(Long id, MedicalRecordsDto medicalRecordsDto) {
        Student student = studentRepository.findById(id)
                .orElseThrow();

        MedicalRecords medicalRecords = medicalRecordsMapper.toMedicalRecordsEntity(medicalRecordsDto);

        medicalRecords.setStudent(student);

        medicalRecordsRepository.save(medicalRecords);
    }

    @Override
    public void updateRecordsByRecordsId(Long id, MedicalRecordsDto medicalRecordsDto) {
        MedicalRecords medicalRecords = medicalRecordsRepository.findById(id)
                .orElseThrow();

        medicalRecordsMapper.mergeToMedicalRecordsEntity(medicalRecordsDto, medicalRecords);

        medicalRecordsRepository.save(medicalRecords);
    }

    @Override
    public void deleteRecordsByRecordsId(Long id) {
        medicalRecordsRepository.deleteById(id);
    }
}
