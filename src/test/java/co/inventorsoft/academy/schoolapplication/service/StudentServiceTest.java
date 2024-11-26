package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.student.StudentRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.student.StudentResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.StudentServiceImpl;
import co.inventorsoft.academy.schoolapplication.util.excepion.ModelValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @InjectMocks
    private StudentServiceImpl studentService;

    @Mock
    private StudentRepository studentRepository;

    @Test
    void testAddNewStudent() {
        StudentRequestDto studentRequestDto = new StudentRequestDto(
                "John", "Doe", "john@example.com", "1234567890");
        StudentResponseDto expectedResponse = new StudentResponseDto(
                1L, "John", "Doe", "john@example.com", "1234567890");

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

        when(studentRepository.save(studentCaptor.capture())).thenAnswer(invocation -> {
            Student capturedStudent = studentCaptor.getValue();
            capturedStudent.setId(1L);
            return capturedStudent;
        });

        StudentResponseDto response = studentService.addNewStudent(studentRequestDto);

        assertThat(response).isEqualTo(expectedResponse);

        verify(studentRepository, times(1)).save(studentCaptor.capture());
    }

    @Test
    void testGetAllStudents() {
        Pageable pageable = PageRequest.of(0, 10);

        StudentResponseDto studentDto1 = new StudentResponseDto(
                1L,
                "John",
                "Doe",
                "john@example.com",
                "1234567890");
        StudentResponseDto studentDto2 = new StudentResponseDto(
                2L,
                "Jane",
                "Smith",
                "jane@example.com",
                "9876543210");

        Student student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane@example.com");
        student2.setPhone("9876543210");

        Page<StudentResponseDto> expectedResponse = new PageImpl<>(List.of(
                studentDto1, studentDto2
        ));

        when(studentRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(
                student1, student2
        )));

        Page<StudentResponseDto> response = studentService.getAllStudents(PageRequest.of(0, 10));

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response).isEqualTo(expectedResponse);

        verify(studentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetStudentById() {
        Long studentId = 1L;

        Student student1 = new Student();
        student1.setId(studentId);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setPhone("1234567890");

        StudentResponseDto expectedResponse = new StudentResponseDto(studentId, "John", "Doe", "john@example.com", "1234567890");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student1));

        StudentResponseDto response = studentService.getStudentById(studentId);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testUpdateStudentById() {
        // Arrange
        Long studentId = 1L;

        Student existingStudent = new Student();
        existingStudent.setId(studentId);
        existingStudent.setFirstName("John");
        existingStudent.setLastName("Doe");
        existingStudent.setEmail("john@example.com");
        existingStudent.setPhone("1234567890");

        StudentRequestDto studentRequestDto = new StudentRequestDto("Updated_John", "Updated_Doe", "john@example.com", "1234567890");
        StudentResponseDto expectedResponse = new StudentResponseDto(1L, "Updated_John", "Updated_Doe", "john@example.com", "1234567890");

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponseDto response = studentService.updateStudentById(studentId, studentRequestDto);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void testDeleteStudentById() {
        Long studentId = 1L;

        Student student = new Student();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        studentService.deleteStudentById(studentId);

        assertThat(student.isDeleted()).isTrue();

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testGetStudentById_NotFound() {
        Long studentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(studentId))
                .isInstanceOf(ResponseStatusException.class);

        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testUploadFromFileWithCSVFile() {
        String csvContent = "firstName,lastName,email,phone\nJohn,Doe,john.doe@example.com,1234567890";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                csvContent.getBytes());

        ArgumentCaptor<List<Student>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        studentService.uploadFromFile(file);

        verify(studentRepository).saveAll(argumentCaptor.capture());
        List<Student> capturedStudents = argumentCaptor.getValue();

        assertThat(capturedStudents).asList().isNotEmpty();
        assertThat(capturedStudents.get(0).getFirstName()).isEqualTo("John");
        assertThat(capturedStudents.get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void testUploadFromFileWithExcelFile() throws IOException {
        String excelFilePath = "src/test/resources/students.xlsx";
        byte[] excelContent = convertExcelFileToByteArray(excelFilePath);

        MockMultipartFile excelFile = new MockMultipartFile(
                "file",
                "students.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent
        );

        ArgumentCaptor<List<Student>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        studentService.uploadFromFile(excelFile);

        verify(studentRepository).saveAll(argumentCaptor.capture());
        List<Student> capturedStudents = argumentCaptor.getValue();

        assertThat(capturedStudents).asList().isNotEmpty();
        assertThat(capturedStudents.get(0)).extracting("firstName", "lastName", "email", "phone")
                .containsExactly("John", "Doe", "john.doe@example.com", "1234567890");
    }

    public byte[] convertExcelFileToByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        return Files.readAllBytes(file.toPath());
    }

    @Test
    void testUploadFromFileWithUnsupportedFileType() {
        MockMultipartFile unsupportedFile = new MockMultipartFile(
                "file",
                "unsupported.txt",
                "text/plain",
                "Some content".getBytes());

        assertThatThrownBy(() -> studentService.uploadFromFile(unsupportedFile))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUploadFromFileWithInvalidDataFormat() throws IOException {
        MockMultipartFile invalidExcelFile = new MockMultipartFile(
                "file",
                "invalid_data.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createInvalidExcelContent());

        assertThatThrownBy(() -> studentService.uploadFromFile(invalidExcelFile))
                .isInstanceOf(ModelValidationException.class);
    }

    @Test
    void testUploadFromFileWithMissingRequiredFields() {
        String csvContentWithMissingFields = "firstName,lastName\nJohn,";
        MockMultipartFile csvFileWithMissingFields = new MockMultipartFile(
                "file",
                "missing_fields.csv",
                "text/csv",
                csvContentWithMissingFields.getBytes());

        assertThatThrownBy(() -> studentService.uploadFromFile(csvFileWithMissingFields))
                .isInstanceOf(ModelValidationException.class);
    }

    private byte[] createInvalidExcelContent() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("firstName");
        row.createCell(1).setCellValue("lastName");
        row.createCell(2).setCellValue("email");
        row.createCell(3).setCellValue("phone");

        // Add an invalid data row
        row = sheet.createRow(1);
        row.createCell(2).setCellValue("not-an-email");        // Invalid email format
        row.createCell(3).setCellValue("12345f6");             // Invalid phone format

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}