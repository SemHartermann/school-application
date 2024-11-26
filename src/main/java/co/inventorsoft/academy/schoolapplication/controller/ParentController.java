package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ParentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.ParentResponseDto;
import co.inventorsoft.academy.schoolapplication.service.ParentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parents")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ParentController {
    ParentService parentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentResponseDto saveParent(@RequestBody @Valid ParentRequestDto parentRequestDto) {
        return parentService.addParent(parentRequestDto);
    }


    @GetMapping("/{id}")
    public ParentResponseDto getParentById(@PathVariable Long id) {
        return parentService.getParentById(id);
    }

    @GetMapping
    public Page<ParentResponseDto> getAllParents(@PageableDefault Pageable pageable) {
        return parentService.getAllParents(pageable);
    }

    @PutMapping("/{id}")
    public ParentResponseDto editParentById(@PathVariable Long id, @RequestBody @Valid ParentRequestDto parentRequestDto) {
        return parentService.updateParentById(id, parentRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGameById(@PathVariable Long id) {
        parentService.deleteParentById(id);
    }
}
