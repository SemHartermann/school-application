package co.inventorsoft.academy.schoolapplication.dto.user;

import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {
    Long id;

    @Email(message = "Email should be valid")
    String email;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}",
            message = "Password must contain at least 8 characters, one uppercase, one lowercase, and one number")
    String password;

    @NotNull(message = "Role cannot be null")
    Role role;
}
