package co.inventorsoft.academy.schoolapplication.entity;

import co.inventorsoft.academy.schoolapplication.entity.enums.GradeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "grades")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Grade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Range(min = 1, max = 12)
    @Column(name = "grade_value", nullable = false)
    Integer gradeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false)
    GradeType gradeType;

    @Column(name = "date")
    ZonedDateTime gradeDate;
}
