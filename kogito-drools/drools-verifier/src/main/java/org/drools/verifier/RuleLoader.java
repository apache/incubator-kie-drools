package org.drools.verifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.rule.Package;

class RuleLoader {

	public static Collection<Package> loadPackages() {

		String[] fileNames = new String[] {
				// Equivalence
				"equivalence/Rules.drl",
				// Incoherence
				"incoherence/Patterns.drl",
				"incoherence/Restrictions.drl",
				// Missing equality
				"missingEquality/MissingEquality.drl",
				// Optimization
				"optimisation/RestrictionOrder.drl",
				"optimisation/PatternOrder.drl",
				// Range checks
				"rangeChecks/Dates.drl", "rangeChecks/Doubles.drl",
				"rangeChecks/Integers.drl",
				"rangeChecks/Patterns.drl",
				"rangeChecks/Variables.drl",
				"rangeChecks/Clean.drl",
				// Redundancy
				"redundancy/Restrictions.drl", "redundancy/Notes.drl",
				"redundancy/Consequence.drl", "redundancy/Patterns.drl",
				"redundancy/Possibilities.drl", "redundancy/Rules.drl",
				"redundancy/Warnings.drl", "reports/RangeCheckReports.drl",
				// Missing consequence
				"Consequence.drl", };

		Collection<Package> packages = new ArrayList<Package>();

		for (int i = 0; i < fileNames.length; i++) {
			try {
				InputStreamReader reader = new InputStreamReader(
						RuleLoader.class.getResourceAsStream(fileNames[i]));
				packages.add(loadPackage(reader));
				reader.close();
			} catch (DroolsParserException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.err.println("Error when opening file " + fileNames[i]
						+ ".");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return packages;
	}

	private static Package loadPackage(InputStreamReader reader)
			throws DroolsParserException, IOException {

        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler",
                                "JANINO" );

		PackageBuilder builder = new PackageBuilder(new PackageBuilderConfiguration( properties ));

		builder.addPackageFromDrl(reader);

		return builder.getPackage();
	}
}
