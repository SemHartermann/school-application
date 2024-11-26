package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalRecordsService {
    Page<MedicalRecordsDto> getAllRecords(Pageable pageable);

    MedicalRecordsDto getRecordsById(Long id);

    void addNewRecordsByStudentId(Long id, MedicalRecordsDto medicalRecordsDto);

    void updateRecordsByRecordsId(Long id, MedicalRecordsDto medicalRecordsDto);

    void deleteRecordsByRecordsId(Long id);
}
