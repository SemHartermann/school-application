package co.inventorsoft.academy.schoolapplication.repository;

import co.inventorsoft.academy.schoolapplication.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Set;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Page<Module> findBySubjectId(Pageable pageable, Long id);

    @Query("SELECT m FROM Module m WHERE m.startDate <= :endOfWeek AND m.endDate >= :startOfWeek")
    Set<Module> findModulesForNextWeek(
            @Param("startOfWeek") ZonedDateTime startOfWeek,
            @Param("endOfWeek") ZonedDateTime endOfWeek);
}
