package org.drools.analytics;

import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.OperatorDescr;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
public class RuleSolver extends Solver {

	private AnalyticsRule rule;

	public RuleSolver(AnalyticsRule rule) {
		super(OperatorDescr.Type.OR);
		this.rule = (AnalyticsRule) rule;
	}

	public AnalyticsRule getRule() {
		return rule;
	}
}
