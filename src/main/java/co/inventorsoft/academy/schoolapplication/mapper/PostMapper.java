package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.PostDto;
import co.inventorsoft.academy.schoolapplication.entity.Post;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostMapper MAPPER = Mappers.getMapper(PostMapper.class);

    @Mapping(source = "author.id", target = "authorId")
    PostDto postToDto(Post post);

    @InheritInverseConfiguration
    Post postDtoToEntity(PostDto postDto);
}
