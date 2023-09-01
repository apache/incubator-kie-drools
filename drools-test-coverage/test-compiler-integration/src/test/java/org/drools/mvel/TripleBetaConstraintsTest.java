package org.drools.mvel;

import org.drools.drl.parser.impl.Operator;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.junit.Test;

public class TripleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public TripleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testNoneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }

    @Test
    public void testTwoIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
    
    @Test
    public void testThreeIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
}
