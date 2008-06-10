package org.drools.verifier.alwaysFalse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBase;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierComponent;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;
import org.drools.verifier.report.components.Incompatibility;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class AlwaysFalsePatternsTest extends TestBase {

//	public void testPatternsAlwaysFalse() throws Exception {
//		StatelessSession session = getStatelessSession(this.getClass()
//				.getResourceAsStream("Patterns.drl"));
//
//		session.setAgendaFilter(new RuleNameMatchesAgendaFilter(
//				"Pattern that is always false"));
//
//		VerifierResult result = VerifierResultFactory.createVerifierResult();
//		Collection<Object> data = new ArrayList<Object>();
//
//		session.setGlobal("result", result);
//
//		final String ruleName1 = "rule 1";
//		Pattern pattern = new Pattern();
//
//		Restriction r1 = new LiteralRestriction();
//		Restriction r2 = new LiteralRestriction();
//		Incompatibility i1 = new Incompatibility(r1, r2);
//		PatternPossibility pp1 = new PatternPossibility();
//		pp1.add(r1);
//		pp1.add(r2);
//
//		Restriction r3 = new VariableRestriction();
//		Restriction r4 = new VariableRestriction();
//		Incompatibility i2 = new Incompatibility(r1, r2);
//		PatternPossibility pp2 = new PatternPossibility();
//		pp2.add(r1);
//		pp2.add(r2);
//
//		data.add(pattern);
//		data.add(r1);
//		data.add(r2);
//		data.add(r3);
//		data.add(r4);
//		data.add(i1);
//		data.add(i2);
//		data.add(pp1);
//		data.add(pp2);
//
//		StatelessSessionResult sessionResult = session.executeWithResults(data);
//
//		Iterator<VerifierMessageBase> iter = result
//				.getBySeverity(Severity.NOTE).iterator();
//
//		Collection<String> ruleNames = new ArrayList<String>();
//		while (iter.hasNext()) {
//			Object o = (Object) iter.next();
//			if (o instanceof VerifierMessage) {
//				String name = ((VerifierMessage) o).getCauses().toArray(
//						new VerifierComponent[2])[0].getRuleName();
//
//				ruleNames.add(name);
//			}
//		}
//
//		assertTrue(ruleNames.remove(ruleName1));
//		// assertFalse(ruleNames.remove(ruleName2));
//
//		if (!ruleNames.isEmpty()) {
//			for (String string : ruleNames) {
//				fail("Rule " + string + " caused an error.");
//			}
//		}
//	}
}