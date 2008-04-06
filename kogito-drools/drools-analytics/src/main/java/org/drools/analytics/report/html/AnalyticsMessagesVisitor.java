package org.drools.analytics.report.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.AnalyticsRangeCheckMessage;
import org.drools.analytics.report.components.Cause;
import org.mvel.templates.TemplateRuntime;

/**
 * 
 * @author Toni Rikkola
 */
class AnalyticsMessagesVisitor extends ReportVisitor {

	private static String ANALYSIS_MESSAGES_TEMPLATE = "analyticsMessages.htm";
	private static String ANALYSIS_MESSAGE_TEMPLATE = "analyticsMessage.htm";

	public static String NOTES = "Notes";
	public static String WARNINGS = "Warnings";
	public static String ERRORS = "Errors";

	public static String visitAnalyticsMessagesCollection(String title,
			Collection<AnalyticsMessageBase> messages, AnalyticsData data) {
		Map<String, Object> map = new HashMap<String, Object>();
		Collection<String> messageTemplates = new ArrayList<String>();
		String myTemplate = readFile(ANALYSIS_MESSAGES_TEMPLATE);

		for (AnalyticsMessageBase message : messages) {
			messageTemplates.add(visitAnalyticsMessage(message, data));
		}

		map.put("title", title);
		map.put("messages", messageTemplates);

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	public static String visitAnalyticsMessage(AnalyticsMessageBase message,
			AnalyticsData data) {
		if (message instanceof AnalyticsRangeCheckMessage) {
			return visitAnalyticsMessage((AnalyticsRangeCheckMessage) message,
					data);
		} else if (message instanceof AnalyticsMessage) {
			return visitAnalyticsMessage((AnalyticsMessage) message);
		}

		return null;
	}

	public static String visitAnalyticsMessage(
			AnalyticsRangeCheckMessage message, AnalyticsData data) {

		return MissingRangesReportVisitor.visitRangeCheckMessage(
				UrlFactory.THIS_FOLDER, message, data);
	}

	public static String visitAnalyticsMessage(AnalyticsMessage message) {

		Map<String, Object> map = new HashMap<String, Object>();
		Collection<String> causeUrls = new ArrayList<String>();
		String myTemplate = readFile(ANALYSIS_MESSAGE_TEMPLATE);

		// Solve the url's to causes if there is any.
		for (Cause cause : message.getCauses()) {
			causeUrls.add(UrlFactory.getUrl(cause));
		}

		map.put("title", message.getSeverity());
		map.put("reason", message.getFaulty());
		map.put("message", message.getMessage());
		map.put("causes", causeUrls);

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}
}
