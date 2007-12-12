package org.drools.analytics.redundancy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.Redundancy;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class RedundantPossibilitiesTest extends RedundancyTestBase {

	public void testPatternPossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find pattern possibility redundancy"));

		Collection<Object> data = new ArrayList<Object>();

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		session.setGlobal("result", result);

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		Pattern p1 = new Pattern();
		p1.setRuleName(ruleName1);
		Pattern p2 = new Pattern();
		p2.setRuleName(ruleName2);

		LiteralRestriction lr1 = new LiteralRestriction();
		lr1.setRuleName(ruleName1);
		LiteralRestriction lr2 = new LiteralRestriction();
		lr2.setRuleName(ruleName2);

		PatternPossibility pp1 = new PatternPossibility();
		pp1.setPatternId(p1.getId());
		pp1.setRuleName(ruleName1);
		pp1.add(lr1);

		PatternPossibility pp2 = new PatternPossibility();
		pp2.setPatternId(p2.getId());
		pp2.setRuleName(ruleName2);
		pp2.add(lr2);

		Redundancy r1 = new Redundancy(lr1, lr2);
		Redundancy r2 = new Redundancy(p1, p2);

		data.add(p1);
		data.add(p2);
		data.add(lr1);
		data.add(lr2);
		data.add(pp1);
		data.add(pp2);
		data.add(r1);
		data.add(r2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName1, ruleName2));
		assertTrue(TestBase.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testRulePossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find rule possibility redundancy"));

		Collection<Object> data = new ArrayList<Object>();

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		session.setGlobal("result", result);

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		AnalyticsRule r1 = new AnalyticsRule();
		r1.setRuleName(ruleName1);
		AnalyticsRule r2 = new AnalyticsRule();
		r2.setRuleName(ruleName2);

		PatternPossibility pp1 = new PatternPossibility();
		pp1.setRuleName(ruleName1);
		PatternPossibility pp2 = new PatternPossibility();
		pp2.setRuleName(ruleName2);

		RulePossibility rp1 = new RulePossibility();
		rp1.setRuleId(r1.getId());
		rp1.setRuleName(ruleName1);
		rp1.add(pp1);

		RulePossibility rp2 = new RulePossibility();
		rp2.setRuleId(r2.getId());
		rp2.setRuleName(ruleName2);
		rp2.add(pp2);

		Redundancy possibilityredundancy = new Redundancy(
				Redundancy.RedundancyType.STRONG, pp1, pp2);
		Redundancy ruleRedundancy = new Redundancy(r1, r2);

		data.add(r1);
		data.add(r2);
		data.add(pp1);
		data.add(pp2);
		data.add(possibilityredundancy);
		data.add(ruleRedundancy);
		data.add(rp1);
		data.add(rp2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName1, ruleName2));
		assertTrue(TestBase.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}
}
