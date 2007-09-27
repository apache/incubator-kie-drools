package org.drools.analytics.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.components.RulePackage;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.mvel.TemplateInterpreter;

public class ComponentsReportVisitor extends ReportVisitor {

	public static String getCss(String fileName) {
		return readFile(fileName);
	}

	public static String visitRulePackageCollection(
			Collection<RulePackage> packages) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.THIS_FOLDER));
		map.put("sourceFolder", UrlFactory.THIS_FOLDER);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("packageFolder", UrlFactory.PACKAGE_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.CSS_FOLDER + "/"
				+ UrlFactory.CSS_FILE_LIST));

		map.put("rulePackages", packages);

		String myTemplate = readFile("packages.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitObjectTypeCollection(
			Collection<AnalyticsClass> objectTypes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.THIS_FOLDER));
		map.put("sourceFolder", UrlFactory.THIS_FOLDER);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.CSS_FOLDER + "/"
				+ UrlFactory.CSS_FILE_LIST));

		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("objectTypes.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitRule(AnalyticsRule rule) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		Collection<AnalyticsClass> objectTypes = data.getClassesByRuleName(rule
				.getRuleName());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.PREVIOUS_FOLDER));
		map.put("sourceFolder", UrlFactory.PREVIOUS_FOLDER);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.PREVIOUS_FOLDER + "/"
				+ UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_FILE_DETAILS));

		map.put("rule", rule);
		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("rule.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitObjectType(AnalyticsClass objectType) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		Collection<AnalyticsRule> rules = data.getRulesByClassId(objectType
				.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.PREVIOUS_FOLDER));
		map.put("sourceFolder", UrlFactory.PREVIOUS_FOLDER);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.PREVIOUS_FOLDER + "/"
				+ UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_FILE_DETAILS));

		map.put("objectType", objectType);
		map.put("rules", rules);

		String myTemplate = readFile("objectType.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}

	public static String visitField(Field field) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		AnalyticsClass objectType = data.getClassById(field.getClassId());
		Collection<AnalyticsRule> rules = data.getRulesByFieldId(field.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header", processHeader(UrlFactory.PREVIOUS_FOLDER));
		map.put("sourceFolder", UrlFactory.PREVIOUS_FOLDER);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);
		map.put("cssStyle", createStyleTag(UrlFactory.PREVIOUS_FOLDER + "/"
				+ UrlFactory.CSS_FOLDER + "/" + UrlFactory.CSS_FILE_DETAILS));

		map.put("field", field);
		map.put("objectType", objectType);
		map.put("rules", rules);

		if (field.getFieldType() == Field.FieldType.DOUBLE
				|| field.getFieldType() == Field.FieldType.DATE
				|| field.getFieldType() == Field.FieldType.INT) {
			Collection<RangeCheckCause> causes = data
					.getRangeCheckCausesByFieldId(field.getId());
			Collection<Restriction> restrictions = data
					.getRestrictionsByFieldId(field.getId());
			map.put("ranges", "Ranges:"
					+ MissingRangesReportVisitor.visitRanges(
							UrlFactory.PREVIOUS_FOLDER, restrictions, causes));
		} else {
			map.put("ranges", "");
		}

		String myTemplate = readFile("field.htm");

		String result = TemplateInterpreter.evalToString(myTemplate, map);

		return result;
	}
}
