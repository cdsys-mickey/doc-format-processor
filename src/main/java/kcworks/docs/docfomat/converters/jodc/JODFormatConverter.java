package kcworks.docs.docfomat.converters.jodc;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import kcworks.docs.docfomat.config.DocFormatConverterConfig;
import kcworks.docs.docfomat.provider.IFileProvider;
import kcworks.env.AppContext;
import kcworks.docs.docfomat.config.JODConverterConfig;
import kcworks.docs.docfomat.converters.AbstractDocFormatConverter;
import kcworks.spring.http.SpringHttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.local.JodConverter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class JODFormatConverter extends AbstractDocFormatConverter {
	// private static final IAppConfig config = AppConfig.getConfig();

	private final JODConverterConfig converterConfig;
	private final DocFormatConverterConfig config;
	private final IFileProvider fileProvider;

	@SneakyThrows
	public ResponseEntity convert(MultipartFile sourceFile, String target
			, SpringHttpHeaders.ContentDispositionType contentDispositionType){
		if(sourceFile == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		byte[] sourceBytes = sourceFile.getBytes();
		//容量檢查
		if(converterConfig.getMaxMb() > 0 && sourceBytes.length > DataSize.ofMegabytes(converterConfig.getMaxMb()).toBytes()){
			throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
					"request body size is too large(" + DataSize.ofBytes(sourceBytes.length).toMegabytes() + " MB > "
							+ converterConfig.getMaxMb() + "MB) for converter");
		}

		String sourceFileName = sourceFile.getOriginalFilename();
		String sourceExtension = Files.getFileExtension(sourceFileName);
		//來源檔名必須有副檔名
		if(Strings.isNullOrEmpty(sourceExtension)){
			throw new IllegalArgumentException("source file name \"" + sourceFileName + "\" has not extension");
		}

		DocumentFormat sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(sourceExtension);

		String targetFileName;
		String targetExtension;
		if(target.indexOf('.') > -1){
			targetFileName = target;
			targetExtension = Files.getFileExtension(target);
		}else{
			targetFileName = Files.getNameWithoutExtension(sourceFileName) + "." + target;
			targetExtension = target;
		}

		//副檔名相同, 且 always.convert 為 false, 就不轉換直接輸出
		if(targetExtension.equals(sourceExtension) && !converterConfig.isConvertAnyway()){
			return ResponseEntity.ok()
					.contentLength(sourceFile.getBytes().length)
					.contentType(MediaType.parseMediaType(sourceFile.getContentType()))
					.headers(contentDispositionType == SpringHttpHeaders.ContentDispositionType.ATTACHMENT
							? SpringHttpHeaders.ofAttachment(targetFileName)
							: SpringHttpHeaders.ofInline(targetFileName)
					)
					.body(new InputStreamResource(new ByteArrayInputStream(sourceFile.getBytes())));
		}

		log.info("converting from {} to {}...", sourceFileName, targetFileName);

		DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(targetExtension);
		String targetMimeType = getMimeType(targetFileName);

		try(
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceFile.getBytes());
				){
			JodConverter
					.convert(inputStream)
					.as(sourceFormat)
					.to(outputStream)
					.as(targetFormat)
					.execute();
			byte[] outputBytes = outputStream.toByteArray();
			log.debug("\tconversion is done, {} bytes written.", outputBytes.length);
			
			try(ByteArrayInputStream resultStream = new ByteArrayInputStream(outputBytes)){
				return ResponseEntity.ok()
						.contentLength(outputBytes.length)
						.contentType(MediaType.parseMediaType(targetMimeType))
						.headers(contentDispositionType == SpringHttpHeaders.ContentDispositionType.ATTACHMENT
								? SpringHttpHeaders.ofAttachment(targetFileName)
								: SpringHttpHeaders.ofInline(targetFileName)
						)
						.body(new InputStreamResource(resultStream));
			}

		}
	}

	@SneakyThrows
	public String convertAsync(MultipartFile sourceFile, String target, boolean useCache) {

		if(sourceFile == null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body is empty");
		}

		byte[] sourceBytes = sourceFile.getBytes();
		//容量檢查
		if(converterConfig.getMaxMb() > 0 && sourceBytes.length > DataSize.ofMegabytes(converterConfig.getMaxMb()).toBytes()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request body size is too large(" + sourceBytes.length + " bytes > " + DataSize.ofMegabytes(converterConfig.getMaxMb()).toBytes() + "bytes) for converter");
		}

		String sourceFileName = sourceFile.getOriginalFilename();
		String sourceExtension = Files.getFileExtension(sourceFileName);
		//來源檔名必須有副檔名
		if(Strings.isNullOrEmpty(sourceExtension)){
			throw new IllegalArgumentException("source file name " + sourceFileName + " has not extension");
		}

		DocumentFormat sourceFormat = DefaultDocumentFormatRegistry.getFormatByExtension(sourceExtension);

		//target 可以是副檔名, 或是完整檔名
		String targetExtension = Files.getFileExtension(target);
		String targetFileName;
		if(!Strings.isNullOrEmpty(targetExtension)){
			targetFileName = target;
		}else{
			targetExtension = target;
			targetFileName = Files.getNameWithoutExtension(sourceFileName) + "." + target;
		}

		LocalDateTime now = LocalDateTime.now();
		String folderName = String.valueOf(now.toEpochSecond(ZoneOffset.UTC));
		Path tempFolder = Paths.get(config.getTempFolder());
		Path targetFolderPath = tempFolder.isAbsolute()
				? tempFolder
				: AppContext.getTempDirPath(true, config.getTempFolder());
		targetFolderPath = targetFolderPath.resolve(folderName);
		Path targetFilePath = targetFolderPath.resolve(targetFileName);
		String id = folderName + "/" + targetFileName;

		//副檔名相同, 且 always.convert 為 false, 就不轉換直接輸出
		if(targetExtension.equals(sourceExtension) && !converterConfig.isConvertAnyway()){
			Files.write(sourceFile.getBytes(), targetFilePath.toFile());
			return id;
		}

		log.info("converting from {} to {}...", sourceFileName, targetFileName);

		DocumentFormat targetFormat = DefaultDocumentFormatRegistry.getFormatByExtension(targetExtension);

		try(
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceFile.getBytes());
		){
			JodConverter
					.convert(inputStream)
					.as(sourceFormat)
					.to(outputStream)
					.as(targetFormat)
					.execute();

			byte[] outputBytes = outputStream.toByteArray();
			log.debug("\tconversion is done, {} bytes written", outputBytes.length);

			//將轉換結果寫入暫存目錄
			try(
					ByteArrayInputStream resultStream = new ByteArrayInputStream(outputBytes);
			){
				return fileProvider.store(resultStream, targetFileName, useCache);
			}
		}
	}
}
