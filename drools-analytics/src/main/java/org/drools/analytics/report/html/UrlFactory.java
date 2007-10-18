package org.drools.analytics.report.html;

import org.drools.analytics.components.AnalyticsRule;

/**
 * 
 * @author Toni Rikkola
 */
class UrlFactory {

	public static final String THIS_FOLDER = ".";
	public static final String PREVIOUS_FOLDER = "..";

	public static final String SOURCE_FOLDER = "report";
	public static final String OBJECT_TYPE_FOLDER = "objectTypes";
	public static final String FIELD_FOLDER = "fields";
	public static final String RULE_FOLDER = "rules";
	public static final String PACKAGE_FOLDER = "packages";
	public static final String CSS_FOLDER = "css";

	public static final String CSS_BASIC = "basic.css";

	public static final String IMAGES_FOLDER = "images";

	public static final String HTML_FILE_INDEX = "index.htm";
	public static final String HTML_FILE_PACKAGES = "packages.htm";
	public static final String HTML_FILE_ANALYTICS_MESSAGES = "analyticsMessages.htm";

	/**
	 * Finds a link to object if one exists.
	 * 
	 * @param o
	 *            Object that might have a page that can be linked.
	 * @return Link to objects page or the toString() text if no link could not
	 *         be created.
	 */
	public static String getUrl(Object o) {
		if (o instanceof AnalyticsRule) {
			AnalyticsRule rule = (AnalyticsRule) o;
			return getRuleUrl(UrlFactory.RULE_FOLDER, rule.getId(), rule
					.getRuleName());
		}

		return o.toString();
	}

	static String getRuleUrl(String sourceFolder, int ruleId, String ruleName) {
		return "<a href=\"" + sourceFolder + "/" + RULE_FOLDER + "/" + ruleId
				+ ".htm\">" + ruleName + "</a>";
	}

}
