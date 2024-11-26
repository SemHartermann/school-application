package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class CommentRequestDto {

    @NotBlank(message = "comment text cant be empty!")
    String text;

    @NotNull(message = "user is mandatory for role!")
    Long userId;
}
