package co.inventorsoft.academy.schoolapplication.util.model;

import com.poiji.annotation.ExcelCellName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Person {
    @ExcelCellName("firstname")
    String firstname;

    @ExcelCellName("lastname")
    String lastname;

    @ExcelCellName("phone")
    String phone;

    @ExcelCellName("email")
    String email;

    @ExcelCellName("address")
    String address;
}
