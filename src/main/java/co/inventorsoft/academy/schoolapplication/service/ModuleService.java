package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

public interface ModuleService {
    Page<ModuleDto> getAllModulesBySubjectId(Pageable pageable, Long id);

    Optional<ModuleDto> getModule(Long id);

    Optional<ModuleDto> createModule(ModuleDto dto);

    Optional<ModuleDto> updateModuleById(Long id, ModuleDto dto);

    void deleteModule(Long id);

    Set<ModuleDto> findModulesForNextWeek(ZonedDateTime startOfWeek, ZonedDateTime endOfWeek);
}
