package org.drools.analytics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.result.Gap;
import org.drools.base.RuleNameMatchesAgendaFilter;


/**
 * 
 * @author Toni Rikkola
 *
 */
public class RangeCheckDoublesTest extends TestBase {

	public void testSmallerAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for doubles, smaller and greater than"));

		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"));

		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		session.setGlobal("data", data);

		StatelessSessionResult sessionResult = session.executeWithResults(testData);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}

		assertTrue(rulesThatHadErrors.remove("Double range 1a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 2a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 3a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 4a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 7a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 7b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 8a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 8b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 9a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 9b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 10a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 10b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 11a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 11b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 12a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 13a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 14a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 15a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 18a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 18b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 19a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 19b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 20a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 20b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 21a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 21b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 22a, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 22b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testEqualAndGreaterThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for doubles, equal and greater than"));

		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"));

		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		session.setGlobal("data", data);

		StatelessSessionResult sessionResult = session.executeWithResults(testData);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}
		
		assertTrue(rulesThatHadErrors.remove("Double range 5b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 16b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}

	public void testEqualAndSmallerThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for doubles, equal and smaller than"));

		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"));

		AnalyticsData data = AnalyticsDataFactory.getAnalyticsData();
		session.setGlobal("data", data);

		StatelessSessionResult sessionResult = session.executeWithResults(testData);

		Iterator iter = sessionResult.iterateObjects();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Gap) {
				rulesThatHadErrors.add(((Gap) o).getRuleName());
			}
			// System.out.println(o);
		}
		
		assertTrue(rulesThatHadErrors.remove("Double range 6b, has gap"));
		assertTrue(rulesThatHadErrors.remove("Double range 17b, has gap"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " fired.");
			}
		}
	}
}
