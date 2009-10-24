package org.drools.verifier.incoherence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class IncoherentPatternsTest extends TestBase {

	public void testIncoherentPatternsInSubRule() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent Patterns in rule possibility"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleVariables()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Incoherent Patterns in rule possibility, variables"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesLess()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with lesser value"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesGreater()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with greater value"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesEqualOrUnequal()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal value"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesEqualOrUnequalVariables()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal or unequal variables"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesEqualValue()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal value"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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

	public void testIncoherentPatternsInSubRuleRangesEqualVariable()
			throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session
				.setAgendaFilter(new RuleNameMatchesAgendaFilter(
						"Incoherent Patterns in rule possibility, ranges when not conflicts with equal variable"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("PatternsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierRule rule = (VerifierRule) ((VerifierMessage) o)
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
