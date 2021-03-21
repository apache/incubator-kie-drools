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
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.junit.Test;

public class QuadroupleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public QuadroupleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testNoneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4  };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

    @Test
    public void testTwoIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        constraint1 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        constraint1 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraint4 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraint3 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraint3 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraint4 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = ( BetaNodeFieldConstraint ) getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

    @Test
    public void testThreeIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.NOT_EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.NOT_EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.NOT_EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.NOT_EQUAL );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
    @Test
    public void testFourIndxed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.EQUAL );
        BetaNodeFieldConstraint constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.EQUAL );
        BetaNodeFieldConstraint constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.EQUAL );
        BetaNodeFieldConstraint constraint4 = getCheeseTypeConstraint( "cheeseType3", Operator.EQUAL );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

}
