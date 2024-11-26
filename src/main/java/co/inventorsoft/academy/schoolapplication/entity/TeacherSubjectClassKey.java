package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class TeacherSubjectClassKey implements Serializable {

    @Column(nullable = false, name = "teacher_id")
    Long teacherId;

    @Column(nullable = false, name = "subject_id")
    Long subjectId;

    @Column(nullable = false, name = "classGroup_id")
    Long classGroupId;
}
