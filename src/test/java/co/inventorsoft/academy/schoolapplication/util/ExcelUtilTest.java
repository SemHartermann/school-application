package co.inventorsoft.academy.schoolapplication.util;

import co.inventorsoft.academy.schoolapplication.util.excepion.excel.EmptyExcelFileException;
import co.inventorsoft.academy.schoolapplication.util.excepion.ModelValidationException;
import co.inventorsoft.academy.schoolapplication.util.excepion.excel.NoExcelFileExtensionException;
import co.inventorsoft.academy.schoolapplication.util.excepion.excel.UnsupportedExcelFormatException;
import co.inventorsoft.academy.schoolapplication.util.model.Person;
import co.inventorsoft.academy.schoolapplication.util.validation.validator.PersonValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelUtilTest {

    @Test
    void whenFileIsEmpty_thenThrowIllegalArgumentException() {
        MultipartFile emptyFile = new MockMultipartFile("data", "test.xlsx", "text/plain", new byte[0]);
        String expectedErrorMsg = String.format("The provided file %s is empty!", emptyFile.getOriginalFilename());

        assertThatThrownBy(() -> ExcelUtil.excelDataToEntityList(emptyFile, Person.class, new PersonValidator()))
                .isInstanceOf(EmptyExcelFileException.class)
                .hasMessageContaining(expectedErrorMsg);
    }

    @Test
    void whenFileExtensionIsAbsent_thenThrowIllegalArgumentException() {
        MultipartFile file = new MockMultipartFile("data", "test", "text/plain", new byte[5]);
        String expectedErrorMsg = String.format("Invalid file %s: The provided file doesn't have an extension. Please upload an Excel file with an '.xls' or '.xlsx' extension!", file.getOriginalFilename());

        assertThatThrownBy(() -> ExcelUtil.excelDataToEntityList(file, Person.class, new PersonValidator()))
                .isInstanceOf(NoExcelFileExtensionException.class)
                .hasMessageContaining(expectedErrorMsg);
    }

    @Test
    void whenUnsupportedFileTypeIsUploaded_thenThrowIllegalArgumentException() {
        byte[] content = "test content".getBytes();
        MultipartFile mockPdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                content
        );

        assertThatThrownBy(() -> ExcelUtil.excelDataToEntityList(mockPdfFile, Person.class, new PersonValidator()))
                .isInstanceOf(UnsupportedExcelFormatException.class)
                .hasMessageContaining("PDF file format is not supported!");

        //docx
        MultipartFile mockDocxFile = new MockMultipartFile(
                "file",
                "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                content
        );

        assertThatThrownBy(() -> ExcelUtil.excelDataToEntityList(mockDocxFile, Person.class, new PersonValidator()))
                .isInstanceOf(UnsupportedExcelFormatException.class)
                .hasMessageContaining("DOCX file format is not supported!");
    }

    @Test
    void whenFileIsNotEmpty_withoutErrors_thenConvertExcelDataToEntityList() throws Exception {
        String testFilePath = "co/inventorsoft/academy/schoolapplication/util/testFile.xlsx";
        byte[] excelData;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(testFilePath)) { //getClassLoader().
            if (Objects.isNull(is)) {
                throw new FileNotFoundException("File not found: " + testFilePath);
            }
            excelData = is.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Can't read Excel file from path: " + testFilePath);
        }

        MultipartFile file = new MockMultipartFile("data", "testFile.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelData);

        List<Person> persons = ExcelUtil.excelDataToEntityList(file, Person.class, new PersonValidator());

        assertThat(persons).hasSize(4);

        Person person1 = persons.get(0);
        assertThat(person1)
                .hasFieldOrPropertyWithValue("firstname", "William")
                .hasFieldOrPropertyWithValue("lastname", "Wallace")
                .hasFieldOrPropertyWithValue("phone", "+1-323-9324-21-231")
                .hasFieldOrPropertyWithValue("email", "wallace@gmail.com")
                .hasFieldOrPropertyWithValue("address", "TreeWood str 12, Elderslie, Renfrewshire, Scotland");

        Person person2 = persons.get(1);
        assertThat(person2)
                .hasFieldOrPropertyWithValue("firstname", "Sarah")
                .hasFieldOrPropertyWithValue("lastname", "o'Connor")
                .hasFieldOrPropertyWithValue("phone", "+1-342-6354-41-734")
                .hasFieldOrPropertyWithValue("email", "sarah.o.connor@gmail.com")
                .hasFieldOrPropertyWithValue("address", "Skynet str. 744/1, New-York, USA");

        Person person3 = persons.get(2);
        assertThat(person3)
                .hasFieldOrPropertyWithValue("firstname", "Obi-Wan")
                .hasFieldOrPropertyWithValue("lastname", "Kenobi")
                .hasFieldOrPropertyWithValue("phone", null)
                .hasFieldOrPropertyWithValue("email", "obi.one@mail.com")
                .hasFieldOrPropertyWithValue("address", null);

        Person person4 = persons.get(3);
        assertThat(person4)
                .hasFieldOrPropertyWithValue("firstname", "TestFirstName")
                .hasFieldOrPropertyWithValue("lastname", "TestLastName")
                .hasFieldOrPropertyWithValue("phone", "+0-000-000-000")
                .hasFieldOrPropertyWithValue("email", "test@email.com")
                .hasFieldOrPropertyWithValue("address", "TestAddress");
    }

    @Test
    void whenFileIsNotEmpty_withErrors_thenReceiveValidationReport() throws Exception {
        String testFilePath = "co/inventorsoft/academy/schoolapplication/util/testFile_with_incorrect_fields.xlsx";
        byte[] excelData;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(testFilePath)) { //getClassLoader().
            if (Objects.isNull(is)) {
                throw new FileNotFoundException("File not found: " + testFilePath);
            }
            excelData = is.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Can't read Excel file from path: " + testFilePath);
        }

        MultipartFile mockFile = new MockMultipartFile("data", "testFile.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelData);

        assertThatThrownBy(() -> ExcelUtil.excelDataToEntityList(mockFile, Person.class, new PersonValidator()))
                .isInstanceOf(ModelValidationException.class)
                .extracting("validationResult")
                .isNotNull();
    }
}