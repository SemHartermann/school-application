package co.inventorsoft.academy.schoolapplication.listener;

import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.event.OnRegistrationCompleteEvent;
import co.inventorsoft.academy.schoolapplication.service.VerificationTokenService;
import co.inventorsoft.academy.schoolapplication.service.MailService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    MailService mailService;
    VerificationTokenService verificationTokenService;
    Environment environment;

    @Autowired
    public RegistrationListener(MailService mailService, VerificationTokenService verificationTokenService, Environment environment) {
        this.mailService = mailService;
        this.verificationTokenService = verificationTokenService;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserEmailDto userDto = event.getUserDto();
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(userDto, token);

        String recipientAddress = userDto.getEmail();
        String subject = environment.getProperty("email.registration.subject", "Registration Confirmation");
        String confirmationUrl = "http://localhost:8080" + event.getAppUrl() + "/api/public/registration/confirmation?token=" + token;
        String emailBody = environment.getProperty("email.registration.body", "Your registration was successful!") + "\r\n" + confirmationUrl;

        mailService.sendEmail(recipientAddress, subject, emailBody);
    }
}