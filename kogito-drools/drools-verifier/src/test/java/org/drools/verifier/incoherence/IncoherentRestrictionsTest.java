package org.drools.verifier.incoherence;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.Severity;

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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("RestrictionsTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.ERROR).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Pattern pattern = (Pattern) ((VerifierMessage) o).getFaulty();
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