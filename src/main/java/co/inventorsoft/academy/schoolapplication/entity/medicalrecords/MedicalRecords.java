package co.inventorsoft.academy.schoolapplication.entity.medicalrecords;

import co.inventorsoft.academy.schoolapplication.entity.AuditableEntity;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@SQLDelete(sql = "UPDATE medical_records SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecords extends AuditableEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    HealthGroup healthGroup;

    String allergies;
    String info;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;
}
