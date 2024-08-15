package kcworks.docfomat.converter;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import kcworks.env.AppContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class Documents4jTest {
	public static final String TEST_FILE_DOCX = "conversion-test.docx";
	public static final String TEST_FILE_PDF = "conversion-test.pdf";

	@SneakyThrows
	@Test
	public void test(){
		Path tempFolder = AppContext.getTempDirPath(true, "d4j-test");
		IConverter converter = LocalConverter.builder()
				.baseFolder(tempFolder.toFile())
				.build();

		InputStream is = getClass().getClassLoader().getResourceAsStream(TEST_FILE_DOCX);
		Path targetPath = tempFolder.resolve(TEST_FILE_PDF);
		Boolean result = converter
				.convert(is, true).as(DocumentType.DOCX)
				.to(targetPath.toFile()).as(DocumentType.PDF).execute();
		printf("result: %s", result);
	}

	private void printf(String message, Object... args){
		System.out.println(String.format(message, args));
	}

	@Test
	public void stressTest(){

	}
}
