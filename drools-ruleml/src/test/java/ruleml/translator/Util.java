package ruleml.translator;

import java.io.BufferedReader;
import java.io.FileReader;

public class Util {
	
	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}
