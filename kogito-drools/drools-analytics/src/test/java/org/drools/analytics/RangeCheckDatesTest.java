package org.drools.analytics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.report.components.Gap;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class RangeCheckDatesTest extends TestBase {

	public void testFake() {
		assertTrue(true);
	}

	public void fixmetestSmallerOrEqual() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Dates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, if smaller than or equal is missing"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session
				.executeWithResults(testData);

		Iterator<Object> iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Date gap rule 4a"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 5a"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void fixmetestGreaterOrEqual() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Dates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, if greater than or equal is missing"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session
				.executeWithResults(testData);

		Iterator<Object> iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Date gap rule 4b"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 5b"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void fixmetestEqualAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Dates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, equal and greater than"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session
				.executeWithResults(testData);

		Iterator<Object> iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Date gap rule 1"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 7b"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 3"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void fixmetestEqualAndSmallerThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Dates.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for dates, equal and smaller than"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDates.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session
				.executeWithResults(testData);

		Iterator<Object> iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Date gap rule 1"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 6b"));
		assertTrue(rulesThatHadErrors.remove("Date gap rule 2"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
