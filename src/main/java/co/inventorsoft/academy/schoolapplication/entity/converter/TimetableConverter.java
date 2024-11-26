package co.inventorsoft.academy.schoolapplication.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;

@Converter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TimetableConverter implements AttributeConverter<Map<DayOfWeek, Set<Integer>>, String> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<DayOfWeek, Set<Integer>> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting map to JSON", e);

            throw new RuntimeException("An error occurred while processing your request.", e);
        }
    }

    @Override
    public Map<DayOfWeek, Set<Integer>> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<Map<DayOfWeek, Set<Integer>>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to map", e);

            throw new RuntimeException("An error occurred while processing your request.", e);
        }
    }

}
