package co.inventorsoft.academy.schoolapplication.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import co.inventorsoft.academy.schoolapplication.util.CSVUtils;
import com.opencsv.bean.CsvBindByName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class CVSUnitTests {

	@Test
	void givenCsvFile_whenConvertToModel_thenCheckIfSuccess() {
		MultipartFile multipartFile;
		try {
			multipartFile = new MockMultipartFile("testFile.csv", "testFile.csv", "text/csv",
				new FileInputStream("src/test/resources/testFile.csv"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		List<Article> articles = CSVUtils.convertToModel(multipartFile, Article.class);

		Article article1 = articles.get(0);

		assertThat(articles).hasSize(8);
		assertThat(article1.getTitle()).isEqualTo("Spring Boot Tut#1");

	}

	@Test
	void givenCsvFile_whenConvertToModel_thenThrowException() {
		assertThatExceptionOfType(FileNotFoundException.class).isThrownBy(() ->
			CSVUtils.convertToModel(new MockMultipartFile(
				"wrongFile.csv", "wrongFile.csv", "text/csv",
				new FileInputStream("src/test/resources/wrongFile.csv")), Article.class));

	}

	@Test
	void givenWrongClassForConversion_whenConvertToModel_thenThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
			CSVUtils.convertToModel(new MockMultipartFile(
				"testFile.csv", "testFile.csv", "text/csv",
				new FileInputStream("src/test/resources/testFile.csv")), WrongArticle.class));

	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Article{
		@CsvBindByName(column = "id", required = true)
		private Long id;

		@CsvBindByName(column = "title", required = true)
		private String title;

		@CsvBindByName(column = "description", required = true)
		private String description;

		@CsvBindByName(column = "published", required = true)
		private boolean published;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class WrongArticle{
		@CsvBindByName(column = "id",required = true)
		private Long id;

		@CsvBindByName(column = "title", required = true)
		private String title;

		@CsvBindByName(column = "description", required = true)
		private String description;

		@CsvBindByName(column = "published", required = true)
		private boolean published;

		@CsvBindByName(column = "created", required = true)
		private boolean created;
	}

}
