package kcworks.docs.docfomat.provider;

import java.io.InputStream;

public interface IFileProvider {

	/**
	 * stores file and return a new key
	 * @param input
	 * @return
	 */
	String store(InputStream input, String targetFileName, boolean useCache);

	byte[] retrieve(String folderName, String fileName);

	void housekeeping();
}
