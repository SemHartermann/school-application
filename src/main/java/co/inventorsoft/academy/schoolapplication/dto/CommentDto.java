package co.inventorsoft.academy.schoolapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
@ToString
public class CommentDto {
    @NotNull
    Long id;

    @NotNull
    Long userId;

    Long parentId;

    @NotBlank
    String text;

    List<CommentDto> subComments;

    @NotNull
    Long postId;
}