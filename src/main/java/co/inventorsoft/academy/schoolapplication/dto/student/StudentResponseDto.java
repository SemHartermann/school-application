package co.inventorsoft.academy.schoolapplication.dto.student;

import lombok.Getter;


public record StudentResponseDto (
        @Getter
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}
