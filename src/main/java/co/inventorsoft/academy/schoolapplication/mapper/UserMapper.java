package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.user.UserRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    User toEntity(UserRequestDto dto);

    User userResponseDtoToUser(UserResponseDto dto);

    UserResponseDto userToUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(UserRequestDto userRequestDto, @MappingTarget User user);
}
