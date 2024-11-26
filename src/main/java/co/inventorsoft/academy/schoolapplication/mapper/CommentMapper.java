package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.CommentDto;
import co.inventorsoft.academy.schoolapplication.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface CommentMapper {
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "parentId", source = "parentComment.id"),
            @Mapping(target = "postId", source = "post.id"),
            @Mapping(target = "text", source = "text"),
            @Mapping(target = "subComments", source = "childrenComments")
    })
    CommentDto commentToDto(Comment comment);

    Comment commentDtoToEntity(CommentDto commentDto);
}