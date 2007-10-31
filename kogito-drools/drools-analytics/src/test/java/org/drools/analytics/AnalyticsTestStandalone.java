package org.drools.analytics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class AnalyticsTestStandalone {

	public static final void main(String[] args) {
		try {
			PackageDescr descr = new DrlParser().parse(new InputStreamReader(
					Analyzer.class
							.getResourceAsStream("MissingRangesForDates.drl")));
			PackageDescr descr2 = new DrlParser()
					.parse(new InputStreamReader(Analyzer.class
							.getResourceAsStream("MissingRangesForDoubles.drl")));
			PackageDescr descr3 = new DrlParser().parse(new InputStreamReader(
					Analyzer.class
							.getResourceAsStream("MissingRangesForInts.drl")));
			PackageDescr descr4 = new DrlParser()
					.parse(new InputStreamReader(
							Analyzer.class
									.getResourceAsStream("MissingRangesForVariables.drl")));
			PackageDescr descr5 = new DrlParser().parse(new InputStreamReader(
					Analyzer.class.getResourceAsStream("Misc.drl")));
			PackageDescr descr6 = new DrlParser().parse(new InputStreamReader(
					Analyzer.class.getResourceAsStream("Misc2.drl")));
			PackageDescr descr7 = new DrlParser().parse(new InputStreamReader(
					Analyzer.class.getResourceAsStream("Misc3.drl")));
			PackageDescr descr8 = new DrlParser().parse(new InputStreamReader(
					Analyzer.class.getResourceAsStream("ConsequenceTest.drl")));
			PackageDescr descr9 = new DrlParser()
					.parse(new InputStreamReader(
							Analyzer.class
									.getResourceAsStream("optimisation/OptimisationRestrictionOrderTest.drl")));
			PackageDescr descr10 = new DrlParser()
			.parse(new InputStreamReader(
					Analyzer.class
					.getResourceAsStream("optimisation/OptimisationPatternOrderTest.drl")));

			Analyzer a = new Analyzer();
			// a.addPackageDescr(descr);
			// a.addPackageDescr(descr2);
			// a.addPackageDescr(descr3);
			// a.addPackageDescr(descr4);
			a.addPackageDescr(descr5);
			a.addPackageDescr(descr6);
			a.addPackageDescr(descr7);
			a.addPackageDescr(descr8);
			a.addPackageDescr(descr9);
			a.addPackageDescr(descr10);
			
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
