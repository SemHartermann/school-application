package co.inventorsoft.academy.schoolapplication.entity;


import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "modules")
@Getter
@Setter
@SQLDelete(sql = "UPDATE modules SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(callSuper = true, of = {"subjectId", "classRoomId", "teacherId", "name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Module extends AuditableEntity {

//    TODO change these referenced fields to entities
    @NotNull
    @Column(nullable = false)
    Long subjectId;

    @NotNull
    @Column(nullable = false)
    Long classRoomId;

    @NotNull
    @Column(nullable = false)
    Long teacherId;

    @NotBlank
    @Column(nullable = false)
    String name;

    @NotNull
    @Column(nullable = false)
    ZonedDateTime startDate;

    @NotNull
    @Column(nullable = false)
    ZonedDateTime endDate;

    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean deleted = false;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    Map<DayOfWeek, Set<Integer>> schedule;

}
