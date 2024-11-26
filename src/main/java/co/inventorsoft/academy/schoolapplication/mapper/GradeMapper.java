package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.GradeDto;
import co.inventorsoft.academy.schoolapplication.entity.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GradeMapper {

    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source = "student.id", target = "studentId")
    GradeDto toDto(Grade grade);
}
