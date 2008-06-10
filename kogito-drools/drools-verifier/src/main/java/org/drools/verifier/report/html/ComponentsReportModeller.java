package org.drools.verifier.report.html;

import java.io.File;
import java.io.IOException;

import org.drools.verifier.components.VerifierClass;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.Field;
import org.drools.verifier.dao.VerifierData;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.Severity;

public class ComponentsReportModeller extends ReportModeller {

	public static void writeHTML(String path, VerifierResult result) {
		VerifierData data = result.getVerifierData();

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
		for (VerifierRule rule : data.getAllRules()) {
			writeToFile(ruleFolder + File.separator + rule.getId() + ".htm",
					formPage(UrlFactory.PREVIOUS_FOLDER,
							ComponentsReportVisitor.visitRule(
									UrlFactory.PREVIOUS_FOLDER, rule, data)));
		}

		// ObjectTypes
		String objectTypeFolder = path + UrlFactory.SOURCE_FOLDER
				+ File.separator + UrlFactory.OBJECT_TYPE_FOLDER;
		File objectTypesFolder = new File(objectTypeFolder);
		objectTypesFolder.mkdir();
		for (VerifierClass objectType : data.getAllClasses()) {
			writeToFile(objectTypeFolder + File.separator + objectType.getId()
					+ ".htm", formPage(UrlFactory.PREVIOUS_FOLDER,
					ComponentsReportVisitor.visitObjectType(
							UrlFactory.PREVIOUS_FOLDER, objectType, data)));
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
									UrlFactory.PREVIOUS_FOLDER, field, result)));
		}

		// Verifier messages
		writeMessages(path, result);

		// css files
		String cssFolder = path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.CSS_FOLDER;
		File cssesFolder = new File(cssFolder);
		cssesFolder.mkdir();
		writeToFile(cssFolder + File.separator + UrlFactory.CSS_BASIC,
				ComponentsReportVisitor.getCss(UrlFactory.CSS_BASIC));

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

	private static void writeMessages(String path, VerifierResult result) {
		VerifierData data = result.getVerifierData();

		String errors = VerifierMessagesVisitor
				.visitVerifierMessagesCollection(
						Severity.ERROR.getTuple(),
						result.getBySeverity(Severity.ERROR),
						data);
		String warnings = VerifierMessagesVisitor
				.visitVerifierMessagesCollection(
						Severity.WARNING.getTuple(),
						result.getBySeverity(Severity.WARNING),
						data);
		String notes = VerifierMessagesVisitor
				.visitVerifierMessagesCollection(
						Severity.NOTE.getTuple(), result
								.getBySeverity(Severity.NOTE),
						data);

		writeToFile(path + UrlFactory.SOURCE_FOLDER + File.separator
				+ UrlFactory.HTML_FILE_VERIFIER_MESSAGES, formPage(
				UrlFactory.THIS_FOLDER, errors + warnings + notes));
	}
}
