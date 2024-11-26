package co.inventorsoft.academy.schoolapplication.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherResponseClassGroupDto {
    Long id;
    String name;
}