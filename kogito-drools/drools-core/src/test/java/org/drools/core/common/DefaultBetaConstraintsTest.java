/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.base.evaluators.Operator;
import org.drools.core.test.model.Cheese;

import org.drools.core.spi.BetaNodeFieldConstraint;
import org.junit.Test;

public class DefaultBetaConstraintsTest extends BaseBetaConstraintsTest {
    
    @Test
    public void testNoIndexConstraints() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint5 = getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3,constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint6 = getConstraint( "cheeseType6", Operator.NOT_EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testIndexedConstraint() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType3", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint5 = getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint6 = getConstraint( "cheeseType6", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testSingleIndex() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testSingleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndex() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.NOT_EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testTripleIndex() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testTripleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType2", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint2 = getConstraint( "cheeseType3", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint3 = getConstraint( "cheeseType4", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint4 = getConstraint( "cheeseType5", Operator.EQUAL, "type", Cheese.class );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }

}
