package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.dto.VerificationTokenDto;
import org.springframework.transaction.annotation.Transactional;

public interface VerificationTokenService {
    @Transactional
    void createVerificationToken(UserEmailDto email, String token);

    VerificationTokenDto getVerificationToken(String VerificationToken);
}
