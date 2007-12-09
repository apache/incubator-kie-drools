package org.drools.analytics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class AnalyticsTestStandalone {

	public static final void main(String[] args) {
		try {

			Collection<String> fileNames = new ArrayList<String>();

			// Test data
//			fileNames.add("MissingRangesForDates.drl");
//			fileNames.add("MissingRangesForDoubles.drl");
//			fileNames.add("MissingRangesForInts.drl");
//			fileNames.add("MissingRangesForVariables.drl");
//			fileNames.add("Misc.drl");
//			fileNames.add("Misc2.drl");
			fileNames.add("Misc3.drl");
//			fileNames.add("ConsequenceTest.drl");
//			fileNames.add("optimisation/OptimisationRestrictionOrderTest.drl");
//			fileNames.add("optimisation/OptimisationPatternOrderTest.drl");

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
			a.writeComponentsHTML("C:\\");
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
