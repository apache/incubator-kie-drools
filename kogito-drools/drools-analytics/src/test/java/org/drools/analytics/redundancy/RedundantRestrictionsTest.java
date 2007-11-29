package org.drools.analytics.redundancy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.PartialRedundancy;
import org.drools.analytics.report.components.Redundancy;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class RedundantRestrictionsTest extends RedundancyTestBase {

	public void fixmetestPartOfRulePossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find part of redundant RulePossibility combination"));

		Collection<Object> data = new ArrayList<Object>();

		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
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

		Redundancy redundancy1 = new Redundancy(pp1, pp2);
		Redundancy redundancy2 = new Redundancy(r1, r2);

		data.add(r1);
		data.add(r2);
		data.add(rp1);
		data.add(rp2);
		data.add(pp1);
		data.add(pp2);
		data.add(redundancy1);
		data.add(redundancy2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<Redundancy>> map = new HashMap<String, Set<Redundancy>>();

		Iterator<Object> iter = sessionResult.iterateObjects();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof PartialRedundancy) {
				PartialRedundancy pr = (PartialRedundancy) o;
				AnalyticsComponent left = (AnalyticsComponent) pr.getLeft();
				AnalyticsComponent right = (AnalyticsComponent) pr.getRight();

				String key = left.getRuleName() + ":" + right.getRuleName();
				if (map.containsKey(key)) {
					Set<Redundancy> set = map.get(key);
					set.add(pr.getRedundancy());
				} else {
					Set<Redundancy> set = new HashSet<Redundancy>();
					set.add(pr.getRedundancy());
					map.put(key, set);
				}
			}
		}

		assertTrue(RedundancyTestBase.mapContains(map, ruleName1 + ":"
				+ ruleName2, redundancy1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testPatternRedundancyWithRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Patterns with restrictions"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("PatternRedundancyTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
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

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("PatternRedundancyTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
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

	public void testAnalyticsLiteralRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant LiteralRestriction"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("RedundancyLiteralRestrictionTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue((TestBase.mapContains(map, "Redundant 1a", "Redundant 1b") ^ TestBase
				.mapContains(map, "Redundant 1b", "Redundant 1a")));
		assertTrue((TestBase.mapContains(map, "Redundant 2a", "Redundant 2b") ^ TestBase
				.mapContains(map, "Redundant 2b", "Redundant 2a")));
		assertTrue((TestBase.mapContains(map, "Redundant 3a", "Redundant 3b") ^ TestBase
				.mapContains(map, "Redundant 3b", "Redundant 3a")));
		assertTrue((TestBase.mapContains(map, "Redundant 4a", "Redundant 4b") ^ TestBase
				.mapContains(map, "Redundant 4b", "Redundant 4a")));
		assertTrue((TestBase.mapContains(map, "Redundant 5a", "Redundant 5b") ^ TestBase
				.mapContains(map, "Redundant 5b", "Redundant 5a")));
		assertTrue((TestBase.mapContains(map, "Redundant 6a", "Redundant 6b") ^ TestBase
				.mapContains(map, "Redundant 6b", "Redundant 6a")));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testAnalyticsVariableRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant VariableRestriction"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("SubsumptionVariableRestrictionTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, "Redundant 1a", "Redundant 1b")
				^ TestBase.mapContains(map, "Redundant 1b", "Redundant 1a"));
		assertTrue(TestBase.mapContains(map, "Redundant 2a", "Redundant 2a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}
}
