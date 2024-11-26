package co.inventorsoft.academy.schoolapplication.service.impl.registration;

import co.inventorsoft.academy.schoolapplication.config.AuthMessages;
import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenMessageDto;
import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.event.OnRegistrationCompleteEvent;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.UserService;
import co.inventorsoft.academy.schoolapplication.service.VerificationTokenService;
import co.inventorsoft.academy.schoolapplication.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationServiceImpl implements RegistrationService {
    UserService userService;

    ApplicationEventPublisher eventPublisher;

    UserRepository userRepository;

    MessageSource messages;

    VerificationTokenService verificationTokenService;

    AuthMessages authMessages;

    RegistrationStrategyFactory strategyFactory;

    public RegistrationServiceImpl(UserService userService,
                                   ApplicationEventPublisher eventPublisher,
                                   UserRepository userRepository,
                                   MessageSource messages,
                                   VerificationTokenService verificationTokenService,
                                   AuthMessages authMessages,
                                   RegistrationStrategyFactory strategyFactory) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
        this.messages = messages;
        this.verificationTokenService = verificationTokenService;
        this.authMessages = authMessages;
        this.strategyFactory = strategyFactory;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(UserEmailDto emailDto, HttpServletRequest request) {
        userService.getUserByEmail(emailDto.getEmail());
        sendVerificationEmail(request, emailDto);
    }

    @Override
    public void sendVerificationEmail(HttpServletRequest request, UserEmailDto activatedUserDto) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(activatedUserDto,
                request.getLocale(),
                request.getContextPath()));
    }

    @Override
    @Transactional
    public VerificationTokenMessageDto confirmUserRegistration(String token, Locale locale) {
        VerificationTokenDto verificationTokenDto = verificationTokenService.getVerificationToken(token);
        if (Objects.isNull(verificationTokenDto)) {
            return new VerificationTokenMessageDto(false, authMessages.getInvalidToken(), HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isEmpty()) {
            String message = messages.getMessage("auth.message.tokenNotFound", null, locale);

            return new VerificationTokenMessageDto(false, message, HttpStatus.BAD_REQUEST);
        }

        if (verificationTokenDto.isExpired()) {
            return new VerificationTokenMessageDto(Boolean.FALSE, authMessages.getExpired(), HttpStatus.BAD_REQUEST);
        }

        user.get().setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user.get());

        return new VerificationTokenMessageDto(true, authMessages.getConfirmed(), HttpStatus.OK);
    }

    @Override
    @Transactional
    public UserResponseDto completeRegistration(RegistrationDto registrationDto, HttpServletRequest request) {
        UserResponseDto responseDto =
                strategyFactory.get(registrationDto.getUserType()).registerUser(registrationDto);

        sendVerificationEmail(request, new UserEmailDto(registrationDto.getEmail()));

        return responseDto;
    }
}
