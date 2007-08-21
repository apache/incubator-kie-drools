package org.drools.common;

import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;
import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.reteoo.BetaMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.util.FactHandleIndexHashTable;
import org.drools.util.FactHashTable;
import org.drools.util.TupleHashTable;
import org.drools.util.TupleIndexHashTable;
import org.drools.util.AbstractHashTable.FieldIndex;
import org.drools.util.AbstractHashTable.Index;

import junit.framework.TestCase;

public class TripleBetaConstraintsTest extends BaseBetaConstraintsTest {
    

    public void testNoneIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }   
    
    public void testOneIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );         
    }   

    public void testTwoIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );  
        
        constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );               
    }    
    
    public void testThreeIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
}
