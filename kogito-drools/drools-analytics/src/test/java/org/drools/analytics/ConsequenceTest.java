package org.drools.analytics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.dao.AnalyticsDataFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Gap;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class ConsequenceTest extends TestBase {

	public void testMissingConsiquence() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Consequence.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"No action - possibly commented out"));

		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("ConsequenceTest.drl"));

		AnalyticsResult result = AnalyticsDataFactory.getAnalyticsResult();
		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
			// System.out.println(o);
		}

		assertFalse(rulesThatHadErrors.contains("Has a consequence 1"));
		assertFalse(rulesThatHadErrors.contains("Has a consequence 2"));
		assertTrue(rulesThatHadErrors.remove("Missing consequence 1"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
