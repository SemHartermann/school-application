package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@SQLDelete(sql = "UPDATE subjects SET deleted = true, name = CONCAT(name, '_', EXTRACT(EPOCH FROM NOW())::bigint) WHERE id=?")
@Where(clause = "deleted=false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subject extends AuditableEntity {

    @Column(nullable = false, unique = true)
    String name;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    List<TeacherSubjectClass> teacherSubjectClass;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean deleted;
}
