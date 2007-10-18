package org.drools.analytics;

import java.util.Collection;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.report.ReportModeller;
import org.drools.analytics.report.html.ComponentsReportModeller;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

/**
 * 
 * @author Toni Rikkola
 */
public class Analyzer {

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
			AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
			AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();

			System.setProperty("drools.accumulate.function.validatePattern",
					"org.drools.analytics.accumulateFunction.ValidatePattern");

			// load up the rule base
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
		return ReportModeller.writePlainText();
	}

	/**
	 * Returns the analysis results as XML.
	 * 
	 * @return Analysis results as XML
	 */
	public String getResultAsXML() {
		return ReportModeller.writeXML();
	}

	/**
	 * Returns the analysis results as HTML.
	 * 
	 * @return Analysis results as HTML
	 */
	public void writeComponentsHTML(String path) {
		ComponentsReportModeller.writeHTML(path);
	}

	/**
	 * Returns the analysis results as <code>AnalysisResult</code> object.
	 * 
	 * @return Analysis result
	 */
	public AnalyticsResult getResult() {
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
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
