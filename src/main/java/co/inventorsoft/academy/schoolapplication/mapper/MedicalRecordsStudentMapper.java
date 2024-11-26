package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsStudentDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MedicalRecordsStudentMapper {

    MedicalRecordsStudentMapper MAPPER = Mappers.getMapper(MedicalRecordsStudentMapper.class);

    MedicalRecordsStudentDto toMedicalRecordsStudentDto(Student student);

    @InheritInverseConfiguration
    Student toStudentEntity(MedicalRecordsStudentDto medicalRecordsStudentDto);
}
