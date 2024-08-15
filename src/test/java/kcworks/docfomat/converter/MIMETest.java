package kcworks.docfomat.converter;

import kcworks.console.Consoles;
import org.apache.tika.Tika;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class MIMETest {

	@Test
	public void testTika(){
		String source = "test.docx";
		Tika tika = new Tika();
		String actual = tika.detect(source);
		Consoles.printf("%s -> %s", source, actual);
	}

	@Test
	public void testSpring(){
		String mimeType = "application/pdf";
		MediaType.parseMediaType(mimeType);
	}
}
