package kcworks.docs.docfomat.controllers;

import com.google.common.base.Strings;
import kcworks.docs.docfomat.converters.jodc.JODFormatConverter;
import kcworks.docs.docfomat.provider.IFileProvider;
import kcworks.spring.http.SpringHttpHeaders;
import kcworks.spring.rest.ApiResponse;
import kcworks.string.StringValue;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * Job Queue based conversion handling
 */
@RequestMapping("/api/v1/jodc/conversions")
@RestController
public class JODConversionsController {

	private static final String PARAM_TARGET = "target";
	private static final String PARAM_OPEN = "open";
	// private static final String PARAM_USE_CACHE = "cache";
	@Autowired
	protected JODFormatConverter formatConverter;

	@Autowired
	protected IFileProvider fileProvider;

	private ResponseEntity<ApiResponse> createStub(MultipartFile file, Map<String, String> params, boolean useCache){
		ApiResponse response = ApiResponse.initAsFailed(HttpStatus.NOT_FOUND);
		String target = StringValue.from(params.get(PARAM_TARGET)).asString();
		// boolean useCache = StringValue.from(params.get(PARAM_USE_CACHE)).isNotNull();
		//檢查 target
		if(Strings.isNullOrEmpty(target)){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "target not specified");
		}
		String id = formatConverter.convertAsync(file, target, useCache);
		response.success("conversion created");
		response.addData(id);
		return response.toResponseEntity();
	}

	@PostMapping
	public ResponseEntity<ApiResponse> create(@RequestParam MultipartFile file, @RequestParam Map<String, String> params){
		return createStub(file, params, false);
	}

	@PutMapping
	public ResponseEntity<ApiResponse> createOrUpdate(@RequestParam MultipartFile file, @RequestParam Map<String, String> params){
		return createStub(file, params, true);
	}

	@SneakyThrows
	@GetMapping("/{folderName}/{fileName}")
	public ResponseEntity get(@PathVariable String folderName, @PathVariable String fileName, @RequestParam Map<String, String> params){
		// String targetExtension = Files.getFileExtension(fileName);
		String targetMimeType = formatConverter.getMimeType(fileName);
		boolean open = StringValue.from(params.get(PARAM_OPEN)).isNotNull();

		byte[] outputBytes = fileProvider.retrieve(folderName, fileName);
		return ResponseEntity.ok()
				.contentLength(outputBytes.length)
				.contentType(MediaType.parseMediaType(targetMimeType))
				.headers(open
						? SpringHttpHeaders.ofInline(fileName)
						: SpringHttpHeaders.ofAttachment(fileName)
				)
				.body(new InputStreamResource(new ByteArrayInputStream(outputBytes)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> delete(@PathVariable String id){
		ApiResponse response = ApiResponse.initAsFailed(HttpStatus.FORBIDDEN);
		
		return response.toResponseEntity();
	}

	// protected String getMimeType(String fileName){
	// 	Tika tika = new Tika();
	// 	return tika.detect(fileName);
	// }
}
