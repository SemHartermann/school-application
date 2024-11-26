package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleDto {

    Long id;

    @NotNull
    Long subjectId;

    @NotNull
    Long classRoomId;

    @NotNull
    Long teacherId;

    @NotBlank
    String name;

    @NotNull
    ZonedDateTime startDate;

    @NotNull
    ZonedDateTime endDate;

    @NotEmpty
    Map<DayOfWeek, Set<Integer>> schedule;

}
