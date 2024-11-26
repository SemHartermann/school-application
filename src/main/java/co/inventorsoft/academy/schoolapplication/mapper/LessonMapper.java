package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.LessonDto;
import co.inventorsoft.academy.schoolapplication.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonDto toDto(Lesson lesson);
    Lesson toLesson(LessonDto dto);

    void updateLessonFromDto(LessonDto dto, @MappingTarget Lesson lesson);

    List<Lesson> listOfLessonDtosToListOfLessons(List<LessonDto> lessonDtos);
}
