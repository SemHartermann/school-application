package co.inventorsoft.academy.schoolapplication.dto;

import co.inventorsoft.academy.schoolapplication.entity.Student;

import java.util.List;

public record ParentResponseDto (
        Long id,
        String firstName,
        String lastName,
        String email,
        List<Long> childrenId) {
}
