package org.drools.analytics.result;

class UrlFactory {

	public static final String THIS_FOLDER = ".";
	public static final String PREVIOUS_FOLDER = "..";

	public static final String SOURCE_FOLDER = "report";
	public static final String OBJECT_TYPE_FOLDER = "objectTypes";
	public static final String FIELD_FOLDER = "fields";
	public static final String RULE_FOLDER = "rules";
	public static final String PACKAGE_FOLDER = "packages";
	public static final String CSS_FOLDER = "css";

	public static final String CSS_FILE_LIST = "relationsList.css";
	public static final String CSS_FILE_DETAILS = "relationsDetails.css";

	public static final String HTML_FILE_INDEX = "index.htm";
	public static final String HTML_FILE_PACKAGES = "packages.htm";
	public static final String HTML_FILE_GAPS = "missingRanges.htm";

	static String getRuleUrl(String sourceFolder, int ruleId, String ruleName) {
		return "<a href=\"" + sourceFolder + "/" + RULE_FOLDER + "/" + ruleId
				+ ".htm\">" + ruleName + "</a>";
	}
}
