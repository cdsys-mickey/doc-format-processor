package kcworks.docs.docfomat.converters;

import kcworks.spring.http.SpringHttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFormatConverter {
	String getMimeType(String fileName) throws IOException;

	ResponseEntity convert(MultipartFile sourceFile, String target
			, SpringHttpHeaders.ContentDispositionType contentDispositionType);

	String convertAsync(MultipartFile sourceFile, String target, boolean useCache);
}
