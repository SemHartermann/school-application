package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.teacherSubjectClasses tsc LEFT JOIN FETCH tsc.classGroup LEFT JOIN FETCH tsc.subject WHERE t.deleted = false ORDER BY t.id")
    Page<Teacher> findAll(Pageable pageable);

    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.teacherSubjectClasses tsc LEFT JOIN FETCH tsc.classGroup LEFT JOIN FETCH tsc.subject WHERE t.deleted = false AND t.id = :id ORDER BY t.id")
    Optional<Teacher> findById(@Param("id") Long id);

    boolean existsByEmailAndDeletedIsFalse(String email);
}
