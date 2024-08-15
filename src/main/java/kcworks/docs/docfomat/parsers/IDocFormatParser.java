package kcworks.docs.docfomat.parsers;

import kcworks.docs.docfomat.DocFormatMeta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

public interface IDocFormatParser {
	Reader retrieve(InputStream stream, String fileName, DocFormatParserSettings settings) throws IOException;
	Reader retrieve(InputStream stream, String fileName) throws IOException;

	Reader retrieve(Path filePath, DocFormatParserSettings settings) throws IOException;
	Reader retrieve(Path filePath) throws IOException;

	DocFormatMeta parse(InputStream stream, String fileName, DocFormatParserSettings settings) throws IOException;
	DocFormatMeta parse(InputStream stream, String fileName) throws IOException;

	DocFormatMeta parse(Path filePath, DocFormatParserSettings settings) throws IOException;
	DocFormatMeta parse(Path filePath) throws IOException;

	DocFormatMeta parse(File file, DocFormatParserSettings settings) throws IOException;
	DocFormatMeta parse(File file) throws IOException;

	DocFormatMeta parse(byte[] body, DocFormatParserSettings settings) throws IOException;
	DocFormatMeta parse(byte[] body) throws IOException;

	DocFormatParserSettings getSettings();
	
	void setSettings(DocFormatParserSettings settings);

	void reload();
}
