package org.drools.analytics.result;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Toni Rikkola
 */
public class Writer {

	public static String write(AnalysisResultNormal result) {
		XStream xstream = new XStream();

		xstream.alias("result", AnalysisResultNormal.class);
		xstream.alias("note", AnalysisNote.class);
		xstream.alias("error", AnalysisError.class);
		xstream.alias("warning", AnalysisWarning.class);

		return "<?xml version=\"1.0\"?>\n" + xstream.toXML(result);
	}

}
