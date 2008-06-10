package org.drools.verifier.redundancy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.AnalyticsRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.dao.AnalyticsResultFactory;
import org.drools.verifier.report.components.Redundancy;

public class RedundantRulesAndPatternsTest extends RedundancyTestBase {

	public void testRuleRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Rules.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Rules, strong redundancy"));

		Collection<Object> data = new ArrayList<Object>();

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		AnalyticsRule rule1 = new AnalyticsRule();
		rule1.setRuleName(ruleName1);
		AnalyticsRule rule2 = new AnalyticsRule();
		rule2.setRuleName(ruleName2);

		TextConsequence c1 = new TextConsequence();
		c1.setRuleName(ruleName1);
		TextConsequence c2 = new TextConsequence();
		c2.setRuleName(ruleName2);

		Redundancy r1 = new Redundancy(rule1, rule2);
		Redundancy r2 = new Redundancy(c1, c2);

		data.add(rule1);
		data.add(rule2);
		data.add(c1);
		data.add(c2);
		data.add(r1);
		data.add(r2);

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName1, ruleName2)
				^ TestBase.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testPatternRedundancyWithRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Patterns with restrictions"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("PatternRedundancyTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy with restrictions 1a",
				"Pattern redundancy with restrictions 1b")
				^ TestBase.mapContains(map,
						"Pattern redundancy with restrictions 1b",
						"Pattern redundancy with restrictions 1a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy with restrictions 2a",
				"Pattern redundancy with restrictions 2b")
				^ TestBase.mapContains(map,
						"Pattern redundancy with restrictions 2b",
						"Pattern redundancy with restrictions 2a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy with restrictions 3a",
				"Pattern redundancy with restrictions 3b")
				^ TestBase.mapContains(map,
						"Pattern redundancy with restrictions 3b",
						"Pattern redundancy with restrictions 3a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy with restrictions 4a",
				"Pattern redundancy with restrictions 4b")
				^ TestBase.mapContains(map,
						"Pattern redundancy with restrictions 4b",
						"Pattern redundancy with restrictions 4a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testPatternRedundancyWithoutRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Patterns without restrictions"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("PatternRedundancyTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy without restrictions 1a",
				"Pattern redundancy without restrictions 1b")
				^ TestBase.mapContains(map,
						"Pattern redundancy without restrictions 1b",
						"Pattern redundancy without restrictions 1a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy without restrictions 2a",
				"Pattern redundancy without restrictions 2b")
				^ TestBase.mapContains(map,
						"Pattern redundancy without restrictions 2b",
						"Pattern redundancy without restrictions 2a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy without restrictions 3a",
				"Pattern redundancy without restrictions 3b")
				^ TestBase.mapContains(map,
						"Pattern redundancy without restrictions 3b",
						"Pattern redundancy without restrictions 3a"));
		assertTrue(TestBase.mapContains(map,
				"Pattern redundancy without restrictions 4a",
				"Pattern redundancy without restrictions 4b")
				^ TestBase.mapContains(map,
						"Pattern redundancy without restrictions 4b",
						"Pattern redundancy without restrictions 4a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

}
