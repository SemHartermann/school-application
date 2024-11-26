package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.medicalrecords.MedicalRecordsDto;
import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.MedicalRecords;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MedicalRecordsMapper {

    MedicalRecordsMapper MAPPER = Mappers.getMapper(MedicalRecordsMapper.class);

    MedicalRecordsDto toMedicalRecordsDto(MedicalRecords medicalRecords);

    @InheritInverseConfiguration
    MedicalRecords toMedicalRecordsEntity(MedicalRecordsDto medicalRecordsDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    void mergeToMedicalRecordsEntity(MedicalRecordsDto medicalRecordsDto, @MappingTarget MedicalRecords medicalRecords);
}
