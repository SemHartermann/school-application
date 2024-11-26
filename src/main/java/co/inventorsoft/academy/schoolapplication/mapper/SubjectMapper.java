package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.entity.Subject;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SubjectMapper {

    SubjectMapper MAPPER = Mappers.getMapper(SubjectMapper.class);

    SubjectDto toSubjectDto(Subject subject);

    @InheritInverseConfiguration
    Subject toSubjectEntity(SubjectDto subjectDto);
}
