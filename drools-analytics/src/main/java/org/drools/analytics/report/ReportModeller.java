package org.drools.analytics.report;

import java.util.Collection;

import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Gap;
import org.drools.analytics.report.components.MissingNumberPattern;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Toni Rikkola
 */
public class ReportModeller {

	public static String writeXML(AnalyticsResult result) {
		XStream xstream = new XStream();

		xstream.alias("result", AnalyticsResult.class);
		xstream.alias("message", AnalyticsMessage.class);

		xstream.alias("Gap", Gap.class);
		xstream.alias("MissingNumber", MissingNumberPattern.class);

		xstream.alias("Field", org.drools.analytics.components.Field.class);

		xstream.alias("LiteralRestriction", LiteralRestriction.class);

		return "<?xml version=\"1.0\"?>\n" + xstream.toXML(result);
	}

	public static String writePlainText(AnalyticsResult result) {

		StringBuffer str = new StringBuffer();

		for (AnalyticsMessage.Severity severity : AnalyticsMessage.Severity
				.values()) {
			Collection<AnalyticsMessageBase> messages = result
					.getBySeverity(severity);

			str.append("************* ");
			str.append(severity.getTuple());
			str.append(" ");

			str.append(messages.size());
			str.append(" ******************\n");
			for (AnalyticsMessageBase message : messages) {
				str.append(message);
				str.append("\n");
			}
			str.append("\n");
		}

		return str.toString();
	}
}
