package org.drools.reteoo;

/*
 * $Id$
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.Agenda;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Duration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * @author mproctor
 */

public class SchedulerTest extends DroolsTestCase {
    public void testScheduledActivation() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Rule rule = new Rule( "test-rule" );
        final TerminalNode node = new TerminalNode(1, new MockTupleSource(2), rule);
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                data.add( "tested" );
            }
        } );

        /* 1/10th of a second */
        Duration duration = new Duration() {
            public long getDuration(Tuple tuple) {
                return 100;
            }

        };
        rule.setDuration( duration );

        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );

        assertEquals( 0,
                      data.size() );

        node.assertTuple( tuple, context, (WorkingMemoryImpl) workingMemory ); 

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      data.size() );
    }

    public void testDoLoopScheduledActivation() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        final WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        final TerminalNode node = new TerminalNode(1, new MockTupleSource(2), rule);
        final List data = new ArrayList();

        /* 1/10th of a second */
        Duration duration = new Duration() {
            public long getDuration(Tuple tuple) {
                return 100;
            }

        };

        rule.setDuration( duration );

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                               WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 3 ) {
                    PropagationContext context2 = new PropagationContextImpl( 0,
                                                                              0,
                                                                              rule,
                                                                              knowledgeHelper.getActivation() );
                    ReteTuple tuple2 = new ReteTuple( new FactHandleImpl( 2 ) );
                    node.assertTuple( tuple2, context2, (WorkingMemoryImpl) workingMemory );
                }
                data.add( "tested" );
            }
        } );

        PropagationContext context1 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  null,
                                                                  null );

        ReteTuple tuple1 = new ReteTuple( new FactHandleImpl( 1 ) );

        node.assertTuple( tuple1, context1, (WorkingMemoryImpl) workingMemory );     

        assertEquals( 0,
                      data.size() );

        // sleep for 0.5 seconds
        Thread.sleep( 500 );

        // now check for update
        assertEquals( 4,
                      data.size() );

    }

    public void testNoLoopScheduledActivation() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        final WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();        

        final Rule rule = new Rule( "test-rule" );
        final List data = new ArrayList();
        
        final TerminalNode node = new TerminalNode(1, new MockTupleSource(2), rule);

        /* 1/10th of a second */
        Duration duration = new Duration() {
            public long getDuration(Tuple tuple) {
                return 100;
            }

        };

        rule.setDuration( duration );
        rule.setNoLoop( true );

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 5 ) {
                    PropagationContext context2 = new PropagationContextImpl( 0,
                                                                              0,
                                                                              rule,
                                                                              knowledgeHelper.getActivation() );
                    ReteTuple tuple2 = new ReteTuple( new FactHandleImpl( 2 ) );
                    node.assertTuple( tuple2, context2, (WorkingMemoryImpl) workingMemory );
                }
                data.add( "tested" );
            }
        } );

        PropagationContext context1 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  null,
                                                                  null );

        ReteTuple tuple1 = new ReteTuple( new FactHandleImpl( 1 ) );
        node.assertTuple( tuple1, context1, (WorkingMemoryImpl) workingMemory ); 
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
