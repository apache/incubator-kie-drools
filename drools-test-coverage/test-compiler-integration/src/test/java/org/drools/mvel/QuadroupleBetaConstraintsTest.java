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
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.base.rule.constraint.BetaConstraint;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class QuadroupleBetaConstraintsTest extends BaseBetaConstraintsTest {

	@ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testNoneIndxed(boolean useLambdaConstraint) { 
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4  };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
	@ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testOneIndxed(boolean useLambdaConstraint) { 
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

	@ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testTwoIndxed(boolean useLambdaConstraint) { 
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint   constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

	@ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testThreeIndxed(boolean useLambdaConstraint) { 
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
        
        constraint0 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator() );
        constraint1 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint3 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraint4 = getCheeseTypeConstraint(useLambdaConstraint,  "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator() );
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }
    
	@ParameterizedTest(name = "useLambdaConstraint={0}")
	@MethodSource("parameters")
    public void testFourIndxed(boolean useLambdaConstraint) { 
        BetaConstraint   constraint0 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint1 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint3 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint   constraint4 = getCheeseTypeConstraint(useLambdaConstraint, "cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint3, constraint4 };
        checkBetaConstraints( constraints, QuadroupleBetaConstraints.class );
    }

}
