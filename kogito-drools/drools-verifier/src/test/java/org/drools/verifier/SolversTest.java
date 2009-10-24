package org.drools.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.solver.Solvers;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class SolversTest extends TestCase {

	/**
	 * <pre>
	 * when 
	 * 		Foo( r &amp;&amp; r2 )
	 * 		and
	 * 		not Foo( r3 &amp;&amp; r4 )
	 * </pre>
	 * 
	 * result:<br>
	 * r && r2<br>
	 * r3 && r4
	 */
	public void testNotAnd() {
		VerifierRule rule = new VerifierRule();
		Pattern pattern = new Pattern();

		Restriction r = new LiteralRestriction();
		Restriction r2 = new LiteralRestriction();
		Restriction r3 = new LiteralRestriction();
		Restriction r4 = new LiteralRestriction();

		OperatorDescr andDescr = new OperatorDescr(OperatorDescr.Type.AND);
		Solvers solvers = new Solvers();

		solvers.startRuleSolver(rule);

		solvers.startOperator(OperatorDescr.Type.AND);
		solvers.startPatternSolver(pattern);
		solvers.startOperator(OperatorDescr.Type.AND);
		solvers.addRestriction(r);
		solvers.addRestriction(r2);
		solvers.endOperator();
		solvers.endPatternSolver();

		solvers.startNot();
		solvers.startPatternSolver(pattern);
		solvers.startOperator(OperatorDescr.Type.AND);
		solvers.addRestriction(r3);
		solvers.addRestriction(r4);
		solvers.endOperator();
		solvers.endPatternSolver();
		solvers.endNot();

		solvers.endOperator();

		solvers.endRuleSolver();

		List<SubRule> list = solvers.getRulePossibilities();
		assertEquals(1, list.size());
		assertEquals(2, list.get(0).getItems().size());

		List<Restriction> result = new ArrayList<Restriction>();
		result.add(r);
		result.add(r2);

		List<Restriction> result2 = new ArrayList<Restriction>();
		result2.add(r3);
		result2.add(r4);

		Object[] possibilies = list.get(0).getItems().toArray();
		SubPattern p1 = (SubPattern) possibilies[0];
		SubPattern p2 = (SubPattern) possibilies[1];

		/*
		 * Order may change but it doesn't matter.
		 */
		if (p1.getItems().containsAll(result)) {
			assertTrue(p2.getItems().containsAll(result2));
		} else if (p1.getItems().containsAll(result2)) {
			assertTrue(p2.getItems().containsAll(result));
		} else {
			fail("No items found.");
		}
	}

	/**
	 * <pre>
	 * when 
	 * 		Foo( descr &amp;&amp; descr2 )
	 * </pre>
	 * 
	 * result:<br>
	 * descr && descr2
	 */
	public void testBasicAnd() {
		VerifierRule rule = new VerifierRule();
		Pattern pattern = new Pattern();

		Restriction r = new LiteralRestriction();
		Restriction r2 = new LiteralRestriction();

		OperatorDescr andDescr = new OperatorDescr(OperatorDescr.Type.AND);
		Solvers solvers = new Solvers();

		solvers.startRuleSolver(rule);
		solvers.startPatternSolver(pattern);
		solvers.startOperator(OperatorDescr.Type.AND);
		solvers.addRestriction(r);
		solvers.addRestriction(r2);
		solvers.endOperator();
		solvers.endPatternSolver();
		solvers.endRuleSolver();

		List<SubRule> list = solvers.getRulePossibilities();
		assertEquals(1, list.size());
		assertEquals(1, list.get(0).getItems().size());

		List<Restriction> result = new ArrayList<Restriction>();
		result.add(r);
		result.add(r2);

		Set<Cause> set = list.get(0).getItems();
		for (Cause cause : set) {
			SubPattern possibility = (SubPattern) cause;
			assertTrue(possibility.getItems().containsAll(result));
		}
	}
}
