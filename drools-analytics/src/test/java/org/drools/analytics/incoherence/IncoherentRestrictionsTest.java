package org.drools.analytics.incoherence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class IncoherentRestrictionsTest extends TestBase {

	public void testIncoherentLiteralRestrictionsInPatternPossibility()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent LiteralRestrictions in pattern possibility"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 1"));
		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 2"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentLiteralRestrictionsInPatternPossibilityImpossibleRanges()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent LiteralRestrictions with ranges in pattern possibility, impossible ranges"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 8"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentLiteralRestrictionsInPatternPossibilityImpossibleEqualityLess()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent LiteralRestrictions with ranges in pattern possibility, impossible equality less or equal"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 9"));
		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 11"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentLiteralRestrictionsInPatternPossibilityImpossibleEqualityGreater()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent LiteralRestrictions with ranges in pattern possibility, impossible equality greater"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 10"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentLiteralRestrictionsInPatternPossibilityImpossibleRange()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent LiteralRestrictions with ranges in pattern possibility, impossible range"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 7"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentVariableRestrictionsInPatternPossibility()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent VariableRestrictions in pattern possibility"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 3"));
		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 4"));
		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 5"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testIncoherentVariableRestrictionsInPatternPossibilityImpossibleRange()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent VariableRestrictions in pattern possibility, impossible range"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Pattern pattern = (Pattern) ((AnalyticsMessage) o).getFaulty();
				rulesThatHadErrors.add(pattern.getRuleName());
			}
		}

		assertTrue(rulesThatHadErrors.remove("Incoherent restrictions 6"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}