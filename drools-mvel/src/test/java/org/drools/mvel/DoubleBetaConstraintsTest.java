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
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.test.model.Cheese;

import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.junit.Test;

public class DoubleBetaConstraintsTest extends BaseBetaConstraintsTest {

    @Test
    public void testAllNoneIndexed() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndexed() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
        
        constraint0 = getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }

    @Test
    public void testOneIndexedForComparison() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.GREATER, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.NOT_EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );

        constraint0 = getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );
        constraint1 = getConstraint( "cheeseType1", Operator.GREATER, "type", Cheese.class );
        constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );
    }

    @Test
    public void testTwoIndexed() {
        BetaNodeFieldConstraint constraint0 = getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint constraint1 = getConstraint( "cheeseType1", Operator.EQUAL, "type", Cheese.class );
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[] { constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
}
