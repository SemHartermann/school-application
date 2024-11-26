package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "deleted=false")
public class ClassGroup extends AuditableEntity {

    @Column(unique = true, nullable = false)
    String name;

    @OneToMany
    List<Student> students;

    @OneToMany
    List<Subject> subject;

    @OneToMany
    List<Teacher> teacherList;

    boolean deleted;
}
