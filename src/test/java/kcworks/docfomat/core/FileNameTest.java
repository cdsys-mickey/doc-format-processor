package kcworks.docfomat.core;

import com.google.common.io.Files;
import kcworks.env.AppContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;

public class FileNameTest {
	@Test
	public void testFileName(){
		String fileName = "檔案名稱.ods";
		String actual = Files.getFileExtension(fileName);
		assertThat("ods", is(actual));

		fileName = "檔案名稱";
		actual = Files.getFileExtension(fileName);
		assertThat("", is(actual));
	}

	@SneakyThrows
	@Test
	public void folderNameTest(){
		String folderName = "folderTest";
		Path folderPath = AppContext.getTempDirPath(true, folderName);
		assertThat(folderName, is(folderPath.getFileName().toString()));
	}
}
