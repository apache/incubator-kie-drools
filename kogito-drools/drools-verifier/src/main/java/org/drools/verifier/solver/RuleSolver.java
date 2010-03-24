package org.drools.verifier.solver;

import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.VerifierRule;

/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
public class RuleSolver extends Solver {

    private VerifierRule rule;

    public RuleSolver(VerifierRule rule) {
        super( OperatorDescrType.OR );
        this.rule = (VerifierRule) rule;
    }

    public VerifierRule getRule() {
        return rule;
    }
}
