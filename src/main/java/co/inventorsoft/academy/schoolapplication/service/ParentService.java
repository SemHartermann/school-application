package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.ParentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.ParentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParentService {
    ParentResponseDto addParent(ParentRequestDto parentRequestDto);

    Page<ParentResponseDto> getAllParents(Pageable pageable);

    ParentResponseDto getParentById(Long id);

    ParentResponseDto updateParentById(Long id, ParentRequestDto parentRequestDto);

    void deleteParentById(Long id);
}
