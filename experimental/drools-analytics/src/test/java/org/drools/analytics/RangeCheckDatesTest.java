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
public class RangeCheckDatesTest extends TestBase {

	public void testSmallerAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RangeCheckDates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, smaller and greater than"));

		Collection<Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"));

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

		assertTrue(rulesThatHadErrors.remove("Date range 1a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 2a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 3a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 4a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 7a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 7b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 8a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 8b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 9a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 9b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 10a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 10b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 11a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 11b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 12a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 13a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 14a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 15a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 18a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 18b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 19a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 19b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 20a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 20b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 21a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 21b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 22a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 22b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testEqualAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RangeCheckDates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, equal and greater than"));

		Collection<Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"));

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

		assertTrue(rulesThatHadErrors.remove("Date range 5b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 16b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}

	public void testEqualAndSmallerThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("RangeCheckDates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, equal and smaller than"));

		Collection<Object> data = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"));

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

		assertTrue(rulesThatHadErrors.remove("Date range 6b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Date range 17b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}
}
