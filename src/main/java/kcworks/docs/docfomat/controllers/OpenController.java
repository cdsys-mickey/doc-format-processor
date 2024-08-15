package kcworks.docs.docfomat.controllers;

import kcworks.docs.docfomat.converters.jodc.JODFormatConverter;
import kcworks.spring.http.SpringHttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("/api/v1/odf/open")
@RestController
public class OpenController {

	@Autowired
	protected JODFormatConverter multipartFileToResponse;

	/**
	 * @param sourceFile
	 * @param target - 可以是檔名, 也可以是格式名
	 * @return
	 */
	@PostMapping("as/{target}")
	public ResponseEntity openAs(
			@RequestParam(name = "file") MultipartFile sourceFile
			, @PathVariable String target ){
		return multipartFileToResponse.convert(sourceFile, target, SpringHttpHeaders.ContentDispositionType.INLINE);
	}

}
