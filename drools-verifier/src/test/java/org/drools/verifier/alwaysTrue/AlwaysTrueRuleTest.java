package org.drools.verifier.alwaysTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.RulePossibility;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.components.AlwaysTrue;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class AlwaysTrueRuleTest extends TestBase {

	public void testPatternPossibilities() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Rules.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Rule possibility that is always true"));

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<Object> data = new ArrayList<Object>();

		session.setGlobal("result", result);

		// This rule is always true.
		VerifierRule rule1 = new VerifierRule();

		RulePossibility rp1 = new RulePossibility();
		PatternPossibility pp1 = new PatternPossibility();
		pp1.setRuleId(rule1.getId());
		AlwaysTrue alwaysTrue1 = new AlwaysTrue(pp1);
		PatternPossibility pp2 = new PatternPossibility();
		pp2.setRuleId(rule1.getId());
		AlwaysTrue alwaysTrue2 = new AlwaysTrue(pp2);

		rp1.add(pp1);
		rp1.add(pp2);

		// This rule is okay.
		VerifierRule rule2 = new VerifierRule();

		RulePossibility rp2 = new RulePossibility();
		PatternPossibility pp3 = new PatternPossibility();
		pp3.setRuleId(rule2.getId());
		PatternPossibility pp4 = new PatternPossibility();
		pp4.setRuleId(rule2.getId());
		AlwaysTrue alwaysTrue4 = new AlwaysTrue(pp4);

		rp2.add(pp3);
		rp2.add(pp4);

		data.add(rule1);
		data.add(rp1);
		data.add(pp1);
		data.add(pp2);
		data.add(alwaysTrue1);
		data.add(alwaysTrue2);

		data.add(rule2);
		data.add(rp2);
		data.add(pp3);
		data.add(pp4);
		data.add(alwaysTrue4);

		StatelessSessionResult sessionResult = session.executeWithResults(data);
		Iterator iter = sessionResult.iterateObjects();

		boolean rp1true = false;
		boolean rp2true = false;
		boolean rp3true = false;
		boolean rp4true = false;
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof AlwaysTrue) {
				AlwaysTrue alwaysTrue = (AlwaysTrue) o;
				if (!rp1true) {
					rp1true = alwaysTrue.getCause().equals(pp1);
				}
				if (!rp2true) {
					rp2true = alwaysTrue.getCause().equals(pp2);
				}
				if (!rp3true) {
					rp3true = alwaysTrue.getCause().equals(pp3);
				}
				if (!rp4true) {
					rp4true = alwaysTrue.getCause().equals(pp4);
				}
			}
		}

		assertTrue(rp1true);
		assertTrue(rp2true);
		assertFalse(rp3true);
		assertTrue(rp4true);
	}

	public void testPatterns() throws Exception {
		StatelessSession session = getStatelessSession(this.getClass()
				.getResourceAsStream("Rules.drl"));

		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
				"Rule that is always true"));

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<Object> data = new ArrayList<Object>();

		session.setGlobal("result", result);

		// This rule is always true.
		VerifierRule rule1 = new VerifierRule();

		RulePossibility rp1 = new RulePossibility();
		rp1.setRuleId(rule1.getId());
		AlwaysTrue alwaysTrue1 = new AlwaysTrue(rp1);

		RulePossibility rp2 = new RulePossibility();
		rp2.setRuleId(rule1.getId());
		AlwaysTrue alwaysTrue2 = new AlwaysTrue(rp2);

		// This rule is okay.
		VerifierRule rule2 = new VerifierRule();

		RulePossibility rp3 = new RulePossibility();
		rp3.setRuleId(rule2.getId());

		RulePossibility rp4 = new RulePossibility();
		rp4.setRuleId(rule2.getId());
		AlwaysTrue alwaysTrue4 = new AlwaysTrue(rp4);

		data.add(rule1);
		data.add(rp1);
		data.add(rp2);
		data.add(alwaysTrue1);
		data.add(alwaysTrue2);

		data.add(rule2);
		data.add(rp3);
		data.add(rp4);
		data.add(alwaysTrue4);

		session.executeWithResults(data);

		Iterator<VerifierMessageBase> iter = result.getBySeverity(
				Severity.WARNING).iterator();

		boolean works = false;
		while (iter.hasNext()) {
			Object o = (Object) iter.next();
			if (o instanceof VerifierMessage) {
				VerifierMessage message = (VerifierMessage) o;
				if (message.getFaulty().equals(rule1)) {
					works = true;
				} else {
					fail("There can be only one. (And this is not the one)");
				}
			}
		}

		assertEquals(0, result.getBySeverity(Severity.ERROR).size());
		assertEquals(1, result.getBySeverity(Severity.WARNING).size());
		assertEquals(0, result.getBySeverity(Severity.NOTE).size());
		assertTrue(works);
	}
}