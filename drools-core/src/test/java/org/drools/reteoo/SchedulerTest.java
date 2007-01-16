package org.drools.reteoo;

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

import org.drools.Agenda;
import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Duration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author mproctor
 */

public class SchedulerTest extends DroolsTestCase {
    public void testScheduledActivation() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newWorkingMemory();

        final Rule rule = new Rule( "test-rule" );
        final RuleTerminalNode node = new RuleTerminalNode( 1,
                                                    new MockTupleSource( 2 ),
                                                    rule,
                                                    rule.getLhs());
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -7225205391633485219L;

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
            private static final long serialVersionUID = 5737133396760056769L;

            public long getDuration(Tuple tuple) {
                return 100;
            }

        };
        rule.setDuration( duration );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );

        final ReteTuple tuple = new ReteTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ) );

        assertEquals( 0,
                      data.size() );

        node.assertTuple( tuple,
                          context,
                          workingMemory );

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      data.size() );
    }

    public void testDoLoopScheduledActivation() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        final RuleTerminalNode node = new RuleTerminalNode( 1,
                                                    new MockTupleSource( 2 ),
                                                    rule,
                                                    rule.getLhs());
        final List data = new ArrayList();

        /* 1/10th of a second */
        final Duration duration = new Duration() {
            /**
             * 
             */
            private static final long serialVersionUID = 6465323213679604468L;

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
            private static final long serialVersionUID = 8128944672361208508L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 3 ) {
                    final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                                    0,
                                                                                    rule,
                                                                                    knowledgeHelper.getActivation() );
                    final ReteTuple tuple2 = new ReteTuple( new DefaultFactHandle( 2,
                                                                                   "cheese" ) );
                    node.assertTuple( tuple2,
                                      context2,
                                      (ReteooWorkingMemory) workingMemory );
                }
                data.add( "tested" );
            }
        } );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        final ReteTuple tuple1 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );

        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        assertEquals( 0,
                      data.size() );

        // sleep for 0.5 seconds
        Thread.sleep( 500 );

        // now check for update
        assertEquals( 4,
                      data.size() );

    }

    public void testNoLoopScheduledActivation() throws Exception {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        final List data = new ArrayList();

        final RuleTerminalNode node = new RuleTerminalNode( 1,
                                                    new MockTupleSource( 2 ),
                                                    rule,
                                                    rule.getLhs());

        /* 1/10th of a second */
        final Duration duration = new Duration() {
            /**
             * 
             */
            private static final long serialVersionUID = -2288407417074884302L;

            public long getDuration(Tuple tuple) {
                return 100;
            }

        };

        rule.setDuration( duration );
        rule.setNoLoop( true );

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -4822730789394039868L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 5 ) {
                    final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                                    0,
                                                                                    rule,
                                                                                    knowledgeHelper.getActivation() );
                    final ReteTuple tuple2 = new ReteTuple( new DefaultFactHandle( 2,
                                                                                   "cheese" ) );
                    node.assertTuple( tuple2,
                                      context2,
                                      (ReteooWorkingMemory) workingMemory );
                }
                data.add( "tested" );
            }
        } );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        final ReteTuple tuple1 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );
        assertEquals( 0,
                      data.size() );

        // sleep for 0.5 seconds
        Thread.sleep( 500 );

        // now check for update
        assertEquals( 1,
                      data.size() );

    }
    //
    //    public void testExceptionHandler() throws Exception {
    //        RuleBase ruleBase = new RuleBaseImpl();
    //
    //        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
    //        Agenda agenda = workingMemory.getAgenda();
    //        final Scheduler scheduler = Scheduler.getInstance();
    //
    //        final Rule rule = new Rule( "test-rule" );
    //
    //        // add consequence
    //        rule.setConsequence( new org.drools.spi.Consequence() {
    //            public void invoke(Activation activation,
    //                               WorkingMemory workingMemory) throws ConsequenceException {
    //                throw new ConsequenceException( "not enough cheese",
    //                                                rule );
    //            }
    //        } );
    //
    //        /* 1/10th of a second */
    //        Duration duration = new Duration() {
    //            public long getDuration(Tuple tuple) {
    //                return 100;
    //            }
    //
    //        };
    //        rule.setDuration( duration );
    //
    //        final List data = new ArrayList();
    //
    //        PropagationContext context = new PropagationContextImpl( 0,
    //                                                                 PropagationContext.ASSERTION,
    //                                                                 null,
    //                                                                 null );
    //
    //        ReteTuple tuple = new ReteTuple( 0,
    //                                         new FactHandleImpl( 1 ),
    //                                         workingMemory );
    //
    //        assertEquals( 0,
    //                      data.size() );
    //
    //        AsyncExceptionHandler handler = new AsyncExceptionHandler() {
    //            public void handleException(WorkingMemory workingMemory,
    //                                        ConsequenceException exception) {
    //                data.add( "tested" );
    //            }
    //        };
    //        workingMemory.setAsyncExceptionHandler( handler );
    //
    //        assertLength( 0,
    //                      data );
    //
    //        agenda.addToAgenda( tuple,
    //                            context,
    //                            rule );
    //
    //        // sleep for 2 seconds
    //        Thread.sleep( 300 );
    //
    //        // now check for update
    //        assertLength( 1,
    //                      data );
    //    }
}