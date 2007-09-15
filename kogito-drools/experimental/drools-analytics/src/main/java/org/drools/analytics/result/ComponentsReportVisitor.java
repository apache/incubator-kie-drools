package org.drools.analytics.result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.RulePackage;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;
import org.mvel.TemplateInterpreter;

public class ComponentsReportVisitor {

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

	private static String readFile(String fileName) {
		StringBuffer str = new StringBuffer("");
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(ComponentsReportVisitor.class
							.getResourceAsStream(fileName)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line);
				str.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	public static String getCss(String fileName) {
		return readFile(fileName);
	}

	private static String createStyleTag(String path) {
		StringBuffer str = new StringBuffer("");

		str.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
		str.append(path);
		str.append("\" />");

		return str.toString();
	}

	public static String visitRulePackageCollection(
			Collection<RulePackage> packages) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(THIS_FOLDER));
		map.put("sourceFolder", THIS_FOLDER);
		map.put("ruleFolder", RULE_FOLDER);
		map.put("packageFolder", PACKAGE_FOLDER);
		map.put("cssStyle", createStyleTag(CSS_FOLDER + "/" + CSS_FILE_LIST));

		map.put("rulePackages", packages);

		String myTemplate = readFile("packages.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitObjectTypeCollection(
			Collection<AnalyticsClass> objectTypes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(THIS_FOLDER));
		map.put("sourceFolder", THIS_FOLDER);
		map.put("objectTypeFolder", OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(CSS_FOLDER + "/" + CSS_FILE_LIST));

		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("objectTypes.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitRule(AnalyticsRule rule) {
		AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();
		Collection<AnalyticsClass> objectTypes = data.getClassesByRuleName(rule
				.getRuleName());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(PREVIOUS_FOLDER));
		map.put("sourceFolder", PREVIOUS_FOLDER);
		map.put("objectTypeFolder", OBJECT_TYPE_FOLDER);
		map.put("cssStyle", createStyleTag(PREVIOUS_FOLDER + "/" + CSS_FOLDER
				+ "/" + CSS_FILE_DETAILS));

		map.put("rule", rule);
		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("rule.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitObjectType(AnalyticsClass objectType) {
		AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();
		Collection<AnalyticsRule> rules = data.getRulesByClassId(objectType
				.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(PREVIOUS_FOLDER));
		map.put("sourceFolder", PREVIOUS_FOLDER);
		map.put("ruleFolder", RULE_FOLDER);
		map.put("fieldFolder", FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(PREVIOUS_FOLDER + "/" + CSS_FOLDER
				+ "/" + CSS_FILE_DETAILS));

		map.put("objectType", objectType);
		map.put("rules", rules);

		String myTemplate = readFile("objectType.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitField(Field field) {
		AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();
		AnalyticsClass objectType = data.getClassById(field.getClassId());
		Collection<AnalyticsRule> rules = data.getRulesByFieldId(field.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(PREVIOUS_FOLDER));
		map.put("sourceFolder", PREVIOUS_FOLDER);
		map.put("ruleFolder", RULE_FOLDER);
		map.put("objectTypeFolder", OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(PREVIOUS_FOLDER + "/" + CSS_FOLDER
				+ "/" + CSS_FILE_DETAILS));

		map.put("field", field);
		map.put("objectType", objectType);
		map.put("rules", rules);

		String myTemplate = readFile("field.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	private static String processHeader(String folder) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", folder);

		map.put("objectTypesFile", HTML_FILE_INDEX);
		map.put("packagesFile", HTML_FILE_PACKAGES);

		String myTemplate = readFile("header.htm");

		return TemplateInterpreter.evalToString(myTemplate, map);
	}
}
