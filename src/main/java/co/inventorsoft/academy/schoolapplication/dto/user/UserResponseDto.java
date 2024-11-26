package co.inventorsoft.academy.schoolapplication.dto.user;

import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {
    Long id;

    String email;

    Role role;

    AccountStatus accountStatus;

    @JsonIgnore
    String password;

    public UserResponseDto(Long id, String email, Role role, AccountStatus accountStatus) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
    }
}
