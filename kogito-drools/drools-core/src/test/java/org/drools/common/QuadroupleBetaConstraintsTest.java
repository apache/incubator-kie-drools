/**
 * 
 */
package org.drools.common;

import org.drools.Cheese;
import org.drools.base.evaluators.Operator;
import org.drools.rule.VariableConstraint;

/**
 * @author etirelli
 *
 */
public class QuadroupleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public void testNoneIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4  };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }   
    
    public void testOneIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );          
    }   

    public void testTwoIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );    
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );   
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );        
    }    

    public void testThreeIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );           
    }      
    
    public void testFourIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );         
    }     

}
