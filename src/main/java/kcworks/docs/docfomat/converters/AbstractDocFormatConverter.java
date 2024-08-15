package kcworks.docs.docfomat.converters;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractDocFormatConverter implements IFormatConverter{
	public String getMimeType(String fileName, InputStream inputStream) throws IOException {
		Tika tika = new Tika();
		if(inputStream == null){
			return tika.detect(fileName);
		}else{
			return tika.detect(inputStream, fileName);
		}
	}

	public String getMimeType(String fileName) throws IOException {
		return getMimeType(fileName, null);
	}
}
