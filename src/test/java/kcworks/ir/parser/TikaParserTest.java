package kcworks.ir.parser;

import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import kcworks.env.AppContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeType;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class TikaParserTest {
	@SneakyThrows
	@Test
	public void shouldParseToString() {
		URL configURL = getClass().getClassLoader().getResource("tika-config.xml");
		log.debug("configURL: {}", configURL);
		TikaConfig config = new TikaConfig(configURL);

		Tika tika = new Tika(config);

		Path filePath = AppContext.getCurrentWorkingDirPath(
				"src/test/resources/案件彙報管理系統源碼檢測報告_2211302.pdf");
		Stopwatch stopwatch = Stopwatch.createStarted();
		String text = tika.parseToString(filePath);
		log.debug("parseToString ELAPSED [{}]", stopwatch.elapsed(TimeUnit.SECONDS));
		log.debug("retrieved text from {}:", filePath);
		log.debug(text);

		// filePath = AppContext.getCurrentWorkingDirPath(
		// 		"src/test/resources/conversion-test.docx");
		// text = tika.parseToString(filePath);
		// log.debug("retrieved text from {}:", filePath);
		// log.debug(text);
	}

	@SneakyThrows
	@Test
	public void shouldParseAsReader() {
		URL configURL = getClass().getClassLoader().getResource("tika-config.xml");
		log.debug("configURL: {}", configURL);
		TikaConfig config = new TikaConfig(configURL);

		Tika tika = new Tika(config);

		Path filePath = AppContext.getCurrentWorkingDirPath(
				"src/test/resources/監察院112內調12 -回應說明V2F(組長修)-1120315.pdf");
		Stopwatch stopwatch = Stopwatch.createStarted();
		Reader reader = tika.parse(filePath);
		stopwatch.stop();
		log.debug("parse {}s ELAPSED", stopwatch.elapsed(TimeUnit.SECONDS));
		stopwatch.start();
		String text = CharStreams.toString(reader);
		stopwatch.stop();
		log.debug("reader to string {}s ELAPSED", stopwatch.elapsed(TimeUnit.SECONDS));
		log.debug("retrieved text from {}: ", filePath);
		log.debug(text);
		stopwatch.start();
		String contentType = tika.detect(filePath);
		stopwatch.stop();
		log.debug("detect {}s ELAPSED", stopwatch.elapsed(TimeUnit.SECONDS));

		log.debug("contentType: {}", contentType);

	}

	@SneakyThrows
	@Test
	public void shouldDetectContentTypeAndExtensions(){
		URL configURL = getClass().getClassLoader().getResource("tika-config.xml");
		log.debug("configURL: {}", configURL);
		TikaConfig config = new TikaConfig(configURL);

		Tika tika = new Tika(config);

		Path filePath = AppContext.getCurrentWorkingDirPath(
				"src/test/resources/案件彙報管理系統源碼檢測報告_2211302.pdf");
		Metadata metadata = new Metadata();
		metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filePath.getFileName().toString());
		InputStream inputStream = TikaInputStream.get(filePath);
		String contentType = tika.detect(inputStream, metadata);
		log.debug("contentType: {}", contentType);

		MimeType mimeType = config.getMimeRepository().forName(contentType);
		log.debug("ext: {}", mimeType.getExtensions().stream().collect(Collectors.joining(", ")));

		filePath = AppContext.getCurrentWorkingDirPath(
				"src/test/resources/附件1+2未備查彙整.xls");
		contentType = tika.detect(filePath);
		log.debug(contentType);

		mimeType = config.getMimeRepository().forName(contentType);
		log.debug("ext: {}", mimeType.getExtensions().stream().collect(Collectors.joining(", ")));
	}

	@Test
	public void shouldDetectMalformedFiles(){

	}
}
