/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.common;

import org.drools.Cheese;
import org.drools.base.evaluators.Operator;
import org.drools.rule.VariableConstraint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultBetaConstraintsTest extends BaseBetaConstraintsTest {
    
    @Test
    public void testNoIndexConstraints() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );        
        
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 }; 
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint5 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3,constraint5 };   
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );     
        
        VariableConstraint constraint6 = ( VariableConstraint ) getConstraint( "cheeseType6", Operator.NOT_EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };   
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );             
    }    
    
    @Test
    public void testIndexedConstraint() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );        
        
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2 };        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 }; 
        checkBetaConstraints( constraints, DefaultBetaConstraints.class ); 
        
        VariableConstraint constraint5 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5 };   
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );     
        
        VariableConstraint constraint6 = ( VariableConstraint ) getConstraint( "cheeseType6", Operator.EQUAL, "type", Cheese.class );        
        constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };   
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );          
    }        
    
    
    @Test
    public void testSingleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );    
    }   
    
    @Test
    public void testSingleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );         
    }    
    
    @Test
    public void testDoubleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );       
    }   
    
    @Test
    public void testDoubleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );       
    }       
    
    
    @Test
    public void testTripleIndex() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );               
    }      
    
    @Test
    public void testTripleIndexNotFirst() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint1 = ( VariableConstraint ) getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint2 = ( VariableConstraint ) getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        VariableConstraint constraint3 = ( VariableConstraint ) getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint constraint4 = ( VariableConstraint ) getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );               
    }     

}
