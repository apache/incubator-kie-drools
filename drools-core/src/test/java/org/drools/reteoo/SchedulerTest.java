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

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.Agenda;
import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.WorkingMemory;
import org.drools.spi.Duration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.time.impl.DurationTimer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class SchedulerTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext buildContext;

    @Before
    public void setUp() throws Exception {
        ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
    }


    @Test
    public void testScheduledActivation() throws Exception {
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();
        InternalWorkingMemory workingMemory = ( InternalWorkingMemory ) ruleBase.newStatefulSession();

        final Rule rule = new Rule( "test-rule" );
        final RuleTerminalNode node = new RuleTerminalNode( idGenerator.getNextId(),
                                                            new MockTupleSource( idGenerator.getNextId() ),
                                                            rule,
                                                            rule.getLhs(),
                                                            0,
                                                            buildContext );
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                data.add( "tested" );
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        } );

        rule.setTimer( new DurationTimer(100) );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final LeftTupleImpl tuple = new LeftTupleImpl( new DefaultFactHandle( 1,
                                                                      "cheese" ), null,
                                                                      true  );

        assertEquals( 0,
                      data.size() );

        node.assertLeftTuple( tuple,
                          context,
                          workingMemory );
        workingMemory.fireAllRules();

        // sleep for 300ms
        Thread.sleep( 300 );

        // now check for update
        assertEquals( 1,
                      data.size() );
    }

    @Test
    public void testDoLoopScheduledActivation() throws Exception {
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        final RuleTerminalNode node = new RuleTerminalNode( idGenerator.getNextId(),
                                                            new MockTupleSource( idGenerator.getNextId() ),
                                                            rule,
                                                            rule.getLhs(),
                                                            0,
                                                            buildContext );
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 3 ) {
                    final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                                    0,
                                                                                    rule,
                                                                                    ( LeftTupleImpl ) knowledgeHelper.getTuple(),
                                                                                    null );
                    final LeftTupleImpl tuple2 = new LeftTupleImpl( new DefaultFactHandle( 2,
                                                                                   "cheese" ), null,
                                                                                   true  );
                    node.assertLeftTuple( tuple2,
                                      context2,
                                      (ReteooWorkingMemory) workingMemory );
                }
                data.add( "tested" );
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        } );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null,
                                                                        null );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( new DefaultFactHandle( 1,
                                                                       "cheese" ), null,
                                                                       true  );
        rule.setTimer( new DurationTimer(50) );

        node.assertLeftTuple( tuple1,
                          context1,
                          workingMemory );

        assertEquals( 0,
                      data.size() );
        workingMemory.fireAllRules();

        // sleep for 2 seconds
        Thread.sleep( 2000 );

        // now check for update
        assertEquals( 4,
                      data.size() );

    }

    @Test
    public void testNoLoopScheduledActivation() throws Exception {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        final List data = new ArrayList();

        final RuleTerminalNode node = new RuleTerminalNode( idGenerator.getNextId(),
                                                            new MockTupleSource( idGenerator.getNextId() ),
                                                            rule,
                                                            rule.getLhs(),
                                                            0,
                                                            buildContext );

        rule.setTimer( new DurationTimer(100) );
        rule.setNoLoop( true );

        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if ( data.size() < 5 ) {
                    final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                                    0,
                                                                                    rule,
                                                                                    ( LeftTupleImpl ) knowledgeHelper.getTuple(),
                                                                                    null );
                    final LeftTupleImpl tuple2 = new LeftTupleImpl( new DefaultFactHandle( 2,
                                                                                   "cheese" ), null,
                                                                                   true  );
                    node.assertLeftTuple( tuple2,
                                      context2,
                                      (ReteooWorkingMemory) workingMemory );
                }
                data.add( "tested" );
            }
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        } );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null,
                                                                        null );

        final LeftTupleImpl tuple1 = new LeftTupleImpl( new DefaultFactHandle( 1,
                                                                       "cheese" ), null,
                                                                       true  );
        node.assertLeftTuple( tuple1,
                          context1,
                          workingMemory );
        assertEquals( 0,
                      data.size() );
        
        workingMemory.fireAllRules();

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
