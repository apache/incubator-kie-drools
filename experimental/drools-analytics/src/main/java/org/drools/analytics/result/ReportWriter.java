package org.drools.analytics.result;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Toni Rikkola
 */
public class ReportWriter {

	public static String writeXML(AnalysisResultNormal result) {
		XStream xstream = new XStream();

		xstream.alias("result", AnalysisResultNormal.class);
		xstream.alias("note", AnalysisNote.class);
		xstream.alias("error", AnalysisError.class);
		xstream.alias("warning", AnalysisWarning.class);

		return "<?xml version=\"1.0\"?>\n" + xstream.toXML(result);
	}

	public static String writePlainText(AnalysisResultNormal result) {

		StringBuffer str = new StringBuffer();

		str.append("************* ERRORS ");
		str.append(result.getErrors().size());
		str.append(" ******************\n");
		for (AnalysisError error : result.getErrors()) {
			str.append(error);
			str.append("\n");
		}
		str.append("\n");

		str.append("************* WARNINGS ");
		str.append(result.getWarnings().size());
		str.append(" ******************\n");
		for (AnalysisWarning warning : result.getWarnings()) {
			str.append(warning);
			str.append("\n");
		}
		str.append("\n");

		str.append("************* NOTES ");
		str.append(result.getNotes().size());
		str.append(" ******************\n");
		for (AnalysisNote note : result.getNotes()) {
			str.append(note);
			str.append("\n");
		}

		return str.toString();
	}

}
