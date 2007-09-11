package org.drools.analytics.result;

import org.drools.analytics.components.LiteralRestriction;

import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.drools.analytics.Analyzer;

/**
 * 
 * @author Toni Rikkola
 */
public class ReportModeller {
    
        private static String cssFile = "basic.css";

	public static String writeXML(AnalysisResultNormal result) {
		XStream xstream = new XStream();

		xstream.alias("result", AnalysisResultNormal.class);
		xstream.alias("note", AnalysisNote.class);
		xstream.alias("error", AnalysisError.class);
		xstream.alias("warning", AnalysisWarning.class);

		xstream.alias("Gap", Gap.class);

		xstream.alias("Field", org.drools.analytics.components.Field.class);

		xstream.alias("LiteralRestriction", LiteralRestriction.class);

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
        
        public static String writeHTML(AnalysisResultNormal result) {
                StringBuffer str = new StringBuffer("");
                str.append("<html>\n");
                str.append("<head>\n");
                str.append("<title>\n");
                str.append("Analysis Result\n");
                str.append("</title>\n");
                //str.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"basic.css\" title=\"default\">\n");
                
                str.append("<style type=\"text/css\">\n");
                str.append("<!--\n");
                BufferedReader reader = new BufferedReader(new InputStreamReader(Analyzer.class.getResourceAsStream(cssFile)));
                try{
                    String cssLine = null;
                    while((cssLine = reader.readLine()) != null)
                    {
                        str.append(cssLine);
                        str.append("\n");
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                str.append("-->\n");
                str.append("</style>\n");
                
                str.append("</head>\n");
                str.append("<body>\n\n");
                
                str.append("<br>\n");
                str.append("<h1>\n");
                str.append("Analysis results");
                str.append("</h1>\n");
                str.append("<br>\n");
                
                if(result.getErrors().size() > 0)
                {
                    str.append("<table class=\"errors\">\n");
                    str.append("<tr>\n");
                    str.append("<th>\n");
                    str.append("ERRORS (");
                    str.append(result.getErrors().size());
                    str.append(")\n");
                    str.append("</th>\n");
                    str.append("</tr>\n");
                    for (AnalysisError error : result.getErrors()) {
                        str.append("<tr>\n");
                        str.append("<td>\n");
                        str.append(error);
                        str.append("</td>\n");
                        str.append("</tr>\n");
                    }
                    str.append("</table>\n");
                    
                    str.append("<br>\n");
                    str.append("<br>\n");
                }
                
                if(result.getWarnings().size() > 0)
                {
                    str.append("<table class=\"warnings\">\n");
                    str.append("<tr>\n");
                    str.append("<th>\n");
                    str.append("WARNINGS (");
                    str.append(result.getWarnings().size());
                    str.append(")\n");
                    str.append("</th>\n");
                    str.append("</tr>\n");
                    for (AnalysisWarning warning : result.getWarnings()) {
                        str.append("<tr>\n");
                        str.append("<td>\n");

                        str.append("Warning id = ");
                        str.append(warning.getId());
                        str.append(":<BR>\n");

                        if (warning.getRuleName() != null) {
                            str.append("in rule ");
                            str.append(warning.getRuleName());
                            str.append(": ");
                        }

                        str.append(warning.getMessage());
                        str.append("<BR>\n");
                        str.append("&nbsp;&nbsp; Causes are [<BR>\n");

                        for (Cause cause : warning.getCauses()) {
                                str.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                                str.append(cause);
                                str.append("<BR>\n");
                        }
                        str.append("&nbsp;&nbsp; ]\n");

                        str.append("</td>\n");
                        str.append("</tr>\n");
                    }
                    str.append("</table>\n");

                    str.append("<br>\n");
                    str.append("<br>\n");
                }
                
                if(result.getNotes().size() > 0)
                {
                    str.append("<table class=\"notes\">\n");
                    str.append("<tr>\n");
                    str.append("<th>\n");
                    str.append("NOTES (");
                    str.append(result.getNotes().size());
                    str.append(")\n");
                    str.append("</th>\n");
                    str.append("</tr>\n");
                    for (AnalysisNote note : result.getNotes()) {
                        str.append("<tr>\n");
                        str.append("<td>\n");
                        str.append(note);
                        str.append("</td>\n");
                        str.append("</tr>\n");
                    }
                    str.append("</table>\n");
                }
                
                str.append("</body>\n");
                str.append("</html>");
                
                return str.toString();
	}

}
