package co.inventorsoft.academy.schoolapplication.dto;

import com.opencsv.bean.CsvBindByName;
import com.poiji.annotation.ExcelCellName;
import jakarta.validation.constraints.NotBlank;
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
public class SubjectDto {

    Long id;
    @ExcelCellName("name")
    @CsvBindByName(column = "title", required = true)
    @NotBlank(message = "The field cannot be blank")
    String name;
}
