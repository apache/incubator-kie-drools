/*
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
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.constraint.BetaConstraint;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class DoubleBetaConstraintsTest extends BaseBetaConstraintsTest {

    @ParameterizedTest(name = "useLambdaConstraint={0}")
    @MethodSource("parameters")
    public void testAllNoneIndexed(boolean useLambdaConstraint) {
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
    @ParameterizedTest(name = "useLambdaConstraint={0}")
    @MethodSource("parameters")
    public void testOneIndexed(boolean useLambdaConstraint) {
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
    @MethodSource("parameters")
    public void testOneIndexedForComparison(boolean useLambdaConstraint) {
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.GREATER.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );

        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.GREATER.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class, NodeTypeEnums.ExistsNode );
    }

    @ParameterizedTest(name = "useLambdaConstraint={0}")
    @MethodSource("parameters")
    public void testTwoIndexed(boolean useLambdaConstraint) {
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DoubleBetaConstraints.class );
    }
    
}
