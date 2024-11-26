package co.inventorsoft.academy.schoolapplication.dto;

import co.inventorsoft.academy.schoolapplication.entity.enums.ReactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReactionDto {
    ReactionType reactionType;
    Long postId;
    Long userId;
}
