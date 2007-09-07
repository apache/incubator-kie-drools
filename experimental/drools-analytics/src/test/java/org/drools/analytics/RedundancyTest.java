package org.drools.analytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.result.AnalysisResultNormal;
import org.drools.analytics.result.PartialRedundancy;
import org.drools.analytics.result.Redundancy;
import org.drools.analytics.result.Subsumption;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class RedundancyTest extends TestBase {

	public void testPartOfRulePossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find part of redundant RulePossibility combination"));

		Collection<Object> data = new ArrayList<Object>();

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

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

		Iterator iter = sessionResult.iterateObjects();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof PartialRedundancy) {
				PartialRedundancy pr = (PartialRedundancy) o;
				String key = pr.getLeft().getRuleName() + ":"
						+ pr.getRight().getRuleName();
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

		assertTrue(RedundancyTest.mapContains(map, ruleName1 + ":" + ruleName2,
				redundancy1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testPossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Possibilities"));

		Collection<Object> data = new ArrayList<Object>();

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		PatternPossibility pp1 = new PatternPossibility();
		pp1.setRuleName(ruleName1);

		PatternPossibility pp2 = new PatternPossibility();
		pp2.setRuleName(ruleName2);

		Subsumption s1 = new Subsumption(pp1, pp2);
		Subsumption s2 = new Subsumption(pp2, pp1);

		data.add(pp1);
		data.add(pp2);
		data.add(s1);
		data.add(s2);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Iterator iter = sessionResult.iterateObjects();

		Map<String, Set<String>> map = createRedundancyMap(iter);

		assertTrue(RedundancyTest.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testPartOfPatternPossibilityRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find part of redundant PatternPossibility combination"));

		Collection<Object> data = new ArrayList<Object>();

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

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

		Map<String, Set<Redundancy>> map = new HashMap<String, Set<Redundancy>>();

		Iterator iter = sessionResult.iterateObjects();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof PartialRedundancy) {
				PartialRedundancy pr = (PartialRedundancy) o;
				String key = pr.getLeft().getRuleName() + ":"
						+ pr.getRight().getRuleName();
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

		assertTrue(RedundancyTest.mapContains(map, ruleName1 + ":" + ruleName2,
				r1));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	/*
	 * Not enough data to test this right now.
	 */
	/*
	 * public void testRuleRedundancy() throws Exception { StatelessSession
	 * session = getStatelessSession(this.getClass()
	 * .getResourceAsStream("Redundancy.drl"));
	 * 
	 * session.setAgendaFilter(new RuleNameMatchesAgendaFilter( "Find redundant
	 * Rule shells"));
	 * 
	 * Collection<Object> data = getTestData(this.getClass()
	 * .getResourceAsStream("RuleRedundancyTest.drl"));
	 * 
	 * AnalysisResultNormal analysisResult = new AnalysisResultNormal();
	 * session.setGlobal("result", analysisResult);
	 * 
	 * StatelessSessionResult sessionResult = session.executeWithResults(data);
	 * 
	 * Map<String, Set<String>> map = createRedundancyMap(sessionResult
	 * .iterateObjects());
	 * 
	 * assertTrue(mapContains(map, "Rule redundancy 1a", "Rule redundancy 1b"));
	 * assertTrue(mapContains(map, "Rule redundancy 1b", "Rule redundancy 1a"));
	 * assertTrue(mapContains(map, "Rule redundancy 2a", "Rule redundancy 2b"));
	 * assertTrue(mapContains(map, "Rule redundancy 2b", "Rule redundancy 2a"));
	 * assertTrue(mapContains(map, "Rule redundancy 3a", "Rule redundancy 3b"));
	 * assertTrue(mapContains(map, "Rule redundancy 3b", "Rule redundancy 3a"));
	 * assertTrue(mapContains(map, "Rule redundancy 4a", "Rule redundancy 4b"));
	 * assertTrue(mapContains(map, "Rule redundancy 4b", "Rule redundancy 4a"));
	 * 
	 * if (!map.isEmpty()) { fail("More redundancies than was expected."); } }
	 */

	public void testPatternRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Patterns.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant Pattern shells"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("PatternRedundancyTest.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, "Pattern redundancy 1a",
				"Pattern redundancy 1b"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 1b",
				"Pattern redundancy 1a"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 2a",
				"Pattern redundancy 2b"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 2b",
				"Pattern redundancy 2a"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 3a",
				"Pattern redundancy 3b"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 3b",
				"Pattern redundancy 3a"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 4a",
				"Pattern redundancy 4b"));
		assertTrue(TestBase.mapContains(map, "Pattern redundancy 4b",
				"Pattern redundancy 4a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testAnalyticsLiteralRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant LiteralRestriction"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("RedundancyLiteralRestrictionTest.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, "Redundant 1a", "Redundant 1b"));
		assertTrue(TestBase.mapContains(map, "Redundant 1b", "Redundant 1a"));
		assertTrue(TestBase.mapContains(map, "Redundant 2a", "Redundant 2b"));
		assertTrue(TestBase.mapContains(map, "Redundant 2b", "Redundant 2a"));
		assertTrue(TestBase.mapContains(map, "Redundant 3a", "Redundant 3b"));
		assertTrue(TestBase.mapContains(map, "Redundant 3b", "Redundant 3a"));
		assertTrue(TestBase.mapContains(map, "Redundant 4a", "Redundant 4b"));
		assertTrue(TestBase.mapContains(map, "Redundant 4b", "Redundant 4a"));
		assertTrue(TestBase.mapContains(map, "Redundant 5a", "Redundant 5b"));
		assertTrue(TestBase.mapContains(map, "Redundant 5b", "Redundant 5a"));
		assertTrue(TestBase.mapContains(map, "Redundant 6a", "Redundant 6b"));
		assertTrue(TestBase.mapContains(map, "Redundant 6b", "Redundant 6a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	public void testAnalyticsVariableRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant VariableRestriction"));

		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("SubsumptionVariableRestrictionTest.drl"));

		AnalysisResultNormal analysisResult = new AnalysisResultNormal();
		session.setGlobal("result", analysisResult);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, "Redundant 1a", "Redundant 1b"));
		assertTrue(TestBase.mapContains(map, "Redundant 1b", "Redundant 1a"));
		assertTrue(TestBase.mapContains(map, "Redundant 2a", "Redundant 2a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

	/**
	 * Creates redundancy map from Redundancy objects, one rule may have several
	 * redundancy dependencies.
	 * 
	 * @param iter
	 * @return
	 */
	private Map<String, Set<String>> createRedundancyMap(Iterator iter) {

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Redundancy) {
				Redundancy r = (Redundancy) o;
				if (map.containsKey(r.getLeft().getRuleName())) {
					Set<String> set = map.get(r.getLeft().getRuleName());
					set.add(r.getRight().getRuleName());
				} else {
					Set<String> set = new HashSet<String>();
					set.add(r.getRight().getRuleName());
					map.put(r.getLeft().getRuleName(), set);
				}
			}
		}

		return map;
	}

	/**
	 * Returns true if map contains redundancy where key is redundant to value.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 * @return True if redundancy exists.
	 */
	private static boolean mapContains(Map<String, Set<Redundancy>> map,
			String key, Object value) {
		if (map.containsKey(key)) {
			Set<Redundancy> set = map.get(key);
			boolean exists = set.remove(value);

			// If set is empty remove key from map.
			if (set.isEmpty()) {
				map.remove(key);
			}
			return exists;
		}
		return false;
	}
}
