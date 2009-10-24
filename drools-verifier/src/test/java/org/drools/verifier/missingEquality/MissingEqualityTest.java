package org.drools.verifier.missingEquality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

public class MissingEqualityTest extends TestBase {

	public void testMissingEqualityInLiteralRestrictions() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("MissingEquality.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Missing restriction in LiteralRestrictions"));

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingEqualityTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Cause cause = ((VerifierMessage) o).getFaulty();
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

		VerifierReport result = VerifierReportFactory.newVerifierReport();
		Collection<? extends Object> testData = getTestData(this.getClass()
				.getResourceAsStream("MissingEqualityTest.drl"), result
				.getVerifierData());

		session.setGlobal("result", result);

		session.executeWithResults(testData);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		Collection<String> ruleNames = new ArrayList<String>();
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				Cause cause = ((VerifierMessage) o).getFaulty();
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
