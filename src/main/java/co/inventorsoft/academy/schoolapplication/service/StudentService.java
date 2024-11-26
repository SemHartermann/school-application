package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    StudentResponseDto addNewStudent(StudentRequestDto studentRequestDto);

    Page<StudentResponseDto> getAllStudents(Pageable pageable);

    StudentResponseDto getStudentById(Long id);

    StudentResponseDto updateStudentById(Long id, StudentRequestDto studentRequestDto);

    void deleteStudentById(Long id);

    Student getExistingStudentById(Long id);

    void saveStudents(List<StudentRequestDto> studentDtoList);

    void uploadFromFile(MultipartFile file);

    void processCSVFile(MultipartFile file);

    void processExcelFile(MultipartFile file);

    String getParentEmailByStudentId(Long studentId);
}
