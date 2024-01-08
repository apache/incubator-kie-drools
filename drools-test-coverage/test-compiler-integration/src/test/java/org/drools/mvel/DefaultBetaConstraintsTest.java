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
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.base.rule.constraint.BetaConstraint;
import org.junit.Test;

public class DefaultBetaConstraintsTest extends BaseBetaConstraintsTest {

    public DefaultBetaConstraintsTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void testNoIndexConstraints() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint5 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );

        BetaConstraint constraint6 = getCheeseTypeConstraint("cheeseType6", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testIndexedConstraint() {
        BetaConstraint   constraint0 = getCheeseTypeConstraint("cheeseType0", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint5 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4, constraint5 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
        
        BetaConstraint constraint6 = getCheeseTypeConstraint("cheeseType6", Operator.BuiltInOperator.EQUAL.getOperator());
        constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4, constraint5, constraint6 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testSingleIndex() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testSingleIndexNotFirst() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndex() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testDoubleIndexNotFirst() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    
    @Test
    public void testTripleIndex() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }
    
    @Test
    public void testTripleIndexNotFirst() {
        BetaConstraint constraint0 = getCheeseTypeConstraint("cheeseType1", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint1 = getCheeseTypeConstraint("cheeseType2", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint2 = getCheeseTypeConstraint("cheeseType3", Operator.BuiltInOperator.NOT_EQUAL.getOperator());
        BetaConstraint constraint3 = getCheeseTypeConstraint("cheeseType4", Operator.BuiltInOperator.EQUAL.getOperator());
        BetaConstraint constraint4 = getCheeseTypeConstraint("cheeseType5", Operator.BuiltInOperator.EQUAL.getOperator());
        
        BetaConstraint[] constraints = new BetaConstraint[] {constraint0, constraint1, constraint2, constraint3, constraint4 };
        
        checkBetaConstraints( constraints, DefaultBetaConstraints.class );
    }

}
