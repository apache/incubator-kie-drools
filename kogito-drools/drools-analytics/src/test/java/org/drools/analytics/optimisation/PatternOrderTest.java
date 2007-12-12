package org.drools.analytics.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.analytics.TestBase;
import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.dao.AnalyticsResultFactory;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessage;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.base.RuleNameMatchesAgendaFilter;

public class PatternOrderTest extends TestBase {

	public void testEvalOrderInsideOperator() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("PatternOrder.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Optimise evals inside pattern"));

		AnalyticsResult result = AnalyticsResultFactory.createAnalyticsResult();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("OptimisationPatternOrderTest.drl"),
				result.getAnalyticsData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<AnalyticsMessageBase> iter = result.getBySeverity(
				AnalyticsMessageBase.Severity.NOTE).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AnalyticsMessage) {
				String name = ((AnalyticsMessage) o).getCauses().toArray(
						new AnalyticsComponent[2])[0].getRuleName();

				ruleNames.add(name);
			}
		}

		assertTrue(ruleNames.remove("Wrong eval order 1"));

		if (!ruleNames.isEmpty()) {
			for (String string : ruleNames) {
				fail("Rule " + string + " caused an error.");
			}
		}
	}
}
