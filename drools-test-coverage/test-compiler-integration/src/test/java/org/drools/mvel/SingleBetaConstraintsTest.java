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

import org.drools.drl.parser.impl.Operator;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.junit.Test;

public class SingleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public SingleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testIndexed() {
        BetaNodeFieldConstraint constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        BetaNodeFieldConstraint [] constraints = new BetaNodeFieldConstraint [] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    @Test
    public void testNotIndexed() {
        BetaNodeFieldConstraint  constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        BetaNodeFieldConstraint [] constraints = new BetaNodeFieldConstraint [] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    @Test
    public void testIndexedForComparison() {
        BetaNodeFieldConstraint  constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.LESS.getOperator() );
        BetaNodeFieldConstraint [] constraints = new BetaNodeFieldConstraint [] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class, NodeTypeEnums.ExistsNode );
    }

    @Test
    public void testNotIndexedForComparison() {
        BetaNodeFieldConstraint  constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.LESS.getOperator() );
        BetaNodeFieldConstraint [] constraints = new BetaNodeFieldConstraint [] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class, NodeTypeEnums.JoinNode );
    }
}
