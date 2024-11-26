package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
}
