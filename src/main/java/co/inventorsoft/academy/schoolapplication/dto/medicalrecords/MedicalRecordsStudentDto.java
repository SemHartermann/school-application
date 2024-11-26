package co.inventorsoft.academy.schoolapplication.dto.medicalrecords;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordsStudentDto {

    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
}
