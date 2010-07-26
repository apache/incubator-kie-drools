/**
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

package org.drools.base;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.drools.RuntimeDroolsException;
import org.drools.base.evaluators.AfterEvaluatorDefinition;
import org.drools.base.evaluators.BeforeEvaluatorDefinition;
import org.drools.base.evaluators.CoincidesEvaluatorDefinition;
import org.drools.base.evaluators.DuringEvaluatorDefinition;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.EvaluatorRegistry;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.VariableRestriction.BooleanVariableContextEntry;
import org.drools.rule.VariableRestriction.CharVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

/**
 * Test coverage for the temporal evaluators.
 *
 * @author Tino Breddin
 */
public class TemporalEvaluatorFactoryTest extends TestCase {

    private EvaluatorRegistry registry = new EvaluatorRegistry();

    public void testAfter() {
        registry.addEvaluatorDefinition( AfterEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   1,
                                                   2 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   4,
                                                   3 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     5,
                                                     2 );

        final Object[][] data = {
                {drool, "after", foo, Boolean.TRUE}, 
                {drool, "after", bar, Boolean.FALSE}, 
                {bar, "after", foo, Boolean.TRUE}, 
                {bar, "after", drool, Boolean.FALSE}, 
                {foo, "after", drool, Boolean.FALSE},
                {foo, "after", bar, Boolean.FALSE}, 
                {foo, "not after", bar, Boolean.TRUE}, 
                {foo, "not after", drool, Boolean.TRUE}, 
                {bar, "not after", drool, Boolean.TRUE}, 
                {bar, "not after", foo, Boolean.FALSE},
                {drool, "not after", foo, Boolean.FALSE}, 
                {drool, "not after", bar, Boolean.TRUE}, 
                {bar, "after[1]", foo, Boolean.TRUE}, 
                {bar, "after[0]", foo, Boolean.TRUE}, 
                {bar, "after[-3]", drool, Boolean.TRUE},
                {bar, "after[-4]", drool, Boolean.TRUE}, 
                {drool, "after[2]", foo, Boolean.TRUE}, 
                {drool, "after[1]", foo, Boolean.TRUE}, 
                {drool, "after[-2]", bar, Boolean.TRUE}, 
                {drool, "after[-3]", bar, Boolean.TRUE},
                {foo, "after[-6]", drool, Boolean.TRUE}, 
                {foo, "after[-7]", drool, Boolean.TRUE}, 
                {foo, "after[-6]", bar, Boolean.TRUE}, 
                {foo, "after[-7]", bar, Boolean.TRUE}, 
                {bar, "not after[1]", foo, Boolean.FALSE},
                {bar, "not after[0]", foo, Boolean.FALSE}, 
                {bar, "not after[-3]", drool, Boolean.FALSE}, 
                {bar, "not after[-4]", drool, Boolean.FALSE}, 
                {drool, "not after[2]", foo, Boolean.FALSE}, 
                {drool, "not after[1]", foo, Boolean.FALSE},
                {drool, "not after[-2]", bar, Boolean.FALSE}, 
                {drool, "not after[-3]", bar, Boolean.FALSE}, 
                {foo, "not after[-6]", drool, Boolean.FALSE}, 
                {foo, "not after[-7]", drool, Boolean.FALSE}, 
                {foo, "not after[-6]", bar, Boolean.FALSE},
                {foo, "not after[-7]", bar, Boolean.FALSE}, 
                {drool, "after[1,4]", foo, Boolean.TRUE}, 
                {drool, "after[3,6]", foo, Boolean.FALSE}, 
                {drool, "after[-3,1]", bar, Boolean.TRUE},
                {drool, "after[-1,3]", bar, Boolean.FALSE},
                {bar, "after[1,5]", foo, Boolean.TRUE}, 
                {bar, "after[2,5]", foo, Boolean.FALSE}, 
                {bar, "after[-3,0]", drool, Boolean.TRUE}, 
                {bar, "after[-2,1]", drool, Boolean.FALSE}, 
                {foo, "after[-7,-3]", bar, Boolean.TRUE},
                {foo, "after[-5,-1]", bar, Boolean.FALSE}, 
                {foo, "after[-6,-5]", drool, Boolean.TRUE},
                {foo, "after[-5,-4]", drool, Boolean.FALSE}, 
                {drool, "not after[1,4]", foo, Boolean.FALSE}, 
                {drool, "not after[3,6]", foo, Boolean.TRUE},
                {drool, "not after[-3,1]", bar, Boolean.FALSE}, 
                {drool, "not after[-1,3]", bar, Boolean.TRUE}, 
                {bar, "not after[1,5]", foo, Boolean.FALSE}, 
                {bar, "not after[2,5]", foo, Boolean.TRUE}, 
                {bar, "not after[-3,0]", drool, Boolean.FALSE},
                {bar, "not after[-2,1]", drool, Boolean.TRUE}, 
                {foo, "not after[-7,-3]", bar, Boolean.FALSE}, 
                {foo, "not after[-5,-1]", bar, Boolean.TRUE}, 
                {foo, "not after[-6,-5]", drool, Boolean.FALSE},
                {foo, "not after[-5,-4]", drool, Boolean.TRUE},};

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testBefore() {
        registry.addEvaluatorDefinition( BeforeEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   1,
                                                   2 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   2 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     5,
                                                     3 );

        final Object[][] data = {
                {foo, "before", drool, Boolean.TRUE}, 
                {foo, "before", bar, Boolean.FALSE}, 
                {drool, "before", foo, Boolean.FALSE}, 
                {drool, "before", bar, Boolean.FALSE}, 
                {bar, "before", drool, Boolean.TRUE},
                {bar, "before", foo, Boolean.FALSE},
                {foo, "not before", drool, Boolean.FALSE},
                {foo, "not before", bar, Boolean.TRUE}, 
                {drool, "not before", foo, Boolean.TRUE},
                {drool, "not before", bar, Boolean.TRUE},
                {bar, "not before", drool, Boolean.FALSE},
                {bar, "not before", foo, Boolean.TRUE},
                {foo, "before[2]", drool, Boolean.TRUE}, 
                {foo, "before[3]", drool, Boolean.FALSE},
                {foo, "before[-1]", bar, Boolean.TRUE},
                {foo, "before[-2]", bar, Boolean.TRUE}, 
                {bar, "before[1]", drool, Boolean.TRUE}, 
                {bar, "before[2]", drool, Boolean.FALSE},
                {bar, "before[-3]", foo, Boolean.TRUE},
                {bar, "before[-2]", foo, Boolean.FALSE},
                {drool, "before[-6]", bar, Boolean.TRUE},
                {drool, "before[-5]", bar, Boolean.FALSE}, 
                {drool, "before[-7]", foo, Boolean.TRUE},
                {drool, "before[-8]", foo, Boolean.TRUE}, 
                {foo, "not before[2]", drool, Boolean.FALSE},
                {foo, "not before[3]", drool, Boolean.TRUE}, 
                {foo, "not before[-1]", bar, Boolean.FALSE},
                {foo, "not before[-2]", bar, Boolean.FALSE}, 
                {bar, "not before[1]", drool, Boolean.FALSE},
                {bar, "not before[2]", drool, Boolean.TRUE},
                {bar, "not before[-3]", foo, Boolean.FALSE}, 
                {bar, "not before[-2]", foo, Boolean.TRUE}, 
                {drool, "not before[-6]", bar, Boolean.FALSE}, 
                {drool, "not before[-5]", bar, Boolean.TRUE}, 
                {drool, "not before[-7]", foo, Boolean.FALSE},
                {drool, "not before[-8]", foo, Boolean.FALSE},
                {foo, "before[2,4]", drool, Boolean.TRUE},
                {foo, "before[3,4]", drool, Boolean.FALSE}, 
                {foo, "before[-1,1]", bar, Boolean.TRUE},
                {foo, "before[0,-2]", bar, Boolean.TRUE},
                {bar, "before[0,4]", drool, Boolean.TRUE}, 
                {bar, "before[2,4]", drool, Boolean.FALSE}, 
                {bar, "before[-4,0]", foo, Boolean.TRUE}, 
                {bar, "before[-2,0]", foo, Boolean.FALSE},
                {drool, "before[-6,-3]", bar, Boolean.TRUE},
                {drool, "before[-5,-3]", bar, Boolean.FALSE}, 
                {drool, "before[-7,-4]", foo, Boolean.TRUE}, 
                {drool, "before[-6,-4]", foo, Boolean.FALSE}, 
                {foo, "not before[2,4]", drool, Boolean.FALSE}, 
                {foo, "not before[3,4]", drool, Boolean.TRUE},
                {foo, "not before[-1,1]", bar, Boolean.FALSE}, 
                {foo, "not before[0,-2]", bar, Boolean.FALSE}, 
                {bar, "not before[0,4]", drool, Boolean.FALSE}, 
                {bar, "not before[2,4]", drool, Boolean.TRUE},
                {bar, "not before[-4,0]", foo, Boolean.FALSE}, 
                {bar, "not before[-2,0]", foo, Boolean.TRUE}, 
                {drool, "not before[-6,-3]", bar, Boolean.FALSE}, 
                {drool, "not before[-5,-3]", bar, Boolean.TRUE},
                {drool, "not before[-7,-4]", foo, Boolean.FALSE}, 
                {drool, "not before[-6,-4]", foo, Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testCoincides() {
        registry.addEvaluatorDefinition( CoincidesEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   3 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   3 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     2,
                                                     2 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    1,
                                                    2 );

        final Object[][] data = {
                {foo, "coincides", bar, Boolean.TRUE}, 
                {foo, "coincides", drool, Boolean.FALSE}, 
                {foo, "coincides", mole, Boolean.FALSE}, 
                {drool, "coincides", mole, Boolean.FALSE}, 
                {foo, "not coincides", bar, Boolean.FALSE},
                {foo, "not coincides", drool, Boolean.TRUE}, 
                {foo, "not coincides", mole, Boolean.TRUE}, 
                {drool, "not coincides", mole, Boolean.TRUE}, 
                {foo, "coincides[1]", bar, Boolean.TRUE}, 
                {foo, "coincides[1]", drool, Boolean.TRUE},
                {foo, "coincides[2]", mole, Boolean.TRUE}, 
                {foo, "coincides[1]", mole, Boolean.FALSE}, 
                {drool, "coincides[1]", mole, Boolean.TRUE},
                {foo, "not coincides[1]", bar, Boolean.FALSE}, 
                {foo, "not coincides[1]", drool, Boolean.FALSE},
                {foo, "not coincides[2]", mole, Boolean.FALSE}, 
                {foo, "not coincides[1]", mole, Boolean.TRUE}, 
                {drool, "not coincides[1]", mole, Boolean.FALSE}, 
                {foo, "coincides[1,2]", bar, Boolean.TRUE},
                {foo, "coincides[0,1]", drool, Boolean.TRUE},
                {foo, "coincides[1,0]", drool, Boolean.FALSE}, 
                {foo, "coincides[1,2]", mole, Boolean.TRUE}, 
                {foo, "coincides[1,1]", mole, Boolean.FALSE}, 
                {drool, "coincides[1,1]", mole, Boolean.TRUE},
                {drool, "coincides[0,1]", mole, Boolean.FALSE},
                {foo, "not coincides[1,2]", bar, Boolean.FALSE}, 
                {foo, "not coincides[0,1]", drool, Boolean.FALSE},
                {foo, "not coincides[1,0]", drool, Boolean.TRUE},
                {foo, "not coincides[1,2]", mole, Boolean.FALSE},
                {foo, "not coincides[1,1]", mole, Boolean.TRUE}, 
                {drool, "not coincides[1,1]", mole, Boolean.FALSE}, 
                {drool, "not coincides[0,1]", mole, Boolean.TRUE}};

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testDuring() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   4,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     1,
                                                     5 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    7,
                                                    6 );

        final Object[][] data = {
                 {foo, "during", bar, Boolean.FALSE}, 
                 {foo, "during", drool, Boolean.FALSE}, 
                 {foo, "during", mole, Boolean.FALSE}, 
                 {bar, "during", foo, Boolean.TRUE}, 
                 {bar, "during", drool, Boolean.FALSE}, 
                 {bar, "during", mole, Boolean.FALSE}, 
                 {foo, "not during", bar, Boolean.TRUE}, 
                 {foo, "not during", drool, Boolean.TRUE}, 
                 {foo, "not during", mole, Boolean.TRUE}, 
                 {bar, "not during", foo, Boolean.FALSE}, 
                 {bar, "not during", drool, Boolean.TRUE}, 
                 {bar, "not during", mole, Boolean.TRUE}, 

                 {bar, "during[2]", foo, Boolean.TRUE}, 
                 {bar, "during[3]", foo, Boolean.TRUE}, 
                 {bar, "during[1]", foo, Boolean.FALSE},
                 {bar, "not during[2]", foo, Boolean.FALSE}, 
                 {bar, "not during[3]", foo, Boolean.FALSE}, 
                 {bar, "not during[1]", foo, Boolean.TRUE},
                 
                 {bar, "during[1, 2]", foo, Boolean.TRUE}, 
                 {bar, "during[2, 3]", foo, Boolean.FALSE}, 
                 {bar, "during[3, 3]", foo, Boolean.FALSE},
                 {bar, "not during[1, 2]", foo, Boolean.FALSE}, 
                 {bar, "not during[2, 3]", foo, Boolean.TRUE}, 
                 {bar, "not during[3, 3]", foo, Boolean.TRUE},
                 
                 {bar, "during[2, 2, 1, 1]", foo, Boolean.TRUE}, 
                 {bar, "during[1, 5, 1, 3]", foo, Boolean.TRUE}, 
                 {bar, "during[0, 1, 0, 3]", foo, Boolean.FALSE},
                 {bar, "not during[2, 2, 1, 1]", foo, Boolean.FALSE}, 
                 {bar, "not during[1, 5, 1, 3]", foo, Boolean.FALSE}, 
                 {bar, "not during[0, 1, 0, 3]", foo, Boolean.TRUE}
                 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testIncludes() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   4,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     1,
                                                     5 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    7,
                                                    6 );

        final Object[][] data = {
                 {bar, "includes", foo, Boolean.FALSE}, 
                 {drool, "includes", foo, Boolean.FALSE}, 
                 {mole, "includes", foo, Boolean.FALSE}, 
                 {foo, "includes", bar, Boolean.TRUE}, 
                 {drool, "includes", bar, Boolean.FALSE}, 
                 {mole, "includes", bar, Boolean.FALSE}, 
                 {bar, "not includes", foo, Boolean.TRUE}, 
                 {drool, "not includes", foo, Boolean.TRUE}, 
                 {mole, "not includes", foo, Boolean.TRUE}, 
                 {foo, "not includes", bar, Boolean.FALSE}, 
                 {drool, "not includes", bar, Boolean.TRUE}, 
                 {mole, "not includes", bar, Boolean.TRUE}, 

                 {foo, "includes[2]", bar, Boolean.TRUE}, 
                 {foo, "includes[3]", bar, Boolean.TRUE}, 
                 {foo, "includes[1]", bar, Boolean.FALSE},
                 {foo, "not includes[2]", bar, Boolean.FALSE}, 
                 {foo, "not includes[3]", bar, Boolean.FALSE}, 
                 {foo, "not includes[1]", bar, Boolean.TRUE},
                 
                 {foo, "includes[1, 2]", bar, Boolean.TRUE}, 
                 {foo, "includes[2, 3]", bar, Boolean.FALSE}, 
                 {foo, "includes[3, 3]", bar, Boolean.FALSE},
                 {foo, "not includes[1, 2]", bar, Boolean.FALSE}, 
                 {foo, "not includes[2, 3]", bar, Boolean.TRUE}, 
                 {foo, "not includes[3, 3]", bar, Boolean.TRUE},
                 
                 {foo, "includes[2, 2, 1, 1]", bar, Boolean.TRUE}, 
                 {foo, "includes[1, 5, 1, 3]", bar, Boolean.TRUE}, 
                 {foo, "includes[0, 1, 0, 3]", bar, Boolean.FALSE},
                 {foo, "not includes[2, 2, 1, 1]", bar, Boolean.FALSE}, 
                 {foo, "not includes[1, 5, 1, 3]", bar, Boolean.FALSE}, 
                 {foo, "not includes[0, 1, 0, 3]", bar, Boolean.TRUE}
                 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testFinishes() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   5,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     2,
                                                     10 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    7,
                                                    6 );

        final Object[][] data = {
                 {bar,   "finishes", foo, Boolean.TRUE}, 
                 {drool, "finishes", foo, Boolean.FALSE}, 
                 {mole,  "finishes", foo, Boolean.FALSE}, 
                 {foo,   "finishes", bar, Boolean.FALSE},
                 
                 {bar,   "not finishes", foo, Boolean.FALSE}, 
                 {drool, "not finishes", foo, Boolean.TRUE}, 
                 {mole,  "not finishes", foo, Boolean.TRUE}, 
                 {foo,   "not finishes", bar, Boolean.TRUE},
                 
                 {bar,   "finishes[1]", foo, Boolean.TRUE}, 
                 {drool, "finishes[1]", foo, Boolean.FALSE}, 
                 {mole,  "finishes[1]", foo, Boolean.TRUE}, 
                 {foo,   "finishes[1]", bar, Boolean.FALSE},
                 
                 {bar,   "not finishes[1]", foo, Boolean.FALSE}, 
                 {drool, "not finishes[1]", foo, Boolean.TRUE}, 
                 {mole,  "not finishes[1]", foo, Boolean.FALSE}, 
                 {foo,   "not finishes[1]", bar, Boolean.TRUE},
                 
                 {mole,  "finishes[3]", foo, Boolean.TRUE}, 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testFinishedBy() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   5,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     2,
                                                     10 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    7,
                                                    6 );

        final Object[][] data = {
                 {foo, "finishedby", bar, Boolean.TRUE}, 
                 {foo, "finishedby", drool, Boolean.FALSE}, 
                 {foo, "finishedby", mole, Boolean.FALSE}, 
                 {bar, "finishedby", foo, Boolean.FALSE},
                 
                 {foo, "not finishedby", bar, Boolean.FALSE}, 
                 {foo, "not finishedby", drool, Boolean.TRUE}, 
                 {foo, "not finishedby", mole, Boolean.TRUE}, 
                 {bar, "not finishedby", foo, Boolean.TRUE},
                 
                 {foo, "finishedby[1]", bar, Boolean.TRUE}, 
                 {foo, "finishedby[1]", drool, Boolean.FALSE}, 
                 {foo, "finishedby[1]", mole, Boolean.TRUE}, 
                 {bar, "finishedby[1]", foo, Boolean.FALSE},
                 
                 {foo, "not finishedby[1]", bar, Boolean.FALSE}, 
                 {foo, "not finishedby[1]", drool, Boolean.TRUE}, 
                 {foo, "not finishedby[1]", mole, Boolean.FALSE}, 
                 {bar, "not finishedby[1]", foo, Boolean.TRUE},
                 
                 {foo, "finishedby[3]", mole, Boolean.TRUE}, 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testStarts() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     2,
                                                     10 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    1,
                                                    4 );

        final Object[][] data = {
                 {bar,   "starts", foo, Boolean.TRUE}, 
                 {drool, "starts", foo, Boolean.FALSE}, 
                 {mole,  "starts", foo, Boolean.FALSE}, 
                 {foo,   "starts", bar, Boolean.FALSE},
                 
                 {bar,   "not starts", foo, Boolean.FALSE}, 
                 {drool, "not starts", foo, Boolean.TRUE}, 
                 {mole,  "not starts", foo, Boolean.TRUE}, 
                 {foo,   "not starts", bar, Boolean.TRUE},
                 
                 {bar,   "starts[1]", foo, Boolean.TRUE}, 
                 {drool, "starts[1]", foo, Boolean.FALSE}, 
                 {mole,  "starts[1]", foo, Boolean.TRUE}, 
                 {foo,   "starts[1]", bar, Boolean.FALSE},
                 
                 {bar,   "not starts[1]", foo, Boolean.FALSE}, 
                 {drool, "not starts[1]", foo, Boolean.TRUE}, 
                 {mole,  "not starts[1]", foo, Boolean.FALSE}, 
                 {foo,   "not starts[1]", bar, Boolean.TRUE},
                 
                 {mole,  "starts[3]", foo, Boolean.TRUE}, 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testStartedBy() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   10 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     2,
                                                     10 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    1,
                                                    6 );

        final Object[][] data = {
                 {foo, "startedby", bar, Boolean.TRUE}, 
                 {foo, "startedby", drool, Boolean.FALSE}, 
                 {foo, "startedby", mole, Boolean.FALSE}, 
                 {bar, "startedby", foo, Boolean.FALSE},
                 
                 {foo, "not startedby", bar, Boolean.FALSE}, 
                 {foo, "not startedby", drool, Boolean.TRUE}, 
                 {foo, "not startedby", mole, Boolean.TRUE}, 
                 {bar, "not startedby", foo, Boolean.TRUE},
                 
                 {foo, "startedby[1]", bar, Boolean.TRUE}, 
                 {foo, "startedby[1]", drool, Boolean.FALSE}, 
                 {foo, "startedby[1]", mole, Boolean.TRUE}, 
                 {bar, "startedby[1]", foo, Boolean.FALSE},
                 
                 {foo, "not startedby[1]", bar, Boolean.FALSE}, 
                 {foo, "not startedby[1]", drool, Boolean.TRUE}, 
                 {foo, "not startedby[1]", mole, Boolean.FALSE}, 
                 {bar, "not startedby[1]", foo, Boolean.TRUE},
                 
                 {foo, "startedby[3]", mole, Boolean.TRUE}, 
                };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testMeets() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   8 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   10,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     8,
                                                     5 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    11,
                                                    4 );

        final Object[][] data = {
             {foo,   "meets", bar, Boolean.TRUE}, 
             {foo,   "meets", drool, Boolean.FALSE}, 
             {foo,   "meets", mole, Boolean.FALSE}, 
             
             {foo,   "not meets", bar, Boolean.FALSE}, 
             {foo,   "not meets", drool, Boolean.TRUE}, 
             {foo,   "not meets", mole, Boolean.TRUE}, 
             
             {foo,   "meets[1]", bar, Boolean.TRUE}, 
             {foo,   "meets[1]", drool, Boolean.FALSE}, 
             {foo,   "meets[1]", mole, Boolean.TRUE}, 
             {foo,   "meets[2]", drool, Boolean.TRUE}, 
             
             {foo,   "not meets[1]", bar, Boolean.FALSE}, 
             {foo,   "not meets[1]", drool, Boolean.TRUE}, 
             {foo,   "not meets[1]", mole, Boolean.FALSE}, 
             {foo,   "not meets[2]", drool, Boolean.FALSE} 
            };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testMetBy() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   10,
                                                   8 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   8 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     5,
                                                     3 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    4,
                                                    7 );

        final Object[][] data = {
             {foo,   "metby", bar, Boolean.TRUE}, 
             {foo,   "metby", drool, Boolean.FALSE}, 
             {foo,   "metby", mole, Boolean.FALSE}, 
             
             {foo,   "not metby", bar, Boolean.FALSE}, 
             {foo,   "not metby", drool, Boolean.TRUE}, 
             {foo,   "not metby", mole, Boolean.TRUE}, 
             
             {foo,   "metby[1]", bar, Boolean.TRUE}, 
             {foo,   "metby[1]", drool, Boolean.FALSE}, 
             {foo,   "metby[1]", mole, Boolean.TRUE}, 
             {foo,   "metby[2]", drool, Boolean.TRUE}, 
             
             {foo,   "not metby[1]", bar, Boolean.FALSE}, 
             {foo,   "not metby[1]", drool, Boolean.TRUE}, 
             {foo,   "not metby[1]", mole, Boolean.FALSE}, 
             {foo,   "not metby[2]", drool, Boolean.FALSE} 
            };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testOverlaps() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   2,
                                                   8 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   7,
                                                   7 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     11,
                                                     5 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    5,
                                                    5 );

        final Object[][] data = {
             {foo,   "overlaps", bar, Boolean.TRUE}, 
             {foo,   "overlaps", drool, Boolean.FALSE}, 
             {foo,   "overlaps", mole, Boolean.FALSE}, 
             
             {foo,   "not overlaps", bar, Boolean.FALSE}, 
             {foo,   "not overlaps", drool, Boolean.TRUE}, 
             {foo,   "not overlaps", mole, Boolean.TRUE}, 
             
             {foo,   "overlaps[3]", bar, Boolean.TRUE}, 
             {foo,   "overlaps[3]", drool, Boolean.FALSE}, 
             {foo,   "overlaps[3]", mole, Boolean.FALSE}, 
             {foo,   "overlaps[2]", bar, Boolean.FALSE}, 
             {foo,   "overlaps[6]", mole, Boolean.FALSE}, 
             
             {foo,   "not overlaps[3]", bar, Boolean.FALSE}, 
             {foo,   "not overlaps[3]", drool, Boolean.TRUE}, 
             {foo,   "not overlaps[3]", mole, Boolean.TRUE}, 
             {foo,   "not overlaps[2]", bar, Boolean.TRUE}, 
             {foo,   "not overlaps[6]", mole, Boolean.TRUE},
             
             {foo,   "overlaps[1,3]", bar, Boolean.TRUE}, 
             {foo,   "overlaps[1,3]", drool, Boolean.FALSE}, 
             {foo,   "overlaps[1,3]", mole, Boolean.FALSE}, 
             {foo,   "overlaps[4,6]", bar, Boolean.FALSE}, 
             {foo,   "overlaps[1,8]", mole, Boolean.FALSE}, 
             
             {foo,   "not overlaps[1,3]", bar, Boolean.FALSE}, 
             {foo,   "not overlaps[1,3]", drool, Boolean.TRUE}, 
             {foo,   "not overlaps[1,3]", mole, Boolean.TRUE}, 
             {foo,   "not overlaps[4,6]", bar, Boolean.TRUE}, 
             {foo,   "not overlaps[1,8]", mole, Boolean.TRUE} 
            };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    public void testOverlapedBy() {
        registry.addEvaluatorDefinition( DuringEvaluatorDefinition.class.getName() );

        EventFactHandle foo = new EventFactHandle( 1,
                                                   "foo",
                                                   1,
                                                   7,
                                                   8 );
        EventFactHandle bar = new EventFactHandle( 2,
                                                   "bar",
                                                   1,
                                                   2,
                                                   8 );
        EventFactHandle drool = new EventFactHandle( 1,
                                                     "drool",
                                                     1,
                                                     11,
                                                     5 );
        EventFactHandle mole = new EventFactHandle( 1,
                                                    "mole",
                                                    1,
                                                    7,
                                                    3 );

        final Object[][] data = {
             {foo,   "overlappedby", bar, Boolean.TRUE}, 
             {foo,   "overlappedby", drool, Boolean.FALSE}, 
             {foo,   "overlappedby", mole, Boolean.FALSE}, 
             
             {foo,   "not overlappedby", bar, Boolean.FALSE}, 
             {foo,   "not overlappedby", drool, Boolean.TRUE}, 
             {foo,   "not overlappedby", mole, Boolean.TRUE}, 
             
             {foo,   "overlappedby[3]", bar, Boolean.TRUE}, 
             {foo,   "overlappedby[3]", drool, Boolean.FALSE}, 
             {foo,   "overlappedby[3]", mole, Boolean.FALSE}, 
             {foo,   "overlappedby[2]", bar, Boolean.FALSE}, 
             {foo,   "overlappedby[6]", mole, Boolean.FALSE}, 
             
             {foo,   "not overlappedby[3]", bar, Boolean.FALSE}, 
             {foo,   "not overlappedby[3]", drool, Boolean.TRUE}, 
             {foo,   "not overlappedby[3]", mole, Boolean.TRUE}, 
             {foo,   "not overlappedby[2]", bar, Boolean.TRUE}, 
             {foo,   "not overlappedby[6]", mole, Boolean.TRUE},
             
             {foo,   "overlappedby[1,3]", bar, Boolean.TRUE}, 
             {foo,   "overlappedby[1,3]", drool, Boolean.FALSE}, 
             {foo,   "overlappedby[1,3]", mole, Boolean.FALSE}, 
             {foo,   "overlappedby[4,6]", bar, Boolean.FALSE}, 
             {foo,   "overlappedby[1,8]", mole, Boolean.FALSE}, 
             
             {foo,   "not overlappedby[1,3]", bar, Boolean.FALSE}, 
             {foo,   "not overlappedby[1,3]", drool, Boolean.TRUE}, 
             {foo,   "not overlappedby[1,3]", mole, Boolean.TRUE}, 
             {foo,   "not overlappedby[4,6]", bar, Boolean.TRUE}, 
             {foo,   "not overlappedby[1,8]", mole, Boolean.TRUE} 
            };

        runEvaluatorTest( data,
                          ValueType.OBJECT_TYPE );
    }

    private void runEvaluatorTest(final Object[][] data,
                                  final ValueType valueType) {
        final InternalReadAccessor extractor = new MockExtractor();
        for ( int i = 0; i < data.length; i++ ) {
            final Object[] row = data[i];
            boolean isNegated = ((String) row[1]).startsWith( "not " );
            //			System.out.println((String) row[1]);
            String evaluatorStr = isNegated ? ((String) row[1]).substring( 4 ) : (String) row[1];
            boolean isConstrained = evaluatorStr.endsWith( "]" );
            String parameters = null;
            if ( isConstrained ) {
                parameters = evaluatorStr.split( "\\[" )[1];
                evaluatorStr = evaluatorStr.split( "\\[" )[0];
                parameters = parameters.split( "\\]" )[0];
            }
            EvaluatorDefinition evalDef = registry.getEvaluatorDefinition( evaluatorStr );
            assertNotNull( evalDef );
            final Evaluator evaluator = evalDef.getEvaluator( valueType,
                                                              evaluatorStr,
                                                              isNegated,
                                                              parameters );
            //			System.out.println(evaluator);

            checkEvaluatorMethodWith2Extractors( valueType,
                                                 extractor,
                                                 row,
                                                 evaluator );
            checkEvaluatorMethodCachedRight( valueType,
                                             extractor,
                                             row,
                                             evaluator );
            checkEvaluatorMethodCachedLeft( valueType,
                                            extractor,
                                            row,
                                            evaluator );
            checkEvaluatorMethodWithFieldValue( valueType,
                                                extractor,
                                                row,
                                                evaluator );

            assertEquals( valueType,
                          evaluator.getValueType() );

        }
    }

    private void checkEvaluatorMethodWith2Extractors(final ValueType valueType,
                                                     final InternalReadAccessor extractor,
                                                     final Object[] row,
                                                     final Evaluator evaluator) {
        final boolean result = evaluator.evaluate( null,
                                                   extractor,
                                                   row[0],
                                                   extractor,
                                                   row[2] );
        final String message = "The evaluator type: [" + valueType + "] with 2 extractors incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    private void checkEvaluatorMethodCachedRight(final ValueType valueType,
                                                 final InternalReadAccessor extractor,
                                                 final Object[] row,
                                                 final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( evaluator,
                                                                   extractor,
                                                                   valueType,
                                                                   row );
        final boolean result = evaluator.evaluateCachedRight( null,
                                                              context,
                                                              row[2] );
        final String message = "The evaluator type: [" + valueType + "] with CachedRight incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    private void checkEvaluatorMethodCachedLeft(final ValueType valueType,
                                                final InternalReadAccessor extractor,
                                                final Object[] row,
                                                final Evaluator evaluator) {
        final VariableContextEntry context = this.getContextEntry( evaluator,
                                                                   extractor,
                                                                   valueType,
                                                                   row );
        final boolean result = evaluator.evaluateCachedLeft( null,
                                                             context,
                                                             row[0] );
        final String message = "The evaluator type: [" + valueType + "] with CachedLeft incorrectly returned " + result + " for [" + row[0] + " " + row[1] + " " + row[2] + "]. It was asserted to return " + row[3];

        if ( row[3] == Boolean.TRUE ) {
            assertTrue( message,
                        result );
        } else {
            assertFalse( message,
                         result );
        }
    }

    private void checkEvaluatorMethodWithFieldValue(final ValueType valueType,
                                                    final InternalReadAccessor extractor,
                                                    final Object[] row,
                                                    final Evaluator evaluator) {
        final FieldValue value = FieldFactory.getFieldValue( row[2] );
        RuntimeDroolsException exc = null;
        try {
            evaluator.evaluate( null,
                                extractor,
                                row[0],
                                value );
        } catch ( RuntimeDroolsException e ) {
            exc = e;
        }
        assertNotNull( exc );
    }

    private VariableContextEntry getContextEntry(final Evaluator evaluator,
                                                 final InternalReadAccessor extractor,
                                                 final ValueType valueType,
                                                 final Object[] row) {
        final Declaration declaration = new Declaration( "test",
                                                         extractor,
                                                         null );
        final ValueType coerced = evaluator.getCoercedValueType();

        if ( coerced.isIntegerNumber() ) {
            final LongVariableContextEntry context = new LongVariableContextEntry( extractor,
                                                                                   declaration,
                                                                                   evaluator );

            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Number) row[2]).longValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Number) row[0]).longValue();
            }
            return context;
        } else if ( coerced.isChar() ) {
            final CharVariableContextEntry context = new CharVariableContextEntry( extractor,
                                                                                   declaration,
                                                                                   evaluator );

            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Character) row[2]).charValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Character) row[0]).charValue();
            }
            return context;
        } else if ( coerced.isBoolean() ) {
            final BooleanVariableContextEntry context = new BooleanVariableContextEntry( extractor,
                                                                                         declaration,
                                                                                         evaluator );

            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Boolean) row[2]).booleanValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Boolean) row[0]).booleanValue();
            }
            return context;
        } else if ( coerced.isFloatNumber() ) {
            final DoubleVariableContextEntry context = new DoubleVariableContextEntry( extractor,
                                                                                       declaration,
                                                                                       evaluator );
            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = ((Number) row[2]).doubleValue();
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = ((Number) row[0]).doubleValue();
            }
            return context;
        } else {
            final ObjectVariableContextEntry context = new ObjectVariableContextEntry( extractor,
                                                                                       declaration,
                                                                                       evaluator );
            if ( row[2] == null ) {
                context.leftNull = true;
            } else {
                context.left = row[2];
            }

            if ( row[0] == null ) {
                context.rightNull = true;
            } else {
                context.right = row[0];
            }
            return context;
        }
    }

    public static class MockExtractor
        implements
        InternalReadAccessor {

        private static final long serialVersionUID = 400L;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public boolean isSelfReference() {
            return false;
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                       final Object object) {
            return object != null ? ((Boolean) object).booleanValue() : false;
        }

        public byte getByteValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Number) object).byteValue() : (byte) 0;
        }

        public char getCharValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Character) object).charValue() : '\0';
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory,
                                     final Object object) {
            return object != null ? ((Number) object).doubleValue() : 0.0;
        }

        public Class<?> getExtractToClass() {
            return null;
        }

        public String getExtractToClassName() {
            return null;
        }

        public float getFloatValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object != null ? ((Number) object).floatValue() : (float) 0.0;
        }

        public int getHashCode(InternalWorkingMemory workingMemory,
                               final Object object) {
            return 0;
        }

        public int getIntValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return object != null ? ((Number) object).intValue() : 0;
        }

        public long getLongValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
            return object != null ? ((Number) object).longValue() : 0;
        }

        public Method getNativeReadMethod() {
            return null;
        }

        public short getShortValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object != null ? ((Number) object).shortValue() : (short) 0;
        }

        public Object getValue(InternalWorkingMemory workingMemory,
                               final Object object) {
            return object;
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
            return object == null;
        }

        public ValueType getValueType() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getIndex() {
            return 0;
        }

        public boolean isGlobal() {
            return false;
        }

        public boolean getBooleanValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public byte getByteValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public char getCharValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public float getFloatValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getHashCode(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getIntValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getLongValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public short getShortValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public Object getValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isNullValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                             Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigDecimal getBigDecimalValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
