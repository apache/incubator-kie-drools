package org.drools.analytics;

import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;

import junit.framework.TestCase;

public class AnalyzerTest extends TestCase {

	public void testAnalyzer() throws Exception {
		Analyzer anal = new Analyzer();

		DrlParser p = new DrlParser();
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		PackageDescr pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		AnalyticsResult result = anal.getResult();
		assertNotNull(result);
		assertEquals(0, result.getBySeverity(AnalyticsMessageBase.Severity.ERROR).size());
		assertEquals(17, result.getBySeverity(AnalyticsMessageBase.Severity.WARNING).size());
		assertEquals(1, result.getBySeverity(AnalyticsMessageBase.Severity.NOTE).size());


		//check it again
		anal = new Analyzer();

		p = new DrlParser();
		reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		result = anal.getResult();
		assertNotNull(result);
		assertEquals(0, result.getBySeverity(AnalyticsMessageBase.Severity.ERROR).size());
		assertEquals(17, result.getBySeverity(AnalyticsMessageBase.Severity.WARNING).size());
		assertEquals(1, result.getBySeverity(AnalyticsMessageBase.Severity.NOTE).size());




	}

	public void testCacheKnowledgeBase() throws Exception {
		Analyzer anal = new Analyzer();
		DrlParser p = new DrlParser();
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		PackageDescr pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		RuleBase original = Analyzer.analysisKnowledgeBase;

		Analyzer anal2 = new Analyzer();

		assertSame(original, Analyzer.analysisKnowledgeBase);

		anal2.reloadAnalysisKnowledgeBase();
		assertNotSame(original, Analyzer.analysisKnowledgeBase);


	}

}
