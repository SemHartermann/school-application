package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenMessageDto;
import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

public interface RegistrationStrategy {
    @Transactional
    UserResponseDto registerUser(RegistrationDto registrationDto);

    Role getSupportedRole();
}
