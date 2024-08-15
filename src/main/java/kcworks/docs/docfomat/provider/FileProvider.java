package kcworks.docs.docfomat.provider;

import com.google.common.base.Throwables;
import kcworks.docs.docfomat.config.DocFormatConverterConfig;
import kcworks.env.AppContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@RequiredArgsConstructor
public class FileProvider implements IFileProvider{
	private final DocFormatConverterConfig converterConfig;
	private Path tempFolderPath;

	@SneakyThrows
	@PostConstruct
	public void init(){
		Path tempFolder = Paths.get(converterConfig.getTempFolder());
		this.tempFolderPath = tempFolder.isAbsolute()
				? tempFolder
				: AppContext.getTempDirPath(true, converterConfig.getTempFolder());
		log.info("using temp dir: {}", this.tempFolderPath.toAbsolutePath());
	}

	@SneakyThrows
	@Override
	public String store(InputStream input, String targetFileName, boolean useCache) {
		LocalDateTime now = LocalDateTime.now();
		String folderName = String.valueOf(now.toEpochSecond(ZoneOffset.UTC));
		Path folderPath = tempFolderPath.resolve(folderName);
		folderPath = Files.createDirectories(folderPath);
		Path filePath = folderPath.resolve(targetFileName);
		String id = folderName + "/" + targetFileName;
		long written = Files.copy(input, filePath);
		log.debug("{} byte(s) written to {}", written, filePath.toAbsolutePath());
		return id;
	}

	@SneakyThrows
	@Override
	public byte[] retrieve(String folderName, String fileName) {
		Path folder = this.tempFolderPath.resolve(folderName);
		Path filePath = folder.resolve(fileName);
		if(!Files.isRegularFile(filePath)){
			log.error("{} not found", filePath.toAbsolutePath());
			throw new IllegalAccessException("file not found");
		}else{
			log.debug("{} retrieved", filePath.toAbsolutePath());
			return Files.readAllBytes(filePath);
		}
	}

	@SneakyThrows
	@Override
	public void housekeeping() {
		//刪除昨天以前的目錄
		LocalDateTime today = LocalDate.now().atStartOfDay();
		long timeMillis = today.toEpochSecond(ZoneOffset.UTC);
		Path tempFolderPath = AppContext.getTempDirPath(false, converterConfig.getTempFolder());
		Files.list(tempFolderPath).filter(p -> Files.isDirectory(p) && Long.parseLong(p.getFileName().toString()) < timeMillis)
				.forEach(p -> {
					try {
						// MoreFiles.deleteRecursively(p);
						// Guava 會引發 InsecureRecursiveDeleteException ?
						FileSystemUtils.deleteRecursively(p);
						log.debug("{} deleted recursively", p);
					} catch (IOException e) {
						log.error("failed to housekeeping {}: {}", p, e.getMessage());
						log.error(Throwables.getStackTraceAsString(e));
					}
				});

	}
}
