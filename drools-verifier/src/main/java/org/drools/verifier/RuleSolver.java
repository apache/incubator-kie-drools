package org.drools.verifier;

import org.drools.verifier.components.AnalyticsRule;
import org.drools.verifier.components.OperatorDescr;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
class RuleSolver extends Solver {

	private AnalyticsRule rule;

	public RuleSolver(AnalyticsRule rule) {
		super(OperatorDescr.Type.OR);
		this.rule = (AnalyticsRule) rule;
	}

	public AnalyticsRule getRule() {
		return rule;
	}
}
