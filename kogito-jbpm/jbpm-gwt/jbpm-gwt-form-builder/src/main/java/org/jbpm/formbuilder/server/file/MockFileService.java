package org.jbpm.formbuilder.server.file;

import java.util.ArrayList;
import java.util.List;

public class MockFileService implements FileService {

	@Override
	public String storeFile(String packageName, String fileName, byte[] content)
			throws FileException {
		return fileName;
	}

	@Override
	public void deleteFile(String packageName, String fileName)
			throws FileException {
	}

	@Override
	public List<String> loadFilesByType(String packageName, String fileType)
			throws FileException {
		return new ArrayList<String>();
	}

	@Override
	public byte[] loadFile(String packageName, String fileName)
			throws FileException {
		return new byte[2048];
	}

}
