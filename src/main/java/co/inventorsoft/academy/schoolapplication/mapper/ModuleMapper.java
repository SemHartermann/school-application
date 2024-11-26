package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.entity.Module;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    ModuleMapper MAPPER = Mappers.getMapper(ModuleMapper.class);

    @Mapping(source = "schedule", target = "schedule")
    ModuleDto toDto(Module module);

    @InheritInverseConfiguration
    Module toEntity(ModuleDto dto);

    void updateModuleFromDto(ModuleDto dto, @MappingTarget Module module);

    Set<ModuleDto> setOfModulesToSetOfModuleDtos(Set<Module> modules);

}