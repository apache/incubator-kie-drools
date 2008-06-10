package org.drools.verifier;

import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
class PatternSolver extends Solver {

	private Pattern pattern;

	public PatternSolver(Pattern pattern) {
		super(OperatorDescr.Type.OR);
		this.pattern = pattern;
	}

	public Pattern getPattern() {
		return pattern;
	}
}
