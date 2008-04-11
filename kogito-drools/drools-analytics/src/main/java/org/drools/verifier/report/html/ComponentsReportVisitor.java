package org.drools.verifier.report.html;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.verifier.components.AnalyticsClass;
import org.drools.verifier.components.AnalyticsRule;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.dao.AnalyticsData;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.report.components.RangeCheckCause;
import org.mvel.templates.TemplateRuntime;

class ComponentsReportVisitor extends ReportVisitor {

	public static String getCss(String fileName) {
		return readFile(fileName);
	}

	public static String visitRulePackageCollection(String sourceFolder,
			Collection<RulePackage> packages) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", sourceFolder);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);

		map.put("rulePackages", packages);

		String myTemplate = readFile("packages.htm");

		String result = String.valueOf(TemplateRuntime.eval(myTemplate, map));

		return result;
	}

	public static String visitObjectTypeCollection(String sourceFolder,
			Collection<AnalyticsClass> objectTypes) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", sourceFolder);
		map.put("objectTypeFolder", sourceFolder + "/"
				+ UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);
		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("objectTypes.htm");

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	public static String visitRule(String sourceFolder, AnalyticsRule rule,
			AnalyticsData data) {
		Collection<AnalyticsClass> objectTypes = data.getClassesByRuleName(rule
				.getRuleName());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", sourceFolder);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);

		map.put("rule", rule);
		map.put("objectTypes", objectTypes);

		String myTemplate = readFile("rule.htm");

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	public static String visitObjectType(String sourceFolder,
			AnalyticsClass objectType, AnalyticsData data) {
		Collection<AnalyticsRule> rules = data.getRulesByClassId(objectType
				.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", sourceFolder);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);

		map.put("objectType", objectType);
		map.put("rules", rules);

		String myTemplate = readFile("objectType.htm");

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}

	public static String visitField(String sourceFolder, Field field,
			AnalyticsResult result) {
		AnalyticsData data = result.getAnalyticsData();
		AnalyticsClass objectType = data.getClassById(field.getClassId());
		Collection<AnalyticsRule> rules = data.getRulesByFieldId(field.getId());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sourceFolder", sourceFolder);
		map.put("ruleFolder", UrlFactory.RULE_FOLDER);
		map.put("objectTypeFolder", UrlFactory.OBJECT_TYPE_FOLDER);
		map.put("fieldFolder", UrlFactory.FIELD_FOLDER);

		map.put("field", field);
		map.put("objectType", objectType);
		map.put("rules", rules);

		if (field.getFieldType() == Field.FieldType.DOUBLE
				|| field.getFieldType() == Field.FieldType.DATE
				|| field.getFieldType() == Field.FieldType.INT) {
			Collection<RangeCheckCause> causes = result
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

		return String.valueOf(TemplateRuntime.eval(myTemplate, map));
	}
}
