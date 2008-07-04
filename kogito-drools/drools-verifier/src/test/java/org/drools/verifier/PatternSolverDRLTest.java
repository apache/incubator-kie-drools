package org.drools.verifier;

import java.io.ByteArrayInputStream;
import java.util.Collection;

import org.drools.verifier.components.PatternPossibility;
import org.drools.verifier.dao.VerifierResult;
import org.drools.verifier.dao.VerifierResultFactory;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class PatternSolverDRLTest extends TestBase {

	public void testOrInsidePattern() throws Exception {

		StringBuffer rule = new StringBuffer();
		rule.append("rule \"Test rule\" ");
		rule.append("   when ");
		rule.append("       customer : Customer( status > 30 && < 50 ) ");
		rule
				.append("       order : OrderHeader( customer == customer , orderPriority == 3 || == 4 ) ");
		rule.append("   then ");
		rule.append("       order.setOrderDiscount( 6.0 ); ");
		rule.append("end");

		VerifierResult result = VerifierResultFactory.createVerifierResult();
		Collection<? extends Object> testData = getTestData(
				new ByteArrayInputStream(rule.toString().getBytes()), result
						.getVerifierData());

		int patternCount = 0;

		// Check that there is three pattern possibilities and that they contain
		// the right amount of items.
		for (Object o : testData) {
			if (o instanceof PatternPossibility) {
				PatternPossibility pp = (PatternPossibility) o;
				if (pp.getItems().size() == 2) {

					patternCount++;
				}
			}
		}

		assertEquals(3, patternCount);
	}
}
