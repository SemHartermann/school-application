package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParentRequestDto(
        @NotBlank(message = "Name is mandatory")
        String firstName,
        @NotBlank(message = "Last name is mandatory")
        String lastName,
        @NotBlank(message = "Email is mandatory")
        @Email
        String email,
        @NotNull(message = "Children is mandatory")
        List<Long> childrenId) {
}
