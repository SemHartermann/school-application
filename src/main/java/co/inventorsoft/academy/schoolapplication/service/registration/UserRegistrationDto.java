package co.inventorsoft.academy.schoolapplication.service.registration;

import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private Role role;
}
