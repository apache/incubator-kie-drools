package org.drools.analytics;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

class RuleLoader {

	public static Collection<Package> loadPackages() {
		Collection<Package> packages = new ArrayList<Package>();

		Collection<InputStreamReader> readers = readInputStreamReaders();

		for (InputStreamReader reader : readers) {
			try {
				packages.add(loadPackage(reader));
			} catch (DroolsParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return packages;
	}

	private static Package loadPackage(InputStreamReader reader)
			throws DroolsParserException, IOException {

		PackageBuilder builder = new PackageBuilder();

		builder.addPackageFromDrl(reader);

		return builder.getPackage();
	}

	private static Collection<InputStreamReader> readInputStreamReaders() {
		Collection<InputStreamReader> list = new ArrayList<InputStreamReader>();

		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Dates.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Doubles.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Integers.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Patterns.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Variables.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("rangeChecks/Clean.drl")));

		// list.add(new InputStreamReader(RuleLoader.class
		// .getResourceAsStream("redundancy/Possibilities.drl")));
		//
		// list.add(new InputStreamReader(RuleLoader.class
		// .getResourceAsStream("redundancy/Patterns.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("redundancy/Restrictions.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("redundancy/Notes.drl")));
		// list.add(new InputStreamReader(RuleLoader.class
		// .getResourceAsStream("redundancy/Rules.drl")));
		list.add(new InputStreamReader(RuleLoader.class
				.getResourceAsStream("reports/RangeCheckReports.drl")));

		return list;
	}
}
