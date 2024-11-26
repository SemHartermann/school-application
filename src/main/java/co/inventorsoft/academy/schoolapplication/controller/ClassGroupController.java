package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.RequestClassGroupDto;
import co.inventorsoft.academy.schoolapplication.dto.ResponseClassGroupDto;
import co.inventorsoft.academy.schoolapplication.service.ClassGroupService;
import jakarta.validation.Valid;
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
@RequiredArgsConstructor
@RequestMapping("/api/classes")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassGroupController {
  ClassGroupService classGroupService;

  @PostMapping
  public ResponseClassGroupDto createClassGroup(@Valid @RequestBody RequestClassGroupDto classGroupDto) {
    return classGroupService.createClassGroup(classGroupDto);
  }

  @GetMapping("/{id}")
  public ResponseClassGroupDto getById(@PathVariable Long id) {
    return classGroupService.getClassGroupById(id);
  }

  @GetMapping
  public Page<ResponseClassGroupDto> getAllClassGroups(@PageableDefault Pageable pageable) {
    return classGroupService.getAll(pageable);
  }

  @PutMapping
  public ResponseClassGroupDto updateClassGroup(@Valid @RequestBody RequestClassGroupDto classGroupDto) {
    return classGroupService.update(classGroupDto);
  }

  @PutMapping("/{classGroupId}/{studentId}")
  public void addStudentToClassGroup(@PathVariable Long classGroupId, @PathVariable Long studentId) {
    classGroupService.addStudentToClassGroup(classGroupId, studentId);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteClassGroup(@PathVariable Long id) {
    classGroupService.delete(id);
  }
}
