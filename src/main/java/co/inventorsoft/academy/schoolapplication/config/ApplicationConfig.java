package co.inventorsoft.academy.schoolapplication.config;

import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProviderImpl")
public class ApplicationConfig {
    @Component("dateTimeProviderImpl")
    public class DateTimeProviderImpl implements DateTimeProvider {
        @Override
        public @NonNull Optional<TemporalAccessor> getNow() {
            return Optional.of(ZonedDateTime.now(ZoneOffset.UTC));
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
