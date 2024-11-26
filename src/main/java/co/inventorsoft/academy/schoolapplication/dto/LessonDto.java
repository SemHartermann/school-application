package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonDto {
    Long id;

    @NotBlank
    String name;

    @NotNull
    ZonedDateTime date;

    @Min(0) @Max(8)
    @NotNull
    Integer period;

    String homework;

    @NotNull
    Long moduleId;
}