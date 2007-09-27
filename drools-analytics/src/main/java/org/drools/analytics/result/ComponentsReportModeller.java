package org.drools.analytics.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;

public class ComponentsReportModeller {

	public static void writeHTML(String path, AnalysisResult result) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		// Source folder
		File sourceFolder = new File(path + UrlFactory.SOURCE_FOLDER);
		sourceFolder.mkdir();

		// Base files
		// index.htm
		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_INDEX, ComponentsReportVisitor
				.visitObjectTypeCollection(data.getAllClasses()));
		// packages.htm
		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_PACKAGES, ComponentsReportVisitor
				.visitRulePackageCollection(data.getAllRulePackages()));

		// rules
		String ruleFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.RULE_FOLDER;
		File rulesFolder = new File(ruleFolder);
		rulesFolder.mkdir();
		for (AnalyticsRule rule : data.getAllRules()) {
			writeToFile(ruleFolder + File.separator + rule.getId() + ".htm",
					ComponentsReportVisitor.visitRule(rule));
		}

		// ObjectTypes
		String objectTypeFolder = path + UrlFactory.SOURCE_FOLDER
				+ File.separator + UrlFactory.OBJECT_TYPE_FOLDER;
		File objectTypesFolder = new File(objectTypeFolder);
		objectTypesFolder.mkdir();
		for (AnalyticsClass objectType : data.getAllClasses()) {
			writeToFile(objectTypeFolder + File.separator + objectType.getId()
					+ ".htm", ComponentsReportVisitor
					.visitObjectType(objectType));
		}

		// Fields
		String fieldFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.FIELD_FOLDER;
		File fieldsFolder = new File(fieldFolder);
		fieldsFolder.mkdir();
		for (Field field : data.getAllFields()) {
			writeToFile(fieldFolder + File.separator + field.getId() + ".htm",
					ComponentsReportVisitor.visitField(field));
		}

		// Gap warnings
		Collection<RangeCheckCause> rangeCheckCauses = data
				.getRangeCheckCauses();
		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_GAPS, MissingRangesReportVisitor
				.visitRangeCheckCauseCollection(rangeCheckCauses));

		// css files
		String cssFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.CSS_FOLDER;
		File cssesFolder = new File(cssFolder);
		cssesFolder.mkdir();
		writeToFile(cssFolder + File.separator + UrlFactory.CSS_FILE_DETAILS,
				ComponentsReportVisitor.getCss(UrlFactory.CSS_FILE_DETAILS));
		writeToFile(cssFolder + File.separator + UrlFactory.CSS_FILE_LIST,
				ComponentsReportVisitor.getCss(UrlFactory.CSS_FILE_LIST));
	}

	private static void writeToFile(String fileName, String text) {
		try {
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			out.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
