/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reteoo.common;

import org.drools.core.WorkingMemory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.ReteooBuilder.IdGenerator;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.time.impl.DurationTimer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.rule.Agenda;
import org.kie.internal.KnowledgeBaseFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class SchedulerTest extends DroolsTestCase {
    private PropagationContextFactory pctxFactory;
    private InternalKnowledgeBase kBase;
    private BuildContext   buildContext;

    @Before
    public void setUp() throws Exception {
        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase, kBase.getReteooBuilder().getIdGenerator());
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
    }

    @Test
    public void testScheduledActivation() throws Exception {
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final RuleImpl rule = new RuleImpl("test-rule");
        final RuleTerminalNode node = new RuleTerminalNode(idGenerator.getNextId(),
                                                           new MockTupleSource(idGenerator.getNextId()),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                data.add("tested");
            }

            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        rule.setTimer(new DurationTimer(100));

        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                null,
                                                                                null,
                                                                                null);

        final RuleTerminalNodeLeftTuple tuple = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                    "cheese"),
                                                                              node,
                                                                              true);

        assertEquals(0,
                     data.size());

        node.assertLeftTuple(tuple,
                             context,
                             ksession);
        ksession.fireAllRules();

        // sleep for 300ms
        Thread.sleep(300);

        // now check for update
        assertEquals(1,
                     data.size());
    }

    @Test
    public void testDoLoopScheduledActivation() throws Exception {
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final Agenda agenda = ksession.getAgenda();

        final RuleImpl rule = new RuleImpl("test-rule");
        final RuleTerminalNode node = new RuleTerminalNode(idGenerator.getNextId(),
                                                           new MockTupleSource(idGenerator.getNextId()),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);
        final List data = new ArrayList();

        // add consequence
        rule.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if (data.size() < 3) {
                    final PropagationContext context2 = pctxFactory.createPropagationContext(0,
                                                                                             0,
                                                                                             rule,
                                                                                             (RuleTerminalNodeLeftTuple) knowledgeHelper.getTuple(),
                                                                                             null);
                    final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(2,
                                                                                                                 "cheese"),
                                                                                           node,
                                                                                           true);
                    node.assertLeftTuple(tuple2,
                                         context2,
                                         (StatefulKnowledgeSessionImpl) workingMemory);
                }
                data.add("tested");
            }

            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 null,
                                                                                 null,
                                                                                 null);

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node,
                                                                               true);
        rule.setTimer(new DurationTimer(50));

        node.assertLeftTuple(tuple1,
                             context1,
                             ksession);

        assertEquals(0,
                     data.size());
        ksession.fireAllRules();

        // sleep for 2 seconds
        Thread.sleep(2000);

        // now check for update
        assertEquals(4,
                     data.size());

    }

    @Test
    public void testNoLoopScheduledActivation() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();

        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final Agenda agenda = ksession.getAgenda();

        final RuleImpl rule = new RuleImpl("test-rule");
        final List data = new ArrayList();

        final RuleTerminalNode node = new RuleTerminalNode(idGenerator.getNextId(),
                                                           new MockTupleSource(idGenerator.getNextId()),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);

        rule.setTimer(new DurationTimer(100));
        rule.setNoLoop(true);

        // add consequence
        rule.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                /* on first invoke add another one to the agenda */
                if (data.size() < 5) {
                    final PropagationContext context2 = pctxFactory.createPropagationContext(0,
                                                                                             0,
                                                                                             rule,
                                                                                             (RuleTerminalNodeLeftTuple) knowledgeHelper.getTuple(),
                                                                                             null);
                    final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(2,
                                                                                                                 "cheese"),
                                                                                           node,
                                                                                           true);
                    node.assertLeftTuple(tuple2,
                                         context2,
                                         (StatefulKnowledgeSessionImpl) workingMemory);
                }
                data.add("tested");
            }

            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 null,
                                                                                 null,
                                                                                 null);

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node,
                                                                               true);
        node.assertLeftTuple(tuple1,
                             context1,
                             ksession);
        assertEquals(0,
                     data.size());

        ksession.fireAllRules();

        // sleep for 0.5 seconds
        Thread.sleep(500);

        // now check for update
        assertEquals(1,
                     data.size());

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
    //        rule.setConsequence( new org.kie.spi.Consequence() {
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
    //        PropagationContext context = new RetePropagationContext( 0,
    //                                                                 PropagationContext.INSERTION,
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
