package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@SQLDelete(sql = "UPDATE teachers SET deleted = true, email = CONCAT(email, '_', EXTRACT(EPOCH FROM NOW())::bigint), phone = CONCAT(phone, '_', EXTRACT(EPOCH FROM NOW())::bigint) WHERE id=?")
@Where(clause = "deleted=false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Teacher extends AuditableEntity {

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String phone;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    List<TeacherSubjectClass> teacherSubjectClasses;

    @Transient
    List<Subject> subjects;

    @Transient
    List<ClassGroup> classes;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean deleted;
}
