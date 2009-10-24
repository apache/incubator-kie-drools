package org.drools.verifier.solver;

import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.solver.Solver;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
public class RuleSolver extends Solver {

    private VerifierRule rule;

    public RuleSolver(VerifierRule rule) {
        super( OperatorDescr.Type.OR );
        this.rule = (VerifierRule) rule;
    }

    public VerifierRule getRule() {
        return rule;
    }
}
