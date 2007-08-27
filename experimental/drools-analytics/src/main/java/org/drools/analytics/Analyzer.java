package org.drools.analytics;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.analytics.result.AnalysisResultNormal;
import org.drools.analytics.result.Writer;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;



/**
 * 
 * @author Toni Rikkola
 */
public class Analyzer {

	private AnalysisResultNormal result = new AnalysisResultNormal();

	public void solvePackageDescr(PackageDescr descr) {
		try {

			System.setProperty("drools.accumulate.function.validatePattern",
					"com.analytics.accumulateFunction.ValidatePattern");

			// load up the rulebase
			RuleBase ruleBase = readRules();

			WorkingMemory workingMemory = ruleBase.newStatefulSession();

			RuleFlattener ruleFlattener = new RuleFlattener();

			ruleFlattener.insert(descr);

			// Rules with relations
			Collection<Object > objects = ruleFlattener.getDataObjects();
			for (Object o : objects) {
				workingMemory.insert(o);
			}

			// Object that returns the results.
			AnalysisResultNormal result = new AnalysisResultNormal();
			workingMemory.setGlobal("result", result);
			workingMemory.fireAllRules();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static RuleBase readRules() throws Exception {
		// read in the source
		Reader source = new InputStreamReader(Analyzer.class
				.getResourceAsStream("RangeCheckIntegers.drl"));

		PackageBuilder builder = new PackageBuilder();

		builder.addPackageFromDrl(source);

		Package pkg = builder.getPackage();

		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(pkg);
		return ruleBase;
	}

	/**
	 * Returns the analysis results as XML.
	 * 
	 * @return Analysis results as XML
	 */
	public String getResultAsXML() {
		return Writer.write(result);
	}

	/**
	 * Returns the analysis results as <code>AnalysisResult</code> object.
	 * 
	 * @return Analysis result
	 */
	public AnalysisResultNormal getResult() {
		return result;
	}
}
