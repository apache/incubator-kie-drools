package org.drools.leaps;

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

import java.util.ArrayList;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Duration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author Alexander Bagerman
 */

public class SchedulerTest extends DroolsTestCase {
    public void testScheduledActivation() throws Exception {
        final RuleBase ruleBase = new LeapsRuleBase();

        final LeapsWorkingMemory workingMemory = (LeapsWorkingMemory) ruleBase.newWorkingMemory();

        final Rule rule = new Rule( "scheduled-test-rule" );
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -1991405634414239175L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                data.add( "tested" );
            }
        } );

        /* 1/10th of a second */
        final Duration duration = new Duration() {
            /**
             * 
             */
            private static final long serialVersionUID = -1650013015631329969L;

            public long getDuration(Tuple tuple) {
                return 100;
            }
        };
        rule.setDuration( duration );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null );

        final FactHandleImpl tupleFactHandle = (FactHandleImpl) workingMemory.assertObject( "tuple object" );
        final FactHandleImpl[] factHandlesTuple = new FactHandleImpl[1];
        factHandlesTuple[0] = tupleFactHandle;

        final ArrayList leapsRules = (ArrayList) Builder.processRule( rule );
        final LeapsTuple tuple = new LeapsTuple( factHandlesTuple,
                                                 (LeapsRule) leapsRules.get( 0 ),
                                                 context );

        assertEquals( 0,
                      data.size() );

        workingMemory.assertTuple( tuple );

        // sleep for 2 seconds
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      data.size() );
    }

    public void testDoLoopScheduledActivation() throws Exception {
        final RuleBase ruleBase = new LeapsRuleBase();

        final LeapsWorkingMemory workingMemory = (LeapsWorkingMemory) ruleBase.newWorkingMemory();

        final Rule rule = new Rule( "do-loop-scheduled-test-rule" );
        final List data = new ArrayList();

        /* 1/10th of a second */
        final Duration duration = new Duration() {
            /**
             * 
             */
            private static final long serialVersionUID = -65249353062404118L;

            public long getDuration(Tuple tuple) {
                return 100;
            }
        };

        rule.setDuration( duration );

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = 5431138295939934840L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 3 ) {
                    final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                                    0,
                                                                                    rule,
                                                                                    knowledgeHelper.getActivation() );

                    final FactHandleImpl tupleFactHandleIn = (FactHandleImpl) workingMemory.assertObject( "tuple object in" );
                    final FactHandleImpl[] factHandlesTupleIn = new FactHandleImpl[1];
                    factHandlesTupleIn[0] = tupleFactHandleIn;
                    final ArrayList leapsRules = (ArrayList) Builder.processRule( rule );
                    final LeapsTuple tupleIn = new LeapsTuple( factHandlesTupleIn,
                                                               (LeapsRule) leapsRules.get( 0 ),
                                                               context2 );
                    ((LeapsWorkingMemory) workingMemory).assertTuple( tupleIn );
                }
                data.add( "tested" );
            }
        } );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null );

        final FactHandleImpl tupleFactHandle = (FactHandleImpl) workingMemory.assertObject( "tuple object" );
        final FactHandleImpl[] factHandlesTuple = new FactHandleImpl[1];
        factHandlesTuple[0] = tupleFactHandle;

        final ArrayList leapsRules = (ArrayList) Builder.processRule( rule );
        final LeapsTuple tuple = new LeapsTuple( factHandlesTuple,
                                                 (LeapsRule) leapsRules.get( 0 ),
                                                 context );

        workingMemory.assertTuple( tuple );

        assertEquals( 0,
                      data.size() );

        // sleep for 0.5 seconds
        Thread.sleep( 1000 );

        // now check for update
        assertEquals( 4,
                      data.size() );

    }
}