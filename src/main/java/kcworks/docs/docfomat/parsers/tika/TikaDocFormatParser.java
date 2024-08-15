package kcworks.docs.docfomat.parsers.tika;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import kcworks.docs.docfomat.DocFormatMeta;
import kcworks.docs.docfomat.parsers.BaseDocFormatParser;
import kcworks.docs.docfomat.parsers.DocFormatParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TikaDocFormatParser extends BaseDocFormatParser {
	public static final String TIKA_CONFIG_FILE = "tika-config.xml";
	private static final String[] OOXML_PROTECTED_CONTENT_TYPES = {
			"application/x-tika-ooxml-protected"
	};

	private static final String[] OOXML_EXTS = {
			".xlsx",
			".docx",
			".pptx"
	};


	private Tika tika = null;
	private TikaConfig tikaConfig;

	public TikaDocFormatParser() {
		log.debug("TikaParser created");
	}

	private Tika getTikaFacade() throws TikaException, IOException, SAXException {
		if (this.tika == null) {
			synchronized (TikaDocFormatParser.class) {
				if (tika == null) {
					log.info("/ INITIALIZING Tika...");
					Stopwatch stopwatch = Stopwatch.createStarted();
					URL configURL = getClass().getClassLoader().getResource(TIKA_CONFIG_FILE);
					log.info("|  using {} from {}", TIKA_CONFIG_FILE, configURL);
					TikaConfig tikaConfig = configURL != null ? new TikaConfig(configURL) : new TikaConfig();
					this.tikaConfig = tikaConfig;
					this.tika = new Tika(tikaConfig);
					log.info("\\ INITIALIZATION done ({}s)", stopwatch.elapsed(TimeUnit.SECONDS));
				}
			}
		}
		return this.tika;
	}

	private TikaConfig getTikaConfig(){
		return this.tikaConfig;
	}

	@Override
	public Reader retrieve(InputStream input, String fileName, DocFormatParserSettings settings) throws IOException {
		try{
			Stopwatch stopwatch = null;
			if (log.isDebugEnabled()) {
				log.debug("retrieving InputStream of {} ...", fileName);
				stopwatch = Stopwatch.createStarted();
			}
			Reader reader = getTikaFacade().parse(TikaInputStream.get(input));
			if (log.isDebugEnabled()) {
				log.debug("  done {}({}s)", fileName, stopwatch.elapsed(TimeUnit.SECONDS));
			}
			return reader;
		} catch (TikaException | SAXException e) {
			throw new IOException(e);
		}
	}



	@Override
	public DocFormatMeta parse(InputStream inputStream, String fileName, DocFormatParserSettings settings) throws IOException {
		try {
			DocFormatMeta meta = new DocFormatMeta();
			meta.setFileName(fileName);
			Stopwatch stopwatch = Stopwatch.createUnstarted();
			Metadata metadata = new Metadata();

			TikaInputStream tikaInputStream;
			if(settings.getInputMaxMegaBytes() > 0){
				byte[] bytes = readInputStream(inputStream, settings.getInputMaxMegaBytes());
				tikaInputStream = TikaInputStream.get(bytes);
			}else{
				tikaInputStream = TikaInputStream.get(inputStream);
			}
			// InputStream tikaInputStream = TikaInputStream.get(inputStream);
			// Full Text
			if(settings.isRetrieveFullText()){

				if (log.isDebugEnabled()) {
					if(Strings.isNullOrEmpty(fileName)){
						log.debug("parsing InputStream without file name");
					} else {
						log.debug("parsing InputStream of {} ...", fileName);
					}
					stopwatch.start();
				}

				String fullText = settings.getOutputMaxMegaBytes() > 0
						? getTikaFacade().parseToString(tikaInputStream, metadata
							, (int) DataSize.ofMegabytes(settings.getOutputMaxMegaBytes()).toBytes())
						: getTikaFacade().parseToString(tikaInputStream, metadata);
				if (log.isDebugEnabled()) {
					stopwatch.stop();
					log.debug("  parsing done ({}s)", stopwatch.elapsed(TimeUnit.SECONDS));
				}
				// Compress Line Breaks
				if(settings.isCompressLinkBreaks()){
					fullText = fullText
							.replaceAll("(\r\n|\n\r|\n|\r)+", "\n")
							;
				}
				// Compress Spaces
				if(settings.isCompressSpaces()){
					fullText = fullText
							.replaceAll(" +", " ")
							;
				}
				meta.setFullText(fullText);
			}

			// Content Type
			if(settings.isDetectContentType()){
				if (log.isDebugEnabled()) {
					if(Strings.isNullOrEmpty(fileName)){
						log.debug("detecting content type without file name");
					}else{
						log.debug("detecting Content-Type of {}", fileName);
					}
					stopwatch.start();
				}

				getTikaFacade().getDetector().detect(tikaInputStream, metadata);
				String contentType = getTikaFacade().detect(tikaInputStream);

				if (log.isDebugEnabled()) {
					stopwatch.stop();
					log.debug("  detected Content-Type: \"{}\" ({}s)",
							contentType,
							stopwatch.elapsed(TimeUnit.SECONDS));
				}
				meta.setContentType(contentType);
				// Extensions
				if(settings.isFetchExtensions()){
					if(Arrays.asList(OOXML_PROTECTED_CONTENT_TYPES).contains(contentType)){
						meta.setExtensions(Arrays.asList(OOXML_EXTS));
					}else{
						MimeType mimeType = getTikaConfig().getMimeRepository().forName(contentType);
						meta.setExtensions(mimeType.getExtensions());
						if(log.isDebugEnabled()){
							log.debug("  ext: {}", mimeType.getExtensions().stream().collect(Collectors.joining(", ")));
						}
					}

					// ExtensionMatched
					if(!Strings.isNullOrEmpty(fileName) && meta.getExtensions() != null){
						String ext = "." + Files.getFileExtension(fileName);
						meta.setExtMatched(meta.getExtensions().stream()
								.anyMatch(s -> s.equalsIgnoreCase(ext)));
					}
				}
			}
			return meta;
		} catch (TikaException | SAXException e) {
			throw new IOException(e);
		}
	}

	@Override
	public DocFormatMeta parse(Path filePath, DocFormatParserSettings settings) throws IOException {
		return parse(TikaInputStream.get(filePath), filePath.getFileName().toString(), settings);
	}

	@Override
	public DocFormatMeta parse(byte[] body, DocFormatParserSettings settings) throws IOException {
		return parse(new ByteArrayInputStream(body), null, settings);
	}

	@Override
	public DocFormatMeta parse(File file, DocFormatParserSettings settings) throws IOException {
		return parse(TikaInputStream.get(file), file.getName(), settings);
	}

	@Override
	public void reload() {
		this.tika = null;
		log.info("tika facade instance cleared");
	}
}
