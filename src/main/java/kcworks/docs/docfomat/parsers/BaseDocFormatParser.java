package kcworks.docs.docfomat.parsers;

import kcworks.docs.docfomat.DocFormatMeta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.util.unit.DataSize;


public abstract class BaseDocFormatParser implements IDocFormatParser {
	private DocFormatParserSettings settings;

	@Override
	public Reader retrieve(InputStream input, String fileName) throws IOException {
		return retrieve(input, fileName, getActiveSettings());
	}

	@Override
	public Reader retrieve(Path filePath, DocFormatParserSettings settings) throws IOException {
		return retrieve(
				Files.newInputStream(filePath, StandardOpenOption.READ),
				filePath.getFileName().toString(),
				settings);
	}

	@Override
	public Reader retrieve(Path filePath) throws IOException {
		return retrieve(filePath, getActiveSettings());
	}

	@Override
	public DocFormatMeta parse(byte[] body) throws IOException {
		return parse(body, getActiveSettings());
	}

	@Override
	public DocFormatMeta parse(Path filePath) throws IOException {
		return parse(filePath, getActiveSettings());
	}

	@Override
	public DocFormatMeta parse(InputStream input, String fileName) throws IOException {
		return parse(input, fileName, getActiveSettings());
	}

	@Override
	public DocFormatMeta parse(File file) throws IOException {
		return parse(file, getActiveSettings());
	}

	@Override
	public DocFormatParserSettings getSettings() {
		return settings;
	}

	@Override
	public void setSettings(DocFormatParserSettings settings) {
		this.settings = settings;
	}

	private DocFormatParserSettings getActiveSettings(){
		DocFormatParserSettings settings = getSettings();
		if(settings == null){
			settings = new DocFormatParserSettings();
		}
		return settings;
	}

	protected byte[] readInputStream(InputStream inputStream, int maxMegaBytes) throws IOException, IllegalArgumentException{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		int totalBytes = 0;
		long limitedBytes = DataSize.ofMegabytes(maxMegaBytes).toBytes();
		if(limitedBytes > 0){
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
				totalBytes += bytesRead;
				if (totalBytes > limitedBytes) {
					throw new IllegalArgumentException("Input stream size is too large. ( > " + maxMegaBytes + "MB)" );
				}
			}
		}
		return outputStream.toByteArray();
	}
}
