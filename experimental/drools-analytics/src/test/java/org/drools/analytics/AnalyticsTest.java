package org.drools.analytics;

import java.io.InputStreamReader;

import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class AnalyticsTest {

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

			Analyzer a = new Analyzer();
			a.addPackageDescr(descr);
			// a.addPackageDescr(descr2);
			// a.addPackageDescr(descr3);
			// a.addPackageDescr(descr4);
			a.fireAnalysis();
			System.out.print(a.getResultAsPlainText());
			// System.out.print(a.getResultAsXML());

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
