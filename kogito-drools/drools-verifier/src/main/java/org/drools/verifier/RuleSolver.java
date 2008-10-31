package org.drools.verifier;

import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.VerifierRule;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
class RuleSolver extends Solver {

	private VerifierRule rule;

	public RuleSolver(VerifierRule rule) {
		super(OperatorDescr.Type.OR);
		this.rule = (VerifierRule) rule;
	}

	public VerifierRule getRule() {
		return rule;
	}
}
