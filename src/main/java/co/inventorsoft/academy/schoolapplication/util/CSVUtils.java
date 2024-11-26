package co.inventorsoft.academy.schoolapplication.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public class CSVUtils {

    //Every class that is to be converted must have @CsvBindByName(column = "column_name", required = true) annotation over fields
    public static <T> List<T> convertToModel(MultipartFile file, Class<T> responseType) {
        List<T> models;
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<?> csvToBean = new CsvToBeanBuilder<>(reader)
                .withType(responseType)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build();
            models = (List<T>) csvToBean.parse();
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getCause().getMessage());
        }
        return models;
    }
}
