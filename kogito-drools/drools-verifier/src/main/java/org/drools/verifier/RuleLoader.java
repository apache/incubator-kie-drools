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
				// Missing consequence
				"Consequence.drl",
				// Always false
				"alwaysFalse/Patterns.drl",
				// Equivalence
				"equivalence/Rules.drl",
				// Incoherence
				"incoherence/Patterns.drl",
				"incoherence/Restrictions.drl",
				// Incompatibility
				"incompatibility/Patterns.drl",
				"incompatibility/Restrictions.drl",
				// Missing equality
				"missingEquality/MissingEquality.drl",
				// Opposites
				"opposites/Patterns.drl",
				"opposites/Restrictions.drl",
				"opposites/Rules.drl",
				// Optimization
				"optimisation/PatternOrder.drl",
				"optimisation/RestrictionOrder.drl",
				// Overlaps
				"overlaps/Restrictions.drl",
				// Range checks
				"rangeChecks/Clean.drl",
				"rangeChecks/Dates.drl", 
				"rangeChecks/Doubles.drl",
				"rangeChecks/Integers.drl",
				"rangeChecks/NumberPatterns.drl",
				"rangeChecks/Variables.drl",
				// Redundancy
				"redundancy/Consequence.drl", 
				"redundancy/Notes.drl",
				"redundancy/Patterns.drl",
				"redundancy/Possibilities.drl",
				"redundancy/Restrictions.drl",  
				"redundancy/Rules.drl",
				"redundancy/Warnings.drl", 
				// Reporting
				"reports/RangeCheckReports.drl", 
				// Subsumption
				"subsumption/Possibilities.drl",
				"subsumption/Restrictions.drl"
			};

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
