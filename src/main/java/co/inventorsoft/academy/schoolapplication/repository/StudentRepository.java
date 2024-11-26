package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT student FROM Student student WHERE student.id IN :ids")
    List<Student> findByIds(@Param("ids") List<Long> childrenIds);

    @Query(value = "SELECT p.email FROM parents p JOIN parents_children spl ON p.id = spl.parent_id WHERE spl.children_id = :studentId", nativeQuery = true)
    Optional<String> findParentEmailByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT student " +
            "FROM Student student " +
            "WHERE student.id " +
            "IN (SELECT s.id FROM ClassGroup cg JOIN cg.students s WHERE cg.id = :classGroupId)")
    List<Student> findAllByClassGroupId(@Param("classGroupId") Long classGroupId);

    boolean existsByEmailAndDeletedIsFalse(String email);
}
