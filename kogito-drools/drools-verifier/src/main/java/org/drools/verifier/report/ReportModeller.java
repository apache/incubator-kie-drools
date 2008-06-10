package org.drools.verifier.report;

import java.util.Collection;

import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.report.components.AnalyticsMessage;
import org.drools.verifier.report.components.AnalyticsMessageBase;
import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.Severity;

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

		xstream.alias("Field", org.drools.verifier.components.Field.class);

		xstream.alias("LiteralRestriction", LiteralRestriction.class);

		return "<?xml version=\"1.0\"?>\n" + xstream.toXML(result);
	}

	public static String writePlainText(AnalyticsResult result) {

		StringBuffer str = new StringBuffer();

		for (Severity severity : Severity
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
