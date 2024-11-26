package co.inventorsoft.academy.schoolapplication.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationTokenMessageDto {
    boolean success;
    String message;
    HttpStatus status;
}
