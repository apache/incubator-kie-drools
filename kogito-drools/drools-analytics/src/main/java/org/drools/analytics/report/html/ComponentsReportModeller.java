package org.drools.analytics.report.html;

import java.io.File;
import java.io.IOException;

import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;

public class ComponentsReportModeller extends ReportModeller {

	public static void writeHTML(String path) {
		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();

		// Source folder
		File sourceFolder = new File(path + UrlFactory.SOURCE_FOLDER);
		sourceFolder.mkdir();

		// Base files
		// index.htm
		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_INDEX, formPage(UrlFactory.THIS_FOLDER,
				ComponentsReportVisitor.visitObjectTypeCollection(
						UrlFactory.THIS_FOLDER, data.getAllClasses())));

		// packages.htm
		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_PACKAGES, formPage(
				UrlFactory.THIS_FOLDER, ComponentsReportVisitor
						.visitRulePackageCollection(UrlFactory.THIS_FOLDER,
								data.getAllRulePackages())));

		// Rules
		String ruleFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.RULE_FOLDER;
		File rulesFolder = new File(ruleFolder);
		rulesFolder.mkdir();
		for (AnalyticsRule rule : data.getAllRules()) {
			writeToFile(ruleFolder + File.separator + rule.getId() + ".htm",
					formPage(UrlFactory.PREVIOUS_FOLDER,
							ComponentsReportVisitor.visitRule(
									UrlFactory.PREVIOUS_FOLDER, rule)));
		}

		// ObjectTypes
		String objectTypeFolder = path + UrlFactory.SOURCE_FOLDER
				+ File.separator + UrlFactory.OBJECT_TYPE_FOLDER;
		File objectTypesFolder = new File(objectTypeFolder);
		objectTypesFolder.mkdir();
		for (AnalyticsClass objectType : data.getAllClasses()) {
			writeToFile(objectTypeFolder + File.separator + objectType.getId()
					+ ".htm", formPage(UrlFactory.PREVIOUS_FOLDER,
					ComponentsReportVisitor.visitObjectType(
							UrlFactory.PREVIOUS_FOLDER, objectType)));
		}

		// Fields
		String fieldFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.FIELD_FOLDER;
		File fieldsFolder = new File(fieldFolder);
		fieldsFolder.mkdir();
		for (Field field : data.getAllFields()) {
			writeToFile(fieldFolder + File.separator + field.getId() + ".htm",
					formPage(UrlFactory.PREVIOUS_FOLDER,
							ComponentsReportVisitor.visitField(
									UrlFactory.PREVIOUS_FOLDER, field)));
		}

		// Analytics messages
		writeMessages(path);

		// css files
		String cssFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.CSS_FOLDER;
		File cssesFolder = new File(cssFolder);
		cssesFolder.mkdir();
		writeToFile(cssFolder + File.separator + UrlFactory.CSS_FILE_DETAILS,
				ComponentsReportVisitor.getCss(UrlFactory.CSS_FILE_DETAILS));
		writeToFile(cssFolder + File.separator + UrlFactory.CSS_FILE_LIST,
				ComponentsReportVisitor.getCss(UrlFactory.CSS_FILE_LIST));

		// Image files
		String imagesFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.IMAGES_FOLDER;

		File imgsFolder = new File(imagesFolder);
		imgsFolder.mkdir();

		try {
			copyFile(imagesFolder, "hdrlogo_drools50px.gif");
			copyFile(imagesFolder, "jbossrules_hdrbkg_blue.gif");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeMessages(String path) {
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		String errors = AnalyticsMessagesVisitor
				.visitAnalyticsMessagesCollection(
						AnalyticsMessage.Severity.ERROR.getTuple(), result
								.getBySeverity(AnalyticsMessage.Severity.ERROR));
		String warnings = AnalyticsMessagesVisitor
				.visitAnalyticsMessagesCollection(
						AnalyticsMessage.Severity.WARNING.getTuple(),
						result.getBySeverity(AnalyticsMessage.Severity.WARNING));
		String notes = AnalyticsMessagesVisitor
				.visitAnalyticsMessagesCollection(
						AnalyticsMessage.Severity.NOTE.getTuple(), result
								.getBySeverity(AnalyticsMessage.Severity.NOTE));

		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_ANALYTICS_MESSAGES, formPage(
				UrlFactory.THIS_FOLDER, errors + warnings + notes));
	}
}
