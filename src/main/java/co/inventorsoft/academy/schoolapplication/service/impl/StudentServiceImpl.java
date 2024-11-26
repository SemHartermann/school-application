package co.inventorsoft.academy.schoolapplication.service.impl;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.mapper.StudentMapper;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.StudentService;
import co.inventorsoft.academy.schoolapplication.util.CSVUtils;
import co.inventorsoft.academy.schoolapplication.util.ExcelUtil;
import co.inventorsoft.academy.schoolapplication.util.excepion.ModelValidationException;
import co.inventorsoft.academy.schoolapplication.util.excepion.excel.ExcelFileProcessingException;
import co.inventorsoft.academy.schoolapplication.util.validation.ValidationResult;
import co.inventorsoft.academy.schoolapplication.util.validation.validator.StudentValidator;
import co.inventorsoft.academy.schoolapplication.util.validation.validator.Validator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentServiceImpl implements StudentService {
    StudentRepository studentRepository;
    StudentMapper studentMapper = StudentMapper.MAPPER;

    @Override
    public StudentResponseDto addNewStudent(StudentRequestDto studentRequestDto) {
        Student newStudent = studentMapper.toEntity(studentRequestDto);

        return studentMapper.toResponseDto(studentRepository.save(newStudent));
    }

    @Override
    public Page<StudentResponseDto> getAllStudents(Pageable pageable) {
        Page<Student> students = studentRepository.findAll(pageable);

        return students.map(studentMapper::toResponseDto);
    }


    @Override
    public StudentResponseDto getStudentById(Long id) {
        Student existingStudent = getExistingStudentById(id);

        return studentMapper.toResponseDto(existingStudent);
    }

    @Override
    public StudentResponseDto updateStudentById(Long id, StudentRequestDto studentRequestDto) {
        Student existingStudent = getExistingStudentById(id);

        studentMapper.updateEntity(studentRequestDto, existingStudent);
        studentRepository.save(existingStudent);

        return studentMapper.toResponseDto(existingStudent);
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.findById(id).ifPresent(student -> {
            long millis = System.currentTimeMillis();
            String email = student.getEmail();
            String phone = student.getPhone();

            email = String.format("%s_%s", email, millis);
            phone = String.format("%s_%s", phone, millis);

            student.setEmail(email);
            student.setPhone(phone);
            student.setDeleted(true);

            studentRepository.save(student);
        });
    }

    @Override
    public Student getExistingStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @Override
    public void saveStudents(List<StudentRequestDto> studentDtoList) {
        List<Student> students = studentDtoList.stream()
                .map(studentMapper::toEntity)
                .toList();
        studentRepository.saveAll(students);
    }

    @Override
    public void uploadFromFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File must have a name.");
        }

        String fileType = FilenameUtils.getExtension(originalFilename).toLowerCase();

        if (fileType.equals("csv")) {
            processCSVFile(file);
        } else if (Arrays.asList("xls", "xlsx").contains(fileType)) {
            processExcelFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    @Override
    public void processCSVFile(MultipartFile file) {
        List<StudentRequestDto> studentDtoList = CSVUtils.convertToModel(file, StudentRequestDto.class);

        Validator<StudentRequestDto> validator = new StudentValidator();
        ValidationResult validationResult = new ValidationResult();

        for (int i = 0; i < studentDtoList.size(); i++) {
            validator.validate(studentDtoList.get(i), i + 1, validationResult);
        }

        if (!validationResult.getErrorDetails().isEmpty()) {
            throw new ModelValidationException(validationResult);
        }

        saveStudents(studentDtoList);
    }

    @Override
    public void processExcelFile(MultipartFile file) {
        Validator<StudentRequestDto> validator = new StudentValidator();

        try {
            List<StudentRequestDto> studentDtoList = ExcelUtil.excelDataToEntityList(file, StudentRequestDto.class, validator);
            saveStudents(studentDtoList);
        } catch (ExcelFileProcessingException | ModelValidationException e) {
            throw e;
        }
    }

    @Override
    public String getParentEmailByStudentId(Long studentId) {
        return studentRepository.findParentEmailByStudentId(studentId).orElse(null);
    }
}
