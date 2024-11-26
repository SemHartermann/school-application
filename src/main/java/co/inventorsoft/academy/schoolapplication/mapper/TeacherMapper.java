package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.TeacherDto;
import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TeacherMapper {

    TeacherMapper MAPPER = Mappers.getMapper(TeacherMapper.class);

    TeacherDto toTeacherDto(Teacher teacher);

    @InheritInverseConfiguration
    Teacher toTeacherEntity(TeacherDto teacherDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherSubjectClasses", ignore = true)
    void mergeToTeacherEntity(TeacherDto teacherDto, @MappingTarget Teacher teacher);
}
