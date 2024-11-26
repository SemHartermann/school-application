package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.mapper.ModuleMapper;
import co.inventorsoft.academy.schoolapplication.repository.ModuleRepository;
import co.inventorsoft.academy.schoolapplication.service.ModuleService;
import co.inventorsoft.academy.schoolapplication.entity.Module;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    ModuleRepository moduleRepository;
    ModuleMapper moduleMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleDto> getAllModulesBySubjectId(Pageable pageable, Long id) {
        Page<Module> modules = moduleRepository.findBySubjectId(pageable, id);

        return modules.map(moduleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ModuleDto> getModule(Long id) {

        return moduleRepository.findById(id).map(moduleMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<ModuleDto> createModule(ModuleDto dto) {
        Module module = moduleMapper.toEntity(dto);
        Module savedLesson = moduleRepository.save(module);

        return Optional.ofNullable(moduleMapper.toDto(savedLesson));
    }

    @Override
    @Transactional
    public Optional<ModuleDto> updateModuleById(Long id, ModuleDto dto) {
        return moduleRepository.findById(dto.getId())
                .map(module -> {
                    moduleMapper.updateModuleFromDto(dto, module);
                    return module;
                })
                .map(moduleRepository::save)
                .map(moduleMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        moduleRepository.deleteById(id);
    }

    @Override
    public Set<ModuleDto> findModulesForNextWeek(ZonedDateTime startOfWeek, ZonedDateTime endOfWeek) {
        Set<Module> modulesForNextWeek = moduleRepository.findModulesForNextWeek(startOfWeek, endOfWeek);

        return moduleMapper.setOfModulesToSetOfModuleDtos(modulesForNextWeek);
    }
}
