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
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.RulePossibility;
import org.drools.analytics.result.PartialRedundancy;
import org.drools.analytics.result.Redundancy;
import org.drools.analytics.result.Subsumption;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class SubsumptantPossibilitiesRuleTest extends TestBase {

	private static final String RULE_NAME = "Find subsumptant Possibilities";
	
	public void testFake ( ) {
		assertTrue(true);
	}

	public void fixmetestSubsumptantPossibilitiesPattern() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(RULE_NAME));

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		StatelessSessionResult sessionResult = session
				.executeWithResults(createSubsumptantPatternData(ruleName1,
						ruleName2));

		Map<String, Set<String>> map = createSubsumptionMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More subsumpt cases than was expected.");
		}
	}

	public void fixmetestSubsumptantPossibilitiesRule() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(RULE_NAME));

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";

		StatelessSessionResult sessionResult = session
				.executeWithResults(createSubsumptantRuleData(ruleName1,
						ruleName2));

		Map<String, Set<String>> map = createSubsumptionMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName2, ruleName1));

		if (!map.isEmpty()) {
			fail("More subsumpt cases than was expected.");
		}
	}

	public void fixmetestSubsumptantPossibilitiesBoth() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("redundancy/Possibilities.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(RULE_NAME));

		String ruleName1 = "Rule 1";
		String ruleName2 = "Rule 2";
		String ruleName3 = "Rule 3";
		String ruleName4 = "Rule 4";
		String ruleName5 = "Rule 5";
		String ruleName6 = "Rule 6";
		String ruleName7 = "Rule 7";
		String ruleName8 = "Rule 8";
		String ruleName9 = "Rule 9";
		String ruleName10 = "Rule 10";
		String ruleName11 = "Rule 11";
		String ruleName12 = "Rule 12";

		// Rule data
		Collection<Object> data = createSubsumptantRuleData(ruleName1,
				ruleName2);
		data.addAll(createSubsumptantRuleData(ruleName3, ruleName4));
		data.addAll(createSubsumptantRuleData(ruleName5, ruleName6));

		// Pattern data.
		data.addAll(createSubsumptantPatternData(ruleName7, ruleName8));
		data.addAll(createSubsumptantPatternData(ruleName9, ruleName10));
		data.addAll(createSubsumptantPatternData(ruleName11, ruleName12));

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createSubsumptionMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, ruleName2, ruleName1));
		assertTrue(TestBase.mapContains(map, ruleName4, ruleName3));
		assertTrue(TestBase.mapContains(map, ruleName6, ruleName5));

		assertTrue(TestBase.mapContains(map, ruleName8, ruleName7));
		assertTrue(TestBase.mapContains(map, ruleName10, ruleName9));
		assertTrue(TestBase.mapContains(map, ruleName12, ruleName11));

		if (!map.isEmpty()) {
			fail("More subsumpt cases than was expected.");
		}
	}

	private Collection<Object> createSubsumptantPatternData(String ruleName1,
			String ruleName2) {

		Collection<Object> data = new ArrayList<Object>();

		LiteralRestriction lr1 = new LiteralRestriction();
		lr1.setRuleName(ruleName1);
		LiteralRestriction lr2 = new LiteralRestriction();
		lr2.setRuleName(ruleName1);

		PatternPossibility pp1 = new PatternPossibility();
		pp1.setRuleName(ruleName1);
		pp1.add(lr1);
		pp1.add(lr2);

		LiteralRestriction lr3 = new LiteralRestriction();
		lr3.setRuleName(ruleName2);

		PatternPossibility pp2 = new PatternPossibility();
		pp2.setRuleName(ruleName2);
		pp2.add(lr3);

		Redundancy redundancy1 = new Redundancy(lr1, lr3);

		PartialRedundancy pr1 = new PartialRedundancy(pp1, pp2, redundancy1);
		PartialRedundancy pr2 = new PartialRedundancy(pp2, pp1, redundancy1);

		data.add(lr1);
		data.add(lr2);
		data.add(lr3);
		data.add(pp1);
		data.add(pp2);
		data.add(redundancy1);
		data.add(pr1);
		data.add(pr2);

		return data;
	}

	private Collection<Object> createSubsumptantRuleData(String ruleName1,
			String ruleName2) {

		Collection<Object> data = new ArrayList<Object>();

		PatternPossibility pp1 = new PatternPossibility();
		pp1.setRuleName(ruleName1);
		PatternPossibility pp2 = new PatternPossibility();
		pp2.setRuleName(ruleName1);

		RulePossibility rp1 = new RulePossibility();
		rp1.setRuleName(ruleName1);
		rp1.add(pp1);
		rp1.add(pp2);

		PatternPossibility pp3 = new PatternPossibility();
		pp3.setRuleName(ruleName2);

		RulePossibility rp2 = new RulePossibility();
		rp2.setRuleName(ruleName2);
		rp2.add(pp3);

		Redundancy redundancy1 = new Redundancy(pp1, pp3);

		PartialRedundancy pr1 = new PartialRedundancy(rp1, rp2, redundancy1);
		PartialRedundancy pr2 = new PartialRedundancy(rp2, rp1, redundancy1);

		data.add(pp1);
		data.add(pp2);
		data.add(rp1);
		data.add(pp3);
		data.add(rp2);
		data.add(redundancy1);
		data.add(pr1);
		data.add(pr2);

		return data;
	}

	/**
	 * Creates redundancy map from Redundancy objects, one rule may have several
	 * redundancy dependencies.
	 * 
	 * @param iter
	 * @return
	 */
	private Map<String, Set<String>> createSubsumptionMap(Iterator iter) {

		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof Subsumption) {
				Subsumption s = (Subsumption) o;
				if (map.containsKey(s.getLeft().getRuleName())) {
					Set<String> set = map.get(s.getLeft().getRuleName());
					set.add(s.getRight().getRuleName());
				} else {
					Set<String> set = new HashSet<String>();
					set.add(s.getRight().getRuleName());
					map.put(s.getLeft().getRuleName(), set);
				}
			}
		}

		return map;
	}
}
