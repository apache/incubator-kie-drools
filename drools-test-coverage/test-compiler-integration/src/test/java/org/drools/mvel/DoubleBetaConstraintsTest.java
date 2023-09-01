package org.drools.mvel;

import org.drools.drl.parser.impl.Operator;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.junit.Test;

public class DoubleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public DoubleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testAllNoneIndexed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndexed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }

    @Test
    public void testOneIndexedForComparison() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.GREATER.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );

        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.GREATER.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );
    }

    @Test
    public void testTwoIndexed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
}
