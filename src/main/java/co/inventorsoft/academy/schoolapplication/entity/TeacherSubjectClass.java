package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "teachers_subjects_classes")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherSubjectClass {

    @EmbeddedId
    TeacherSubjectClassKey id;

    @ManyToOne
    @MapsId("teacherId")
    @JoinColumn(nullable = false, name = "teacher_id")
    Teacher teacher;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(nullable = false, name = "subject_id")
    Subject subject;

    @ManyToOne
    @MapsId("classGroupId")
    @JoinColumn(nullable = false, name = "classGroup_id")
    ClassGroup classGroup;

}
