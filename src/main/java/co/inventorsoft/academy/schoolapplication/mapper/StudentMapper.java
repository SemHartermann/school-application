package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMapper {
    StudentMapper MAPPER = Mappers.getMapper(StudentMapper.class);

    StudentResponseDto toResponseDto(Student student);

    Student toEntity(StudentRequestDto studentRequestDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(StudentRequestDto studentRequestDto, @MappingTarget Student student);
}
