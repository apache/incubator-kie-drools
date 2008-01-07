package org.drools.analytics.missingEquality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.LiteralRestriction;
import org.drools.analytics.components.VariableRestriction;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Cause;
import org.drools.analytics.report.components.Severity;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class MissingEqualityTest extends TestBase {

	public void testMissingEqualityInLiteralRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("MissingEquality.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Missing restriction in LiteralRestrictions"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingEqualityTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Cause cause = ((AnalyticsMessage) o).getFaulty();
				String name = ((LiteralRestriction) cause).getRuleName();

				ruleNames.add(name);
			}
		}

		assertTrue(ruleNames.remove("Missing equality 1"));
		assertTrue(ruleNames.remove("Missing equality 2"));

		if (!ruleNames.isEmpty()) {
			for (String string : ruleNames) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}

	public void testMissingEqualityInVariableRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("MissingEquality.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Missing restriction in VariableRestrictions"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingEqualityTest.drl"), result
				.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				Cause cause = ((AnalyticsMessage) o).getFaulty();
				String name = ((VariableRestriction) cause).getRuleName();

				ruleNames.add(name);
			}
		}

		assertTrue(ruleNames.remove("Missing equality 3"));
		assertTrue(ruleNames.remove("Missing equality 4"));
		assertTrue(ruleNames.remove("Missing equality 5"));
		assertTrue(ruleNames.remove("Missing equality 6"));

		if (!ruleNames.isEmpty()) {
			for (String string : ruleNames) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
