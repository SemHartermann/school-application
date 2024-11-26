package co.inventorsoft.academy.schoolapplication.event;

import co.inventorsoft.academy.schoolapplication.entity.attendance.Attendance;
import org.springframework.context.ApplicationEvent;

public class AttendanceEvent extends ApplicationEvent {
    private final Attendance attendance;

    public AttendanceEvent(Object source, Attendance attendance) {
        super(source);
        this.attendance = attendance;
    }

    public Attendance getAttendance() {
        return attendance;
    }
}
