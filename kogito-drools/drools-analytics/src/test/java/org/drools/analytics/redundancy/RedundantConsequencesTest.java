package org.drools.analytics.redundancy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.TestBase;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class RedundantConsequencesTest extends RedundancyTestBase {

	public void testRedundantTextConsequences() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Consequence.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant TextConsequences"));

		AnalyticsDataFactory.clearAnalyticsData();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("ConsequenceRedundancyTest.drl"));

		AnalyticsDataFactory.clearAnalyticsResult();
		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		StatelessSessionResult sessionResult = session.executeWithResults(data);

		Map<String, Set<String>> map = createRedundancyMap(sessionResult
				.iterateObjects());

		assertTrue(TestBase.mapContains(map, "Redundant consiquence 1a",
				"Redundant consiquence 1b")
				^ TestBase.mapContains(map, "Redundant consiquence 1b",
						"Redundant consiquence 1a"));

		assertTrue(TestBase.mapContains(map, "Redundant consiquence 2a",
				"Redundant consiquence 2b")
				^ TestBase.mapContains(map, "Redundant consiquence 2b",
						"Redundant consiquence 2a"));

		assertTrue(TestBase.mapContains(map, "Redundant consiquence 3a",
				"Redundant consiquence 3b")
				^ TestBase.mapContains(map, "Redundant consiquence 3b",
						"Redundant consiquence 3a"));

		if (!map.isEmpty()) {
			fail("More redundancies than was expected.");
		}
	}

}
