package co.inventorsoft.academy.schoolapplication.service.impl.registration;

import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.repository.ParentRepository;
import co.inventorsoft.academy.schoolapplication.service.RegistrationStrategy;
import co.inventorsoft.academy.schoolapplication.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ParentRegistrationStrategy implements RegistrationStrategy {
    ParentRepository parentRepository;
    UserService userService;

    public ParentRegistrationStrategy(ParentRepository parentRepository,
                                      UserService userService) {
        this.parentRepository = parentRepository;
        this.userService = userService;
    }

    @Override
    public Role getSupportedRole() {
        return Role.PARENT;
    }

    @Override
    @Transactional
    public UserResponseDto registerUser(RegistrationDto registrationDto) {
        if (!parentRepository.existsByEmailAndDeletedIsFalse(registrationDto.getEmail())) {
            throw new IllegalStateException("No data about user with email: " + registrationDto.getEmail());
        }

        UserRequestDto newUserDto = new UserRequestDto(
                null,
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                Role.PARENT
        );

        return userService.saveRegisteredUser(newUserDto);
    }
}
