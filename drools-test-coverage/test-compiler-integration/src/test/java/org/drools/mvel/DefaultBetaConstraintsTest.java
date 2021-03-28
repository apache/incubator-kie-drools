/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import org.drools.core.base.evaluators.Operator;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.junit.Test;

public class DefaultBetaConstraintsTest extends BaseBetaConstraintsTest {

    public DefaultBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testNoIndexConstraints() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType4", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint5 = getCheeseTypeConstraint( "cheeseType5", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3,constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaNodeFieldConstraint constraint6 = getCheeseTypeConstraint( "cheeseType6", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testIndexedConstraint() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType4", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint5 = getCheeseTypeConstraint( "cheeseType5", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaNodeFieldConstraint constraint6 = getCheeseTypeConstraint( "cheeseType6", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testSingleIndex() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.NOT_EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testSingleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndex() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.NOT_EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testTripleIndex() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testTripleIndexNotFirst() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        BetaNodeFieldConstraint constraint2 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType4", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType5", Operator.EQUAL );
        
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }

}
