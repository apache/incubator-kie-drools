package org.drools.verifier.report;

import java.util.Collection;

import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

import com.thoughtworks.xstream.XStream;

/**
 *
 * @author Toni Rikkola
 */
public class ReportModeller {

	public static String writeXML(VerifierReport result) {
		XStream xstream = new XStream();

		xstream.alias("result", VerifierReport.class);
		xstream.alias("message", VerifierMessage.class);

		xstream.alias("Gap", Gap.class);
		xstream.alias("MissingNumber", MissingNumberPattern.class);

		xstream.alias("Field", org.drools.verifier.components.Field.class);

		xstream.alias("LiteralRestriction", LiteralRestriction.class);

		return "<?xml version=\"1.0\"?>\n" + xstream.toXML(result);
	}

	public static String writePlainText(VerifierReport result) {

		StringBuffer str = new StringBuffer();

		for (Severity severity : Severity
				.values()) {
			Collection<VerifierMessageBase> messages = result
					.getBySeverity(severity);

			str.append("************* ");
			str.append(severity.getTuple());
			str.append(" ");

			str.append(messages.size());
			str.append(" ******************\n");
			for (VerifierMessageBase message : messages) {
				str.append(message);
				str.append("\n");
			}
			str.append("\n");
		}

		return str.toString();
	}
}
