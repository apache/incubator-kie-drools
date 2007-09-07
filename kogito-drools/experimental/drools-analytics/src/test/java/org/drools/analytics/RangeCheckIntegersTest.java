package org.drools.analytics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.result.AnalysisResultNormal;
import org.drools.analytics.result.Gap;
import org.drools.base.RuleNameMatchesAgendaFilter;


/**
 * 
 * @author Toni Rikkola
 *
 */
public class RangeCheckIntegersTest extends TestBase {

	public void testSmallerAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/RangeCheckIntegers.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for integers, smaller and greater than"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForInts.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Missing int range 1a, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 1b, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 2a, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 2b, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 3a, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 3b, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 4a, warning"));
		assertTrue(rulesThatHadErrors.remove("Missing int range 4b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 7a, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 7b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 8a, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 8b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 9a, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 9b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 10a, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 10b, warning"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testEqualAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/RangeCheckIntegers.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for integers, equal and greater than"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForInts.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Missing int range 5b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 11b, warning"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}

	public void testEqualAndSmallerThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/RangeCheckIntegers.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for integers, equal and smaller than"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForInts.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}
		assertTrue(rulesThatHadErrors.remove("Missing int range 6b, warning"));
		assertTrue(rulesThatHadErrors
				.remove("Missing not int range 12b, warning"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}
}
