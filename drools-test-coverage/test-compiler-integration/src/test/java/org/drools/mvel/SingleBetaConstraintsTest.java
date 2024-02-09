/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import org.drools.drl.parser.impl.Operator;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.BetaConstraint;
import org.junit.Test;

public class SingleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public SingleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testIndexed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    @Test
    public void testNotIndexed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    @Test
    public void testIndexedForComparison() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.LESS.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class, NodeTypeEnums.ExistsNode );
    }

    @Test
    public void testNotIndexedForComparison() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.LESS.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class, NodeTypeEnums.JoinNode );
    }
}
