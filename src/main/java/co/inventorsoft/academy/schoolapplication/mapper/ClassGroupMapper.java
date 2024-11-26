package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import co.inventorsoft.academy.schoolapplication.entity.ClassGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClassGroupMapper {

    ClassGroupMapper MAPPER = Mappers.getMapper(ClassGroupMapper.class);

    @Mapping(target = "students", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ClassGroup requestClassGroupDtoToClassGroup(RequestClassGroupDto dto);

    ResponseClassGroupDto classGroupToResponseClassGroupDto(ClassGroup classGroup);
}
