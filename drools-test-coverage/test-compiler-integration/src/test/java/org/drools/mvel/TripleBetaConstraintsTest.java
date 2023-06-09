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
import org.drools.core.common.TripleBetaConstraints;
import org.drools.base.rule.constraint.BetaConstraint;
import org.junit.Test;

public class TripleBetaConstraintsTest extends BaseBetaConstraintsTest {

    public TripleBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testNoneIndxed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
    
    @Test
    public void testOneIndxed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }

    @Test
    public void testTwoIndxed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint( "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint( "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint( "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
    
    @Test
    public void testThreeIndxed() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3 };
        checkBetaConstraints( constraints, TripleBetaConstraints.class );
    }
}
