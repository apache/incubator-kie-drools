/*
 * Copyright 2010 JBoss Inc
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

package org.drools.common;

import org.drools.Cheese;
import org.drools.base.evaluators.Operator;
import org.drools.rule.VariableConstraint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SingleBetaConstraintsTest extends BaseBetaConstraintsTest {
    
    @Test
    public void testIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.EQUAL, "type", Cheese.class );
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }

    @Test
    public void testNotIndxed() {
        VariableConstraint constraint0 = ( VariableConstraint ) getConstraint( "cheeseType0", Operator.NOT_EQUAL, "type", Cheese.class );                
        VariableConstraint[] constraints = new VariableConstraint[] { constraint0 };
        checkBetaConstraints( constraints, SingleBetaConstraints.class );
    }    
            
}
