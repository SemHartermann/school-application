package co.inventorsoft.academy.schoolapplication.dto;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseClassGroupDto {
    Long id;
    String name;
    List<StudentResponseDto> studentDtoList;
    List<SubjectDto> subjectDtoList;
}
