package org.drools.verifier;

import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.compiler.DrlParser;
import org.drools.lang.descr.PackageDescr;
import org.drools.verifier.Verifier;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.report.components.Severity;

public class VerifierTest extends TestCase {

	public void testAnalyzer() throws Exception {
		Verifier anal = new Verifier();

		DrlParser p = new DrlParser();
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		PackageDescr pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		VerifierResult result = anal.getResult();
		assertNotNull(result);
		assertEquals(0, result.getBySeverity(Severity.ERROR).size());
		assertEquals(10, result.getBySeverity(Severity.WARNING).size());
		assertEquals(1, result.getBySeverity(Severity.NOTE).size());


		//check it again
		anal = new Verifier();

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
		Verifier anal = new Verifier();
		DrlParser p = new DrlParser();
		InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("Misc3.drl"));
		PackageDescr pkg = p.parse(reader);
		assertFalse(p.hasErrors());

		anal.addPackageDescr(pkg);
		anal.fireAnalysis();

		RuleBase original = Verifier.verifierKnowledgeBase;

		Verifier anal2 = new Verifier();

		assertSame(original, Verifier.verifierKnowledgeBase);

		anal2.reloadAnalysisKnowledgeBase();
		assertNotSame(original, Verifier.verifierKnowledgeBase);


	}

}
