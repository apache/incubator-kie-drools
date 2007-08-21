package org.drools.common;

import org.drools.Cheese;
import org.drools.base.evaluators.Operator;
import org.drools.rule.VariableConstraint;

public class SingleBetaConstraintsTest extends BaseBetaConstraintsTest {
    
    public void testIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    public void testNotIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );                
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }    
            
}
