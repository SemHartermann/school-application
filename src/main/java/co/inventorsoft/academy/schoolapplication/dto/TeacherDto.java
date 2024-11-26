package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherDto {

    Long id;

    @NotBlank(message = "The field cannot be blank")
    String firstName;

    @NotBlank(message = "The field cannot be blank")
    String lastName;

    String email;
    String phone;

    List<SubjectDto> subjects;
    List<TeacherResponseClassGroupDto> classes;
}
