package org.drools.verifier.optimisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.AnalyticsComponent;
import org.drools.verifier.dao.AnalyticsResult;
import org.drools.verifier.dao.AnalyticsResultFactory;
import org.drools.verifier.report.components.AnalyticsMessage;
import org.drools.verifier.report.components.AnalyticsMessageBase;
import org.drools.verifier.report.components.Severity;

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
				Severity.NOTE).iterator();

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
