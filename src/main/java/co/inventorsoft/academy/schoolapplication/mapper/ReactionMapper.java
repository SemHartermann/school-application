package co.inventorsoft.academy.schoolapplication.mapper;

import co.inventorsoft.academy.schoolapplication.dto.ReactionDto;
import co.inventorsoft.academy.schoolapplication.entity.Reaction;
import org.mapstruct.Mapper;

@Mapper
public interface ReactionMapper {
    ReactionDto reactionToDto(Reaction reaction);

    Reaction reactionDtoToEntity(ReactionDto reactionDto);
}