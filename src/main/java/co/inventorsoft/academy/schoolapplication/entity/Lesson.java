package co.inventorsoft.academy.schoolapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "lessons")
@SQLDelete(sql = "UPDATE lessons SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true, of = {"name", "date", "period", "moduleId", "homework"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson extends AuditableEntity {

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    ZonedDateTime date;

    @Column(nullable = false)
    Integer period;

    String homework;

    @Column(name = "module_id", nullable = false)
    Long moduleId;

    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean deleted = false;
}
