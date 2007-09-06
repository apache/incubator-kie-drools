package org.drools.analytics;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataMaps;
import org.drools.analytics.result.AnalysisResultNormal;
import org.drools.analytics.result.ReportWriter;
import org.drools.compiler.PackageBuilder;
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
					"com.analytics.accumulateFunction.ValidatePattern");

			// load up the rulebase
			RuleBase ruleBase = readRules();

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
		return ReportWriter.writePlainText(result);
	}

	/**
	 * Returns the analysis results as XML.
	 * 
	 * @return Analysis results as XML
	 */
	public String getResultAsXML() {
		return ReportWriter.writeXML(result);
	}

	/**
	 * Returns the analysis results as <code>AnalysisResult</code> object.
	 * 
	 * @return Analysis result
	 */
	public AnalysisResultNormal getResult() {
		return result;
	}

	private static RuleBase readRules() throws Exception {
		// read in the source
		List<InputStreamReader> list = new ArrayList<InputStreamReader>();

		list.add(new InputStreamReader(Analyzer.class
				.getResourceAsStream("RangeCheckIntegers.drl")));
		list.add(new InputStreamReader(Analyzer.class
				.getResourceAsStream("reports/RangeCheckReports.drl")));

		RuleBase ruleBase = RuleBaseFactory.newRuleBase();

		for (InputStreamReader reader : list) {

			PackageBuilder builder = new PackageBuilder();

			builder.addPackageFromDrl(reader);

			Package pkg = builder.getPackage();
			ruleBase.addPackage(pkg);
		}

		return ruleBase;
	}
}
