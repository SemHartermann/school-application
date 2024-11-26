package co.inventorsoft.academy.schoolapplication.dto.medicalrecords;

import co.inventorsoft.academy.schoolapplication.entity.medicalrecords.HealthGroup;
import jakarta.validation.constraints.NotNull;
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
public class MedicalRecordsDto {

    Long id;

    @NotNull
    HealthGroup healthGroup;

    String allergies;
    String info;
    MedicalRecordsStudentDto student;
}
