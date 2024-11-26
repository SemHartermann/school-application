package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.config.AuthMessages;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenMessageDto;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.registration.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private AuthMessages authMessages;

    private VerificationTokenDto expiredTokenDto;

    private VerificationTokenDto validTokenDto;
    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        expiredTokenDto = new VerificationTokenDto("expired-token", LocalDateTime.now().minusDays(1));
        validTokenDto = new VerificationTokenDto("valid-token", LocalDateTime.now().plusDays(1));
        token = "valid-token";
        testUser = new User();
    }

    @Test
    void whenConfirmUserRegistrationWithValidToken_thenSuccess() {
        when(verificationTokenService.getVerificationToken(token)).thenReturn(validTokenDto);
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(testUser));
        when(authMessages.getConfirmed()).thenReturn("Your account has been confirmed");

        VerificationTokenMessageDto result = registrationService.confirmUserRegistration(token, Locale.ENGLISH);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Your account has been confirmed");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        verify(userRepository).save(testUser);
    }


    @Test
    void whenConfirmUserRegistrationWithInvalidToken_thenFail() {
        when(verificationTokenService.getVerificationToken(token)).thenReturn(null);
        when(authMessages.getInvalidToken()).thenReturn("Invalid token");

        VerificationTokenMessageDto result = registrationService.confirmUserRegistration(token, Locale.ENGLISH);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("Invalid token");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(userRepository, never()).save(any(User.class));
    }
}
