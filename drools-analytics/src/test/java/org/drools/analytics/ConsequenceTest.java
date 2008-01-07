package org.drools.analytics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Severity;
import org.drools.base.RuleNameMatchesAgendaFilter;

/**
 *
 * @author Toni Rikkola
 *
 */
public class ConsequenceTest extends TestBase {

	public void testMissingConsequence() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Consequence.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"No action - possibly commented out"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();

		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("ConsequenceTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Set<String> rulesThatHadErrors = new HashSet<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				AnalyticsRule rule = (AnalyticsRule) ((AnalyticsMessage) o)
						.getFaulty();
				rulesThatHadErrors.add(rule.getRuleName());
			}
		}

		assertFalse(rulesThatHadErrors.contains("Has a consequence 1"));
		assertTrue(rulesThatHadErrors.remove("Missing consequence 1"));
		assertTrue(rulesThatHadErrors.remove("Missing consequence 2"));

		if (!rulesThatHadErrors.isEmpty()) {
			for (String string : rulesThatHadErrors) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
