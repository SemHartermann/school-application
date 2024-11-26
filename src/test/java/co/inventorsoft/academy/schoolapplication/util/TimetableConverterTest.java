package co.inventorsoft.academy.schoolapplication.util;

import co.inventorsoft.academy.schoolapplication.entity.converter.TimetableConverter;
import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimetableConverterTest {

    @Test
    public void testConverter() {
        TimetableConverter converter = new TimetableConverter();

        Map<DayOfWeek, Set<Integer>> timetable = new HashMap<>();
        timetable.put(DayOfWeek.MONDAY, new HashSet<>(Arrays.asList(1, 2, 3)));
        timetable.put(DayOfWeek.TUESDAY, new HashSet<>(Arrays.asList(4, 5, 6)));

        String json = converter.convertToDatabaseColumn(timetable);

        Map<DayOfWeek, Set<Integer>> convertedTimetable = converter.convertToEntityAttribute(json);

        assertEquals(timetable, convertedTimetable);
    }
}