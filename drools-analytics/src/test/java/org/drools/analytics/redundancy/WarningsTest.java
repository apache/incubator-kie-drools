package org.drools.analytics.redundancy;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Redundancy;
import org.drools.analytics.report.components.RedundancyType;
import org.drools.analytics.report.components.Severity;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class WarningsTest extends TestBase {

	public void testRedundantRules() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Warnings.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant rule possibilities from different rules"));

		Collection<Object> objects = new ArrayList<Object>();

		AnalyticsRule rule1 = new AnalyticsRule();
		AnalyticsRule rule2 = new AnalyticsRule();

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

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<AnalyticsMessageBase> notes = result
				.getBySeverity(Severity.WARNING);

		// Has at least one item.
		assertEquals(1, notes.size());

		AnalyticsMessageBase warning = notes.iterator().next();
		assertTrue(warning.getFaulty().equals(rulePossibilityRedundancy1));
	}

	public void testSubsumptantRules() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Warnings.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find subsumptant rule possibilities from different rules"));

		Collection<Object> objects = new ArrayList<Object>();

		AnalyticsRule rule1 = new AnalyticsRule();
		AnalyticsRule rule2 = new AnalyticsRule();

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

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(objects);

		Collection<AnalyticsMessageBase> notes = result
				.getBySeverity(Severity.WARNING);

		// Has at least one item.
		assertEquals(1, notes.size());

		AnalyticsMessageBase warning = notes.iterator().next();
		assertTrue(warning.getFaulty().equals(rulePossibilityRedundancy1));
	}
}
