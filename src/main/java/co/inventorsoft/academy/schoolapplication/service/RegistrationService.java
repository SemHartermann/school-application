package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenMessageDto;
import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

public interface RegistrationService {
    @Transactional
    void resendVerificationEmail(UserEmailDto emailDto, HttpServletRequest request);

    void sendVerificationEmail(HttpServletRequest request, UserEmailDto activatedUserDto);

    @Transactional
    UserResponseDto completeRegistration(RegistrationDto registrationDto, HttpServletRequest request);

    @Transactional
    VerificationTokenMessageDto confirmUserRegistration(String token, Locale locale);
}
