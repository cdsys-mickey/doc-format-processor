package kcworks;

import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


public class MiscTest {
	@Test
	public void shouldCompressLinkBreaks(){
		String original = "\n\r\n\n\r\n\r\r\r\n";
		String expected = "\n";
		assertThat(original.replaceAll("(\n\r|\n\r|\n|\r)+", "\n")).isEqualTo(expected);
		
	}

	@Test
	public void shouldCompressSpaces(){
		String original = "   ";
		String expected = " ";
		assertThat(original.replaceAll("\\s+", " ")).isEqualTo(expected);

		original = "   \n  ";
		expected = " \n ";
		assertThat(original.replaceAll(" +", " ")).isEqualTo(expected);

		original = " ";
		expected = " ";
		assertThat(original.replaceAll("\\s+", " ")).isEqualTo(expected);

		original = "";
		expected = "";
		assertThat(original.replaceAll("\\s+", " ")).isEqualTo(expected);

	}
}
