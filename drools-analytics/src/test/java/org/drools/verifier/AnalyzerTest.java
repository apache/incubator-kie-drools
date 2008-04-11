package org.drools.verifier;

import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.Analyzer;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.report.components.Severity;

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
		assertEquals(0, result.getBySeverity(Severity.ERROR).size());
		assertEquals(10, result.getBySeverity(Severity.WARNING).size());
		assertEquals(1, result.getBySeverity(Severity.NOTE).size());


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
		assertEquals(0, result.getBySeverity(Severity.ERROR).size());
		assertEquals(10, result.getBySeverity(Severity.WARNING).size());
		assertEquals(1, result.getBySeverity(Severity.NOTE).size());




	}

	public void testCacheKnowledgeBase() throws Exception {
		Analyzer anal = new Analyzer();
		DrlParser p = new DrlParser();
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		PackageDescr pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		RuleBase original = Analyzer.verifierKnowledgeBase;

		Analyzer anal2 = new Analyzer();

		assertSame(original, Analyzer.verifierKnowledgeBase);

		anal2.reloadAnalysisKnowledgeBase();
		assertNotSame(original, Analyzer.verifierKnowledgeBase);


	}

}
