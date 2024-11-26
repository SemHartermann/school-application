package co.inventorsoft.academy.schoolapplication.service.registration;

import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        validateEmail(registrationDto.getEmail());
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email is already in use"); // TODO: Use a custom exception
        }

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

        User newUser = User.builder()
                .email(registrationDto.getEmail())
                .password(encodedPassword)
                .role(registrationDto.getRole())
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        return userRepository.save(newUser);
    }

    private void validateEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
