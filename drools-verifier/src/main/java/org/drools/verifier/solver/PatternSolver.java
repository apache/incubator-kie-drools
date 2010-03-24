package org.drools.verifier.solver;

import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
class PatternSolver extends Solver {

    private Pattern pattern;

    public PatternSolver(Pattern pattern) {
        super( OperatorDescrType.OR );
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
