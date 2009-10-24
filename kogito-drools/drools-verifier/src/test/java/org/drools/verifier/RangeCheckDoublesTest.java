package org.drools.verifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Gap;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class RangeCheckDoublesTest extends TestBase {

	public void testSmallerOrEqual() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Range check for doubles, if smaller than or equal is missing"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"), result
				.getVerifierData());

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

		assertTrue(rulesThatHadErrors.remove("Double gap rule 4a"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 5a"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testGreaterOrEqual() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Range check for doubles, if greater than or equal is missing"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"), result
				.getVerifierData());

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

		assertTrue(rulesThatHadErrors.remove("Double gap rule 4b"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 5b"));

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

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"), result
				.getVerifierData());

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

		assertTrue(rulesThatHadErrors.remove("Double gap rule 1"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 7b"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 3"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 9b"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testEqualAndSmallerThan() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("rangeChecks/Doubles.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Range check for doubles, equal and smaller than"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingRangesForDoubles.drl"), result
				.getVerifierData());

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

		assertTrue(rulesThatHadErrors.remove("Double gap rule 1"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 6b"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 2"));
		assertTrue(rulesThatHadErrors.remove("Double gap rule 9a"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
