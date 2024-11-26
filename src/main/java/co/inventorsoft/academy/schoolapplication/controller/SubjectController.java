package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import co.inventorsoft.academy.schoolapplication.service.SubjectService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectController {

    SubjectService subjectService;

    @GetMapping
    public Page<SubjectDto> getAllSubjects(@PageableDefault Pageable pageable) {
        return subjectService.getAllSubjects(pageable);
    }

    @GetMapping("/{id}")
    public SubjectDto getSubjectById(@PathVariable("id") Long id) {
        return subjectService.getSubjectById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewSubject(@RequestBody @Valid SubjectDto subjectDto) {
        subjectService.addNewSubject(subjectDto);
    }

    @PutMapping("/{id}")
    public void updateSubjectById(@PathVariable("id") Long id, @RequestBody @Valid SubjectDto requestBody) {
        subjectService.updateSubjectById(id, requestBody);
    }

    @DeleteMapping("/{id}")
    public void deleteSubjectById(@PathVariable("id") Long id) {
        subjectService.deleteSubjectById(id);
    }

    @PostMapping("/import")
    public void uploadSubjectFromFile(@RequestPart("file") MultipartFile file) {
        subjectService.uploadFromFile(file);
    }
}
