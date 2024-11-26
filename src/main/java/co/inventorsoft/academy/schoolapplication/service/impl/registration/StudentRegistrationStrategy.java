package co.inventorsoft.academy.schoolapplication.service.impl.registration;

import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.RegistrationStrategy;
import co.inventorsoft.academy.schoolapplication.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StudentRegistrationStrategy implements RegistrationStrategy {

    StudentRepository studentRepository;
    UserService userService;

    @Override
    public UserResponseDto registerUser(RegistrationDto registrationDto) {
        if (!studentRepository.existsByEmailAndDeletedIsFalse(registrationDto.getEmail())) {
            throw new IllegalStateException("No data about user with email: " + registrationDto.getEmail());
        }

        UserRequestDto newUserDto = new UserRequestDto(
                null,
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                Role.STUDENT
        );

        return userService.saveRegisteredUser(newUserDto);
    }

    @Override
    public Role getSupportedRole() {
        return Role.STUDENT;
    }
}
