package org.drools.verifier.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.RedundancyType;
import org.drools.verifier.report.components.Severity;

public class WarningsTest extends TestBase {

	public void testRedundantRules() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Warnings.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant rule possibilities from different rules"));

		Collection<Object> objects = new ArrayList<Object>();

		VerifierRule rule1 = new VerifierRule();
		VerifierRule rule2 = new VerifierRule();

		Redundancy ruleRedundancy = new Redundancy(
				RedundancyType.STRONG, rule1, rule2);

		RulePossibility rp1 = new RulePossibility();
		rp1.setRuleId(rule1.getId());

		RulePossibility rp2 = new RulePossibility();
		rp2.setRuleId(rule2.getId());

		Redundancy rulePossibilityRedundancy1 = new Redundancy(
				RedundancyType.STRONG, rp1, rp2);

		Redundancy rulePossibilityRedundancy2 = new Redundancy(
				RedundancyType.STRONG, rp2, rp1);

		objects.add(rule1);
		objects.add(rule2);
		objects.add(ruleRedundancy);
		objects.add(rp1);
		objects.add(rp2);
		objects.add(rulePossibilityRedundancy1);
		objects.add(rulePossibilityRedundancy2);

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<VerifierMessageBase> notes = result
				.getBySeverity(Severity.WARNING);

		// Has at least one item.
		assertEquals(1, notes.size());

		VerifierMessageBase warning = notes.iterator().next();
		assertTrue(warning.getFaulty().equals(rulePossibilityRedundancy1));
	}

	public void testSubsumptantRules() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Warnings.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find subsumptant rule possibilities from different rules"));

		Collection<Object> objects = new ArrayList<Object>();

		VerifierRule rule1 = new VerifierRule();
		VerifierRule rule2 = new VerifierRule();

		Redundancy ruleRedundancy = new Redundancy(
				RedundancyType.STRONG, rule1, rule2);

		RulePossibility rp1 = new RulePossibility();
		rp1.setRuleId(rule1.getId());

		RulePossibility rp2 = new RulePossibility();
		rp2.setRuleId(rule2.getId());

		Redundancy rulePossibilityRedundancy1 = new Redundancy(
				RedundancyType.STRONG, rp1, rp2);

		objects.add(rule1);
		objects.add(rule2);
		objects.add(ruleRedundancy);
		objects.add(rp1);
		objects.add(rp2);
		objects.add(rulePossibilityRedundancy1);

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<VerifierMessageBase> notes = result
				.getBySeverity(Severity.WARNING);

		// Has at least one item.
		assertEquals(1, notes.size());

		VerifierMessageBase warning = notes.iterator().next();
		assertTrue(warning.getFaulty().equals(rulePossibilityRedundancy1));
	}
}
