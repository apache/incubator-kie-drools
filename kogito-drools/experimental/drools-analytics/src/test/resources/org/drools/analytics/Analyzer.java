package org.drools.analytics;

import java.util.Collection;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;
import org.drools.analytics.result.AnalysisResultNormal;
import org.drools.analytics.result.ReportModeller;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

/**
 * 
 * @author Toni Rikkola
 */
public class Analyzer {

	private AnalysisResultNormal result = new AnalysisResultNormal();

	public void addPackageDescr(PackageDescr descr) {
		try {

			PackageDescrFlattener ruleFlattener = new PackageDescrFlattener();

			ruleFlattener.insert(descr);

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void fireAnalysis() {
		try {
			AnalyticsData data = AnalyticsDataMaps.getAnalyticsDataMaps();

			System.setProperty("drools.accumulate.function.validatePattern",
					"org.drools.analytics.accumulateFunction.ValidatePattern");

			// load up the rulebase
			RuleBase ruleBase = createRuleBase();

			WorkingMemory workingMemory = ruleBase.newStatefulSession();

			for (Object o : data.getAll()) {
				workingMemory.insert(o);
			}

			// Object that returns the results.
			workingMemory.setGlobal("result", result);
			workingMemory.fireAllRules();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Returns the analysis results as plain text.
	 * 
	 * @return Analysis results as plain text.
	 */
	public String getResultAsPlainText() {
		return ReportModeller.writePlainText(result);
	}

	/**
	 * Returns the analysis results as XML.
	 * 
	 * @return Analysis results as XML
	 */
	public String getResultAsXML() {
		return ReportModeller.writeXML(result);
	}
        
        /**
	 * Returns the analysis results as HTML.
	 * 
	 * @return Analysis results as HTML
	 */
	public String getResultAsHTML() {
		return ReportModeller.writeHTML(result);
	}

	/**
	 * Returns the analysis results as <code>AnalysisResult</code> object.
	 * 
	 * @return Analysis result
	 */
	public AnalysisResultNormal getResult() {
		return result;
	}

	private static RuleBase createRuleBase() throws Exception {

		RuleBase ruleBase = RuleBaseFactory.newRuleBase();

		Collection<Package> packages = RuleLoader.loadPackages();
		for (Package pkg : packages) {

			ruleBase.addPackage(pkg);
		}

		return ruleBase;
	}
}
