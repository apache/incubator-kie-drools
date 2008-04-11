package org.drools.verifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.drools.compiler.DrlParser;
import org.drools.compiler.Dialect.AnalysisResult;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.Analyzer;
import org.drools.verifier.components.AnalyticsClass;
import org.drools.verifier.components.AnalyticsRule;
import org.drools.verifier.components.Field;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.report.components.AnalyticsMessage;
import org.drools.verifier.report.components.AnalyticsMessageBase;
import org.drools.verifier.report.components.AnalyticsRangeCheckMessage;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class AnalyticsTestStandalone {

	public static final void main(String[] args) {
		try {

			Collection<String> fileNames = new ArrayList<String>();

			// Test data
			// fileNames.add("MissingRangesForDates.drl");
			// fileNames.add("MissingRangesForDoubles.drl");
			// fileNames.add("MissingRangesForInts.drl");
			// fileNames.add("MissingRangesForVariables.drl");
			// fileNames.add("Misc.drl");
			// fileNames.add("Misc2.drl");
			fileNames.add("Misc3.drl");
			// fileNames.add("ConsequenceTest.drl");
			// fileNames.add("optimisation/OptimisationRestrictionOrderTest.drl");
			// fileNames.add("optimisation/OptimisationPatternOrderTest.drl");

			DrlParser parser = new DrlParser();
			Analyzer a = new Analyzer();

			for (String s : fileNames) {
				PackageDescr descr = parser.parse(new InputStreamReader(
						Analyzer.class.getResourceAsStream(s)));
				a.addPackageDescr(descr);
			}

			a.fireAnalysis();
			// System.out.print(a.getResultAsPlainText());
			// System.out.print(a.getResultAsXML());
			// a.writeComponentsHTML("/stash/");
			// a.writeComponentsHTML("/Users/michaelneale/foo.html");
			a.writeComponentsHTML("c:/");

			AnalyticsResult result = a.getResult();
			Collection<AnalyticsMessageBase> msgs = result
					.getBySeverity(Severity.ERROR);

			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				AnalyticsMessageBase msg = (AnalyticsMessageBase) iterator
						.next();
				System.out.println("ERR: " + msg.getMessage());
			}

			msgs = result.getBySeverity(Severity.WARNING);
			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				AnalyticsMessageBase msg = (AnalyticsMessageBase) iterator
						.next();
				System.out.println("WARN (" + msg.getClass().getSimpleName()
						+ "): " + msg.getMessage());
				System.out.println("\t FAULT: ["
						+ msg.getClass().getSimpleName() + "] "
						+ msg.getFaulty());
				if (msg instanceof AnalyticsMessage) {
					System.out.println("\t CAUSES (message):");
					AnalyticsMessage amsg = (AnalyticsMessage) msg;
					for (Iterator iterator2 = amsg.getCauses().iterator(); iterator2
							.hasNext();) {
						Cause c = (Cause) iterator2.next();
						System.out.println("\t\t ["
								+ c.getClass().getSimpleName() + "]" + c);

					}

				} else if (msg instanceof AnalyticsRangeCheckMessage) {
					System.out.println("\t CAUSES (range):");
					AnalyticsRangeCheckMessage amsg = (AnalyticsRangeCheckMessage) msg;
					for (Iterator iterator2 = amsg.getCauses().iterator(); iterator2
							.hasNext();) {
						Cause c = (Cause) iterator2.next();
						System.out.println("\t\t" + c);

					}

				}
			}

			msgs = result.getBySeverity(Severity.NOTE);
			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				AnalyticsMessageBase msg = (AnalyticsMessageBase) iterator
						.next();
				System.out.println("NOTE: " + msg.getMessage());
				System.out.println("\t" + msg.getFaulty());
			}

			Collection<AnalyticsClass> classes = result.getAnalyticsData()
					.getAllClasses();
			for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
				AnalyticsClass c = (AnalyticsClass) iterator.next();

				Collection<AnalyticsRule> cr = result.getAnalyticsData()
						.getRulesByClassId(c.getId());
				System.err.println("Class rules:" + cr);
				Set<Field> flds = c.getFields();
				for (Iterator iterator2 = flds.iterator(); iterator2.hasNext();) {
					Field f = (Field) iterator2.next();
					cr = result.getAnalyticsData().getRulesByFieldId(f.getId());
					System.err.println("Field rules: " + cr);

				}
			}

			// System.err.println(a.getResultAsPlainText());
			// System.out.println(result.toString());
		} catch (Throwable t) {
			t.printStackTrace();
		}
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
