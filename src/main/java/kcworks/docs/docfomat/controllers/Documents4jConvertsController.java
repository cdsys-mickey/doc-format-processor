package kcworks.docs.docfomat.controllers;

import kcworks.docs.docfomat.converters.jodc.Documents4jFormatConverter;
import kcworks.spring.http.SpringHttpHeaders;
import kcworks.string.StringValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 同步模式
 */
@Slf4j
@RequestMapping("/api/v1/d4j/converts")
@RestController
public class Documents4jConvertsController {

	private static final String PARAM_MULTIPART_FILE = "file";
	private static final String PARAM_OPEN = "open";

	@Autowired
	protected Documents4jFormatConverter formatConverter;

	@PostMapping("to/{target}")
	public ResponseEntity open(@PathVariable String target, @RequestParam MultipartFile file, @RequestParam Map<String, String> params){
		// MultipartFile sourceFile = (MultipartFile) params.get(PARAM_MULTIPART_FILE);
		// MultipartFile sourceFile = (MultipartFile) params.get(PARAM_MULTIPART_FILE);
		boolean open = StringValue.from(params.get(PARAM_OPEN)).isNotNull();

		return formatConverter.convert(
				file, target
				, open ? SpringHttpHeaders.ContentDispositionType.INLINE : SpringHttpHeaders.ContentDispositionType.ATTACHMENT);
	}
}
