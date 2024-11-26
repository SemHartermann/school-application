package co.inventorsoft.academy.schoolapplication.service;


import co.inventorsoft.academy.schoolapplication.dto.SubjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface SubjectService {

    Page<SubjectDto> getAllSubjects(Pageable pageable);

    SubjectDto getSubjectById(Long id);

    void addNewSubject(SubjectDto subjectDto);

    void updateSubjectById(Long id, SubjectDto requestBody);

    void deleteSubjectById(Long id);

    void uploadFromFile(MultipartFile file);
}
