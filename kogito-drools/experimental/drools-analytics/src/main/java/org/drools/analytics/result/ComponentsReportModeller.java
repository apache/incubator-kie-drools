package org.drools.analytics.result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;

public class ComponentsReportModeller {

	public static void writeHTML(String path) {
		AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();

		// Source folder
		File sourceFolder = new File(path
				+ ComponentsReportVisitor.SOURCE_FOLDER);
		sourceFolder.mkdir();

		// Base files
		// index.htm
		writeToFile(path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.HTML_FILE_INDEX,
				ComponentsReportVisitor.visitObjectTypeCollection(data
						.getAllClasses()));
		// packages.htm
		writeToFile(path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.HTML_FILE_PACKAGES,
				ComponentsReportVisitor.visitRulePackageCollection(data
						.getAllRulePackages()));

		// rules
		String ruleFolder = path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.RULE_FOLDER;
		File rulesFolder = new File(ruleFolder);
		rulesFolder.mkdir();
		for (AnalyticsRule rule : data.getAllRules()) {
			writeToFile(ruleFolder + File.separator + rule.getId() + ".htm",
					ComponentsReportVisitor.visitRule(rule));
		}

		// ObjectTypes
		String objectTypeFolder = path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.OBJECT_TYPE_FOLDER;
		File objectTypesFolder = new File(objectTypeFolder);
		objectTypesFolder.mkdir();
		for (AnalyticsClass objectType : data.getAllClasses()) {
			writeToFile(objectTypeFolder + File.separator + objectType.getId()
					+ ".htm", ComponentsReportVisitor
					.visitObjectType(objectType));
		}

		// Fields
		String fieldFolder = path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.FIELD_FOLDER;
		File fieldsFolder = new File(fieldFolder);
		fieldsFolder.mkdir();
		for (Field field : data.getAllFields()) {
			writeToFile(fieldFolder + File.separator + field.getId() + ".htm",
					ComponentsReportVisitor.visitField(field));
		}

		// css files
		String cssFolder = path + ComponentsReportVisitor.SOURCE_FOLDER
				+ File.separator + ComponentsReportVisitor.CSS_FOLDER;
		File cssesFolder = new File(cssFolder);
		cssesFolder.mkdir();
		writeToFile(cssFolder + File.separator
				+ ComponentsReportVisitor.CSS_FILE_DETAILS,
				ComponentsReportVisitor
						.getCss(ComponentsReportVisitor.CSS_FILE_DETAILS));
		writeToFile(cssFolder + File.separator
				+ ComponentsReportVisitor.CSS_FILE_LIST,
				ComponentsReportVisitor
						.getCss(ComponentsReportVisitor.CSS_FILE_LIST));
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
