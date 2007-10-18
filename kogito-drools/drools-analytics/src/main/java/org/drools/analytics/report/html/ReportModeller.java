package org.drools.analytics.report.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.mvel.TemplateInterpreter;

public abstract class ReportModeller {

	protected static String formPage(String sourceFolder, String content) {
		Map<String, Object> map = new HashMap<String, Object>();
		String myTemplate = AnalyticsMessagesVisitor.readFile("frame.htm");

		map.put("cssStyle", ReportVisitor.createStyleTag(sourceFolder + "/"
				+ UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_BASIC));
		map.put("sourceFolder", sourceFolder);
		map.put("header", ReportVisitor.processHeader(sourceFolder));
		map.put("content", content);

		return TemplateInterpreter.evalToString(myTemplate, map);
	}

	public static void copyFile(String destination, String filename)
			throws IOException {

		File source = new File(ComponentsReportModeller.class.getResource(
				filename).getFile());
		File dest = new File(destination + File.separator + filename);

		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			in.close();
			out.close();
		}

	}

	protected static void writeToFile(String fileName, String text) {
		try {
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			out.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
