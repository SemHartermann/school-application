package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.ModuleDto;
import co.inventorsoft.academy.schoolapplication.service.ModuleService;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/modules")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ModuleController {
    ModuleService moduleService;

    @GetMapping("/subjects/{subjectId}/modules")
    public Page<ModuleDto> getAllModulesBySubjectId(@PageableDefault Pageable pageable, @PathVariable Long subjectId) {
        return moduleService.getAllModulesBySubjectId(pageable, subjectId);
    }

    @GetMapping("/{id}")
    public ModuleDto getModule(@PathVariable Long id) {
        return moduleService.getModule(id).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModuleDto createModule(@RequestBody @Valid ModuleDto moduleDto) {
        Optional<ModuleDto> savedLessonDto = moduleService.createModule(moduleDto);

        return savedLessonDto.orElseThrow();
    }

    @PutMapping("/{id}")
    public ModuleDto updateModuleById(@PathVariable("id") Long id, @RequestBody @Valid ModuleDto moduleDto){
        return moduleService.updateModuleById(id, moduleDto).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
    }

}
