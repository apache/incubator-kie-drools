package org.drools.mvel;

import org.drools.drl.parser.impl.Operator;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.junit.Test;

public class QuadroupleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public QuadroupleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;

    }

    @Test
    public void testNoneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4  };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

    @Test
    public void testTwoIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

    @Test
    public void testThreeIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
    @Test
    public void testFourIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

}
