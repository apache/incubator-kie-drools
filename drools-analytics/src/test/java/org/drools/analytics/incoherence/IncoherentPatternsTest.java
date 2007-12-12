package org.drools.analytics.incoherence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class IncoherentPatternsTest extends TestBase {

	public void testIncoherentPatternsInRulePossibility() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent Patterns in rule possibility"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 1"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 2"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 7"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityVariables()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent Patterns in rule possibility, variables"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 3"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 4"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 5"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 6"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesLess()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with lesser value"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 8"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 12"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesGreater()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with greater value"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 9"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 14"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesEqualOrUnequal()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal value"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 10"));
		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 15"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesEqualOrUnequalVariables()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal variables"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 11"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesEqualValue()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal value"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 16"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentPatternsInRulePossibilityRangesEqualVariable()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal variable"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent patterns 13"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
