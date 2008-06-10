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
import org.drools.verifier.Verifier;
import org.drools.verifier.components.VerifierClass;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.Field;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.VerifierRangeCheckMessage;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class VerifierTestStandalone {

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
			Verifier a = new Verifier();

			for (String s : fileNames) {
				PackageDescr descr = parser.parse(new InputStreamReader(
						Verifier.class.getResourceAsStream(s)));
				a.addPackageDescr(descr);
			}

			a.fireAnalysis();
			// System.out.print(a.getResultAsPlainText());
			// System.out.print(a.getResultAsXML());
			// a.writeComponentsHTML("/stash/");
			// a.writeComponentsHTML("/Users/michaelneale/foo.html");
			a.writeComponentsHTML("c:/");

			VerifierResult result = a.getResult();
			Collection<VerifierMessageBase> msgs = result
					.getBySeverity(Severity.ERROR);

			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				VerifierMessageBase msg = (VerifierMessageBase) iterator
						.next();
				System.out.println("ERR: " + msg.getMessage());
			}

			msgs = result.getBySeverity(Severity.WARNING);
			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				VerifierMessageBase msg = (VerifierMessageBase) iterator
						.next();
				System.out.println("WARN (" + msg.getClass().getSimpleName()
						+ "): " + msg.getMessage());
				System.out.println("\t FAULT: ["
						+ msg.getClass().getSimpleName() + "] "
						+ msg.getFaulty());
				if (msg instanceof VerifierMessage) {
					System.out.println("\t CAUSES (message):");
					VerifierMessage amsg = (VerifierMessage) msg;
					for (Iterator iterator2 = amsg.getCauses().iterator(); iterator2
							.hasNext();) {
						Cause c = (Cause) iterator2.next();
						System.out.println("\t\t ["
								+ c.getClass().getSimpleName() + "]" + c);

					}

				} else if (msg instanceof VerifierRangeCheckMessage) {
					System.out.println("\t CAUSES (range):");
					VerifierRangeCheckMessage amsg = (VerifierRangeCheckMessage) msg;
					for (Iterator iterator2 = amsg.getCauses().iterator(); iterator2
							.hasNext();) {
						Cause c = (Cause) iterator2.next();
						System.out.println("\t\t" + c);

					}

				}
			}

			msgs = result.getBySeverity(Severity.NOTE);
			for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
				VerifierMessageBase msg = (VerifierMessageBase) iterator
						.next();
				System.out.println("NOTE: " + msg.getMessage());
				System.out.println("\t" + msg.getFaulty());
			}

			Collection<VerifierClass> classes = result.getVerifierData()
					.getAllClasses();
			for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
				VerifierClass c = (VerifierClass) iterator.next();

				Collection<VerifierRule> cr = result.getVerifierData()
						.getRulesByClassId(c.getId());
				System.err.println("Class rules:" + cr);
				Set<Field> flds = c.getFields();
				for (Iterator iterator2 = flds.iterator(); iterator2.hasNext();) {
					Field f = (Field) iterator2.next();
					cr = result.getVerifierData().getRulesByFieldId(f.getId());
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
