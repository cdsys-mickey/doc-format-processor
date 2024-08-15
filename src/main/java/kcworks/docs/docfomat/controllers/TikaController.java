package kcworks.docs.docfomat.controllers;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import kcworks.docs.docfomat.DocFormatMeta;
import kcworks.docs.docfomat.parsers.DocFormatParserSettings;
import kcworks.docs.docfomat.parsers.tika.TikaDocFormatParser;
import kcworks.spring.http.SpringHttpHeaders;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/tika")
@RestController
public class TikaController {
	private static final int BUFFER_SIZE = 4096;
	private static final String PARAM_META = "meta";
	@Autowired
	private TikaDocFormatParser parser;

	/**
	 * MultipartFile 輸入, 串流方式輸出
	 * @param response
	 * @param file
	 * @param params
	 */
	@SneakyThrows
	@PostMapping("retrieve")
	public void retrieveFromFile(
			HttpServletResponse response
			, @RequestParam MultipartFile file
			, @RequestParam Map<String, String> params){
		if(file == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		String sourceFileName = file.getOriginalFilename();
		String sourceExtension = Files.getFileExtension(sourceFileName);

		//來源檔名必須有副檔名
		if(Strings.isNullOrEmpty(sourceExtension)){
			throw new IllegalArgumentException("source file name \"" + sourceFileName + "\" has no file extension");
		}

		String targetFileName = Files.getNameWithoutExtension(sourceFileName) + ".txt";

		retrieveAndOutput(response, params, file.getInputStream(), sourceFileName, targetFileName);
	}

	/**
	 * MultipartFile 輸入, DocFormatMeta 輸出
	 * @param file
	 * @param params
	 * @return
	 */
	@SneakyThrows
	@PostMapping("parse")
	public ResponseEntity parseFromFile(
			@RequestParam MultipartFile file
			, @RequestParam Map<String, String> params){
		if(file == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		String sourceFileName = file.getOriginalFilename();
		String sourceExtension = Files.getFileExtension(sourceFileName);
		//來源檔名必須有副檔名
		if(Strings.isNullOrEmpty(sourceExtension)){
			throw new IllegalArgumentException("source file name \"" + sourceFileName + "\" has not extension");
		}
		String targetFileName = Files.getNameWithoutExtension(sourceFileName) + ".txt";

		DocFormatMeta result = parser.parse(file.getInputStream(), sourceFileName, DocFormatParserSettings.forParsing(params));
		return buildDocFormatMetaToEntity(result, targetFileName);
	}

	/**
	 * byte array 輸入, 串流方式輸出
	 * @param response
	 * @param body
	 * @param params
	 */
	@SneakyThrows
	@PostMapping("retrieve-body")
	public void retrieveFromBody(
			HttpServletResponse response
			, @RequestBody byte[] body
			, @RequestParam Map<String, String> params){
		if(body == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		DocFormatMeta output = parser.parse(body, DocFormatParserSettings.forRetrieving(params));
		byte[] outputBytes = output.getFullText().getBytes(StandardCharsets.UTF_8);

		retrieveAndOutput(response, params, new ByteArrayInputStream(outputBytes), null, null);
	}

	/**
	 * byte array 輸入, DocFormatMeta 輸出
	 * @param body
	 * @param params
	 * @return
	 */
	@SneakyThrows
	@PostMapping("parse-body")
	public ResponseEntity parseFromBody(
			@RequestBody byte[] body
			, @RequestParam Map<String, String> params){
		if(body == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		DocFormatMeta output = parser.parse(body, DocFormatParserSettings.forParsing(params));

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.headers(SpringHttpHeaders.ofInline())
				.body(output);
	}

	private ResponseEntity buildDocFormatMetaToEntity(DocFormatMeta meta, String targetFileName) throws IOException {

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.headers(SpringHttpHeaders.ofInline(targetFileName))
				.body(meta);
	}

	private void retrieveAndOutput(HttpServletResponse response, Map<String, String> params, InputStream inputStream, String sourceFileName, String targetFileName) throws IOException {
		char[] buffer = new char[BUFFER_SIZE];
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		try(Reader reader = parser.retrieve(inputStream, sourceFileName, DocFormatParserSettings.forRetrieving(params))){
			// STATUS
			response.setStatus(HttpStatus.OK.value());

			// HEADER
			SpringHttpHeaders.buildForInline(targetFileName).build().toResponse(response);

			// output starts
			int readBytes;
			int totalBytes = 0;
			Writer writer = response.getWriter();
			while((readBytes = reader.read(buffer)) > 0){
				writer.write(buffer, 0, readBytes);
				totalBytes += readBytes;
			}
			log.debug("  {} bytes written", totalBytes);
			response.getWriter().flush();
		}
	}
}
