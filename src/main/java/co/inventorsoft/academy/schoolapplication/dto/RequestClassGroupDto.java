package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestClassGroupDto {
    Long id;
    @Pattern(regexp = "\\d+-[А-ЯA-Z]", message = "The class name must be in the format of 'number-letter' (e.g., '10-A')")
    String name;
}
