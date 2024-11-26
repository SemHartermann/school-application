package co.inventorsoft.academy.schoolapplication.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    TEST_READ("test:read"),
    TEST_UPDATE("test:update");
    // TODO: define more permissions for STUDENT, TEACHER, PARENT, MEDICAL_STAFF, etc.
    private final String permission;
}
