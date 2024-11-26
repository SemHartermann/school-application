package co.inventorsoft.academy.schoolapplication.dto.student;

import com.poiji.annotation.ExcelCellName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StudentRequestDto {

        @NotBlank(message = "First name is required")
        @ExcelCellName("firstName")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @ExcelCellName("lastName")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @ExcelCellName("email")
        private String email;

        @NotBlank(message = "Phone is required")
        @ExcelCellName("phone")
        @Pattern(regexp = "^\\+?[0-9]+$", message = "Phone must contain only digits and an optional plus symbol")
        private String phone;
}
