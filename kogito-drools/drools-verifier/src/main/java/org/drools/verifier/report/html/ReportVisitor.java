package org.drools.verifier.report.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.mvel.templates.TemplateRuntime;


abstract class ReportVisitor {

	protected static String processHeader(String folder) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", folder);

		map.put("objectTypesFile", UrlFactory.HTML_FILE_INDEX);
		map.put("packagesFile", UrlFactory.HTML_FILE_PACKAGES);
		map.put("messagesFile", UrlFactory.HTML_FILE_VERIFIER_MESSAGES);

		String myTemplate = readFile("header.htm");

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	protected static String readFile(String fileName) {
		StringBuffer str = new StringBuffer("");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ReportVisitor.class.getResourceAsStream(fileName)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
				str.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("File " + fileName + " was not found.");
			e.printStackTrace();
		}
		return str.toString();
	}

	protected static String createStyleTag(String path) {
		StringBuffer str = new StringBuffer("");

		str.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
		str.append(path);
		str.append("\" />");

		return str.toString();
	}
}
