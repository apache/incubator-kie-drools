package org.drools.verifier.redundancy;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;

public class RedundantRestrictionsTest extends RedundancyTestBase {

	public void testVerifierLiteralRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant LiteralRestriction"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("RedundancyLiteralRestrictionTest.drl"),
				result.getVerifierData());

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

	public void testVerifierVariableRestrictionRedundancy() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Restrictions.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Find redundant VariableRestriction"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> data = getTestData(this.getClass()
				.getResourceAsStream("SubsumptionVariableRestrictionTest.drl"),
				result.getVerifierData());

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
