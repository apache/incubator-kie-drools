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

package org.drools.reteoo.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.SalienceInteger;
import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.ActivationCancelledEvent;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.ReteooBuilder.IdGenerator;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.InternalActivationGroup;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.ConsequenceException;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.time.impl.DurationTimer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.rule.MatchCancelledCause;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.event.rule.ActivationUnMatchListener;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class AgendaTest extends DroolsTestCase {
    private InternalKnowledgeBase     kBase;
    private BuildContext              buildContext;
    private PropagationContextFactory pctxFactory;

    @Before
    public void setUp() throws Exception {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setPhreakEnabled(false);
        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase,
                                        kBase.getReteooBuilder().getIdGenerator());
        pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
    }

    @Test
    public void testClearAgenda() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        final RuleImpl rule1 = new RuleImpl("test-rule1");
        final RuleImpl rule2 = new RuleImpl("test-rule2");

        final RuleTerminalNode node1 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleTerminalNode node2 = new RuleTerminalNode(5,
                                                            new MockTupleSource(4),
                                                            rule2,
                                                            rule2.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(2, "cheese"), node2, true);

        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule1.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule2.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        assertEquals(0,
                     agenda.getFocus().size());

        rule1.setNoLoop(false);
        rule2.setTimer(new DurationTimer(5000));

        node1.assertLeftTuple(tuple1,
                              context1,
                              ksession);

        node2.assertLeftTuple(tuple2,
                              context1,
                              ksession);

        agenda.unstageActivations();

        // make sure we have an activation in the current focus
        assertEquals(1,
                     agenda.getFocus().size());

        assertEquals(1,
                     agenda.getScheduledActivations().length);

        agenda.clearAndCancel();

        assertEquals(0,
                     agenda.getFocus().size());

        assertEquals(0,
                     agenda.getScheduledActivations().length);
    }

    @Test
    public void testActivationUnMatchListener() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        final RuleImpl rule1 = new RuleImpl("test-rule1");

        final RuleTerminalNode node1 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);

        Cheese cheese = new Cheese();
        cheese.setPrice(50);
        final RuleTerminalNodeLeftTuple tuple = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, cheese), node1, true);

        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule1.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                AgendaItem item = (AgendaItem) knowledgeHelper.getMatch();
                final Cheese cheese = (Cheese) item.getTuple().getHandle().getObject();
                final int oldPrice = cheese.getPrice();
                cheese.setPrice(100);

                item.setActivationUnMatchListener(new ActivationUnMatchListener() {

                    public void unMatch(org.kie.api.runtime.rule.RuleRuntime wm,
                                        Match activation) {
                        cheese.setPrice(oldPrice);
                    }
                });
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        assertEquals(50, cheese.getPrice());

        node1.assertLeftTuple(tuple,
                              context1,
                              ksession);


        agenda.unstageActivations();
        agenda.fireNextItem(null, 0, -1);
        assertEquals(100, cheese.getPrice());

        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.DELETION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        node1.retractLeftTuple(tuple, context0, ksession);

        assertEquals(50, cheese.getPrice());
    }

    @Test
    public void testFilters() throws Exception {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        final Boolean[] filtered = new Boolean[]{false};

        ksession.addEventListener(new DefaultAgendaEventListener() {

            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                if (event.getCause() == MatchCancelledCause.FILTER) {
                    filtered[0] = true;
                }
            }
        });

        final RuleImpl rule = new RuleImpl("test-rule");
        final RuleTerminalNode node = new RuleTerminalNode(3,
                                                           new MockTupleSource(2),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);

        final Map results = new HashMap();
        // add consequence
        rule.setConsequence(new org.drools.core.spi.Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                results.put("fired",
                            new Boolean(true));
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        });

        final RuleTerminalNodeLeftTuple tuple = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                    "cheese"),
                                                                              node,
                                                                              true);
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                rule,
                                                                                null,
                                                                                new DefaultFactHandle());

        // test agenda is empty
        assertEquals(0,
                     agenda.getFocus().size());

        // True filter, activations should always add
        final AgendaFilter filterTrue = new

                AgendaFilter() {
                    public boolean accept(Match item) {
                        return true;
                    }
                };

        rule.setNoLoop(false);
        node.assertLeftTuple(tuple,
                             context,
                             ksession);

        agenda.unstageActivations();

        // check there is an item to fire
        assertEquals(1,
                     agenda.getFocus().size());
        agenda.fireNextItem(filterTrue, 0, -1);

        // check focus is empty
        assertEquals(0,
                     agenda.getFocus().size());

        // make sure it also fired
        assertEquals(new Boolean(true),
                     results.get("fired"));

        assertEquals(false,
                     filtered[0].booleanValue());

        // clear the agenda and the result map
        agenda.clearAndCancel();
        results.clear();

        // False filter, activations should always be denied
        final AgendaFilter filterFalse = new

                AgendaFilter() {
                    public boolean accept(Match item) {
                        return false;
                    }
                };

        rule.setNoLoop(false);
        node.assertLeftTuple(tuple,
                             context,
                             ksession);

        agenda.unstageActivations();

        // check we have an item to fire
        assertEquals(1,
                     agenda.getFocus().size());
        agenda.fireNextItem(filterFalse, 0, -1);

        // make sure the focus is empty
        assertEquals(0,
                     agenda.getFocus().size());

        // check the consequence never fired
        assertNull(results.get("fired"));

        assertEquals(true,
                     filtered[0].booleanValue());
    }

    @Test
    public void testFocusStack() throws ConsequenceException {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        // create a rule for each agendaGroup
        final RuleImpl rule0 = new RuleImpl("test-rule0");
        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);
        rule0.setConsequence(consequence);
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule1 = new RuleImpl("test-rule1",
                                    "agendaGroup1");
        final RuleTerminalNode node1 = new RuleTerminalNode(5,
                                                            new MockTupleSource(4),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);
        rule1.setConsequence(consequence);
        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule2 = new RuleImpl("test-rule2",
                                    "agendaGroup2");
        final RuleTerminalNode node2 = new RuleTerminalNode(7,
                                                            new MockTupleSource(6),
                                                            rule2,
                                                            rule2.getLhs(),
                                                            0,
                                                            buildContext);
        rule2.setConsequence(consequence);
        final PropagationContext context2 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule2,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule3 = new RuleImpl("test-rule3",
                                    "agendaGroup3");
        final RuleTerminalNode node3 = new RuleTerminalNode(9,
                                                            new MockTupleSource(8),
                                                            rule3,
                                                            rule3.getLhs(),
                                                            0,
                                                            buildContext);
        rule3.setConsequence(consequence);
        final PropagationContext context3 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule3,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);

        final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(2,
                                                                                                     "cheese"),
                                                                               node2,
                                                                               true);

        final RuleTerminalNodeLeftTuple tuple3 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(3,
                                                                                                     "cheese"),
                                                                               node2,
                                                                               true);

        final RuleTerminalNodeLeftTuple tuple4 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(4,
                                                                                                     "cheese"),
                                                                               node3,
                                                                               true);

        final RuleTerminalNodeLeftTuple tuple5 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(5,
                                                                                                     "cheese"),
                                                                               node3,
                                                                               true);

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create the AgendaGroups
        final AgendaGroup agendaGroup1 = new AgendaGroupQueueImpl("agendaGroup1",
                                                                  kBase);
        agenda.addAgendaGroup(agendaGroup1);

        final AgendaGroup agendaGroup2 = new AgendaGroupQueueImpl("agendaGroup2",
                                                                  kBase);
        agenda.addAgendaGroup(agendaGroup2);

        final AgendaGroup agendaGroup3 = new AgendaGroupQueueImpl("agendaGroup3",
                                                                  kBase);
        agenda.addAgendaGroup(agendaGroup3);

        // focus at this point is MAIN
        assertEquals(0,
                     agenda.focusStackSize());

        node0.assertLeftTuple(tuple1,
                              context0,
                              ksession);

        agenda.unstageActivations();

        // check focus is main
        final AgendaGroup main = agenda.getAgendaGroup(AgendaGroup.MAIN);
        assertEquals(agenda.getFocus(),
                     main);
        // check main got the tuple
        assertEquals(1,
                     agenda.getFocus().size());
        node2.assertLeftTuple(tuple2,
                              context2,
                              ksession);

        agenda.unstageActivations();

        // main is still focus and this tuple went to agendaGroup 2
        assertEquals(1,
                     agenda.getFocus().size());

        // check agendaGroup2 still got the tuple
        assertEquals(1,
                     agendaGroup2.size());

        // make sure total agenda size reflects this
        assertEquals(2,
                     agenda.agendaSize());

        // put another one on agendaGroup 2
        node2.assertLeftTuple(tuple3,
                              context2,
                              ksession);

        agenda.unstageActivations();

        // main is still focus so shouldn't have increased
        assertEquals(1,
                     agenda.getFocus().size());

        // check agendaGroup2 still got the tuple
        assertEquals(2,
                     agendaGroup2.size());

        // make sure total agenda size reflects this
        assertEquals(3,
                     agenda.agendaSize());

        // set the focus to agendaGroup1, note agendaGroup1 has no activations
        agenda.setFocus("agendaGroup1");
        // add agendaGroup2 onto the focus stack
        agenda.setFocus("agendaGroup2");
        // finally add agendaGroup3 to the top of the focus stack
        agenda.setFocus("agendaGroup3");

        // agendaGroup3, the current focus, has no activations
        assertEquals(0,
                     agenda.getFocus().size());

        // add to agendaGroup 3
        node3.assertLeftTuple(tuple4,
                              context3,
                              ksession);

        agenda.unstageActivations();

        assertEquals(1,
                     agenda.getFocus().size());

        node3.assertLeftTuple(tuple5,
                              context3,
                              ksession);

        agenda.unstageActivations();

        // agendaGroup3 now has 2 activations
        assertEquals(2,
                     agenda.getFocus().size());
        // check totalAgendaSize still works
        assertEquals(5,
                     agenda.agendaSize());

        // ok now lets check that stacks work with fireNextItem
        agenda.fireNextItem(null, 0, -1);

        // agendaGroup3 should still be the current agendaGroup
        assertEquals(agenda.getFocus(),
                     agendaGroup3);
        // agendaGroup3 has gone from 2 to one activations
        assertEquals(1,
                     agenda.getFocus().size());
        // check totalAgendaSize has reduced too
        assertEquals(4,
                     agenda.agendaSize());

        // now repeat the process
        agenda.fireNextItem(null, 0, -1);

        // focus is still agendaGroup3, but now its empty
        assertEquals(agenda.getFocus(),
                     agendaGroup3);
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(3,
                     agenda.agendaSize());

        // repeat fire again
        agenda.fireNextItem(null, 0, -1);

        // agendaGroup3 is empty so it should be popped from the stack making````````````````````
        // agendaGroup2
        // the current agendaGroup
        assertEquals(agendaGroup2,
                     agenda.getFocus());
        // agendaGroup2 had 2 activations, now it only has 1
        assertEquals(1,
                     agenda.getFocus().size());
        assertEquals(2,
                     agenda.agendaSize());

        // repeat fire again
        agenda.fireNextItem(null, 0, -1);

        assertEquals(agenda.getFocus(),
                     agendaGroup2);
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(1,
                     agenda.agendaSize());

        // this last fire is more interesting as it demonstrates that
        // agendaGroup1 on
        // the stack before agendaGroup2 gets skipped as it has no activations
        agenda.fireNextItem(null, 0, -1);

        assertEquals(agenda.getFocus(),
                     main);
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(0,
                     agenda.agendaSize());

    }

    //
    @Test
    public void testAutoFocus() throws ConsequenceException {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create the agendaGroup
        final AgendaGroup agendaGroup = new AgendaGroupQueueImpl("agendaGroup",
                                                                 kBase);
        agenda.addAgendaGroup(agendaGroup);

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };


        // create a rule for the agendaGroup
        final RuleImpl rule = new RuleImpl("test-rule",
                                   "agendaGroup");
        final RuleTerminalNode node = new RuleTerminalNode(2,
                                                           new MockTupleSource(2),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);

        final RuleTerminalNodeLeftTuple tuple = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                    "cheese"),
                                                                              node,
                                                                              true);
        rule.setConsequence(consequence);
        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                rule,
                                                                                null,
                                                                                new DefaultFactHandle());

        // first test that autoFocus=false works. Here the rule should not fire
        // as its agendaGroup does not have focus.
        rule.setAutoFocus(false);

        node.assertLeftTuple(tuple,
                             context,
                             ksession);

        agenda.unstageActivations();

        // check activation as added to the agendaGroup
        assertEquals(1,
                     agendaGroup.size());

        // fire next item, agendaGroup should not fire as its not on the focus stack
        // and thus should retain its sinle activation
        agenda.fireNextItem(null, 0, -1);
        assertEquals(1,
                     agendaGroup.size());

        // Clear the agenda we we can test again
        agenda.clearAndCancel();
        assertEquals(0,
                     agendaGroup.size());

        // Now test that autoFocus=true works. Here the rule should fire as its
        // agendaGroup gets the focus when the activation is created.
        rule.setAutoFocus(true);

        node.assertLeftTuple(tuple,
                             context,
                             ksession);

        agenda.unstageActivations();

        assertEquals(1,
                     agendaGroup.size());
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     agendaGroup.size());
    }

    @Test
    public void testAgendaGroupLockOnActive() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create the agendaGroup
        final InternalAgendaGroup agendaGroup = new AgendaGroupQueueImpl("agendaGroup",
                                                                         kBase);
        agenda.addAgendaGroup(agendaGroup);

        // create a rule for the agendaGroup
        final RuleImpl rule = new RuleImpl("test-rule",
                                   "agendaGroup");
        final RuleTerminalNode node = new RuleTerminalNode(2,
                                                           new MockTupleSource(2),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);

        final RuleTerminalNodeLeftTuple tuple = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                    "cheese"),
                                                                              node,
                                                                              true);

        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                rule,
                                                                                null,
                                                                                new DefaultFactHandle());

        // When both the rule is lock-on-active and the agenda group is active, activations should be ignored
        rule.setLockOnActive(true);
        ((InternalRuleFlowGroup) agendaGroup).setAutoDeactivate(false);
        agendaGroup.setActive(true);
        node.assertLeftTuple(tuple,
                             context,
                             ksession);
        // activation should be ignored
        assertEquals(0,
                     agendaGroup.size());

        // lock-on-active is now false so activation should propagate
        rule.setLockOnActive(false);
        node.assertLeftTuple(tuple,
                             context,
                             ksession);

        agenda.unstageActivations();

        assertEquals(1,
                     agendaGroup.size());

        // even if lock-on-active is true, unless the agenda group is active the activation will still propagate
        rule.setLockOnActive(true);
        agendaGroup.setActive(false);
        node.assertLeftTuple(tuple,
                             context,
                             ksession);
        agenda.unstageActivations();
        assertEquals(2,
                     agendaGroup.size());
    }

    @Test
    public void testActivationGroup() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        final List list = new ArrayList();

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add(knowledgeHelper.getRule());
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        // create a rule for each agendaGroup
        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setActivationGroup("activation-group-0");
        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);
        rule0.setConsequence(consequence);
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule1 = new RuleImpl("test-rule1");
        rule1.setActivationGroup("activation-group-0");
        rule1.setSalience(new SalienceInteger(10));
        final RuleTerminalNode node1 = new RuleTerminalNode(5,
                                                            new MockTupleSource(4),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);
        rule1.setConsequence(consequence);
        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule2 = new RuleImpl("test-rule2");
        rule2.setSalience(new SalienceInteger(-5));
        final RuleTerminalNode node2 = new RuleTerminalNode(7,
                                                            new MockTupleSource(6),
                                                            rule2,
                                                            rule2.getLhs(),
                                                            0,
                                                            buildContext);
        rule2.setConsequence(consequence);
        final PropagationContext context2 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule2,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule3 = new RuleImpl("test-rule3",
                                    "agendaGroup3");
        rule3.setSalience(new SalienceInteger(-10));
        rule3.setActivationGroup("activation-group-3");
        final RuleTerminalNode node3 = new RuleTerminalNode(9,
                                                            new MockTupleSource(8),
                                                            rule3,
                                                            rule3.getLhs(),
                                                            0,
                                                            buildContext);
        rule3.setConsequence(consequence);
        final PropagationContext context3 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule3,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple3 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple4 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple5 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple6 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node1, true);
        final RuleTerminalNodeLeftTuple tuple7 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node2, true);
        final RuleTerminalNodeLeftTuple tuple8 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node3, true);

        // Assert the tuple and check it was added to activation-group-0
        node0.assertLeftTuple(tuple1,
                              context0,
                              ksession);
        agenda.unstageActivations();
        final InternalActivationGroup activationGroup0 = agenda.getActivationGroup("activation-group-0");
        assertEquals(1,
                     activationGroup0.size());

        // Removing a tuple should remove the activation from the activation-group-0 again
        node0.retractLeftTuple(tuple1,
                               context0,
                               ksession);
        assertEquals(0,
                     activationGroup0.size());

        // Assert the tuple again and check it was added to activation-group-0
        node0.assertLeftTuple(tuple3,
                              context0,
                              ksession);
        agenda.unstageActivations();
        assertEquals(1,
                     activationGroup0.size());

        // Assert another tuple and check it was added to activation-group-0
        node1.assertLeftTuple(tuple4,
                              context1,
                              ksession);
        agenda.unstageActivations();
        assertEquals(2,
                     activationGroup0.size());

        // There should now be two potential activations to fire
        assertEquals(2,
                     agenda.focusStackSize());

        // The first tuple should fire, adding itself to the List and clearing and cancelling the other Activations in the activation-group-0
        agenda.fireNextItem(null, 0, -1);

        // Make sure the activation-group-0 is clear
        assertEquals(0,
                     activationGroup0.size());

        // Make sure the Agenda  is  empty
        assertEquals(0,
                     agenda.focusStackSize());

        // List should only have a single item, "rule0"
        assertEquals(1,
                     list.size());
        assertSame(rule1,
                   list.get(0));

        list.clear();

        //-------------------
        // Now try a more complex scenario involving  two Xor Groups and one  rule not in a Group
        node0.assertLeftTuple(tuple5,
                              context0,
                              ksession);
        node1.assertLeftTuple(tuple6,
                              context1,
                              ksession);
        node2.assertLeftTuple(tuple7,
                              context2,
                              ksession);
        node3.assertLeftTuple(tuple8,
                              context3,
                              ksession);
        agenda.unstageActivations();

        // activation-group-0 should be populated again
        assertEquals(2,
                     activationGroup0.size());

        // make sure the activation-group-3 is cleared when we can clear the Agenda Group for the activation that is in both
        final InternalActivationGroup activationGroup3 = agenda.getActivationGroup("activation-group-3");

        assertEquals(4,
                     agenda.agendaSize());
        assertEquals(1,
                     activationGroup3.size());

        agenda.clearAndCancelAgendaGroup("agendaGroup3");
        assertEquals(3,
                     agenda.agendaSize());
        assertEquals(0,
                     activationGroup3.size());

        // Activation for activation-group-0 should be next - the activation in no activation/agenda group should remain on the agenda
        agenda.fireNextItem(null, 0, -1);
        assertEquals(1,
                     agenda.agendaSize());
        assertEquals(0,
                     activationGroup0.size());

        // Fire  the  last activation and  make sure the Agenda Empties
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     agenda.agendaSize());

        assertEquals(2,
                     list.size());
        assertEquals(rule1,
                     list.get(0));
        assertEquals(rule2,
                     list.get(1));

    }

    /**
     * Basic RuleFlowGroup test where there are three rules, each in their own
     * RuleFlowGroup.  First only rule-flow-group-0 is activated and rule0 is
     * executed.  When the two remaining groups are activated, the rule with the
     * highest priority is executed first.
     */
    @Test
    public void testRuleFlowGroup() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        final List list = new ArrayList();

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add(knowledgeHelper.getRule());
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        // create a rule for each rule flow groups
        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setAgendaGroup("rule-flow-group-0");
        rule0.setConsequence(consequence);

        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleImpl rule1 = new RuleImpl("test-rule1");
        rule1.setAgendaGroup("rule-flow-group-1");
        rule1.setConsequence(consequence);

        final RuleTerminalNode node1 = new RuleTerminalNode(4,
                                                            new MockTupleSource(2),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleImpl rule2 = new RuleImpl("test-rule2");
        rule2.setAgendaGroup("rule-flow-group-2");
        rule2.setConsequence(consequence);
        rule2.setSalience(new SalienceInteger(10));

        final RuleTerminalNode node2 = new RuleTerminalNode(5,
                                                            new MockTupleSource(2),
                                                            rule2,
                                                            rule2.getLhs(),
                                                            0,
                                                            buildContext);

        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");
        final RuleFlowGroup ruleFlowGroup1 = agenda.getRuleFlowGroup("rule-flow-group-1");
        final RuleFlowGroup ruleFlowGroup2 = agenda.getRuleFlowGroup("rule-flow-group-2");

        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple0,
                              context0,
                              ksession);

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node1,
                                                                               true);
        node0.assertLeftTuple(tuple1,
                              context0,
                              ksession);

        final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node2,
                                                                               true);
        node1.assertLeftTuple(tuple2,
                              context0,
                              ksession);

        final RuleTerminalNodeLeftTuple tuple3 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node2.assertLeftTuple(tuple3,
                              context0,
                              ksession);
        agenda.unstageActivations();

        assertEquals(2,
                     ruleFlowGroup0.size());
        assertEquals(1,
                     ruleFlowGroup1.size());
        assertEquals(1,
                     ruleFlowGroup2.size());
        assertEquals(4,
                     agenda.agendaSize());

        // Activate the RuleFlowGroup, the nodes stay in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());
        assertEquals(4,
                     agenda.agendaSize());

        // As we fire each rule they are removed from both the Agenda and the RuleFlowGroup
        agenda.fireNextItem(null, 0, -1);
        assertEquals(1,
                     ruleFlowGroup0.size());
        assertEquals(3,
                     agenda.agendaSize());

        // After firing all activations of RuleFlowGroup 0, the agenda is empty
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());
        assertEquals(2,
                     agenda.agendaSize());

        // Now we activate two RuleFlowGroups together
        // All their activations should be added to the agenda.
        agenda.activateRuleFlowGroup("rule-flow-group-1");
        agenda.activateRuleFlowGroup("rule-flow-group-2");
        assertEquals(1,
                     ruleFlowGroup1.size());
        assertEquals(1,
                     ruleFlowGroup2.size());
        assertEquals(2,
                     agenda.agendaSize());

        // we set the salience higher on rule2, so it sould fire first and empty ruleFlowGroup2
        agenda.fireNextItem(null, 0, -1);
        assertEquals(1,
                     ruleFlowGroup1.size());
        assertEquals(0,
                     ruleFlowGroup2.size());
        assertEquals(1,
                     agenda.agendaSize());

        // this is the last activation, so everything should be empty after this
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());
        assertEquals(0,
                     ruleFlowGroup1.size());
        assertEquals(0,
                     ruleFlowGroup2.size());
        assertEquals(0,
                     agenda.agendaSize());
    }

    /**
     * RuleFlowGroup test that makes sure that, if new activations are created
     * for an active RuleFlowGroup, those activations get added to the agenda
     * directly as well.
     */
    @Test
    public void testRuleFlowGroup1() {
        final StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create rule1
        final Consequence consequence1 = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        final RuleImpl rule1 = new RuleImpl("test-rule1");
        rule1.setAgendaGroup("rule-flow-group-0");
        rule1.setConsequence(consequence1);

        final RuleTerminalNode node1 = new RuleTerminalNode(4,
                                                            new MockTupleSource(2),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);

        // create context
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        // create rule0
        final Consequence consequence0 = new

                Consequence() {
                    private static final long serialVersionUID = 510l;

                    public void evaluate(KnowledgeHelper knowledgeHelper,
                                         WorkingMemory w) {
                        // activate rule1
                        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                                     "cheese"),
                                                                                               node1,
                                                                                               true);
                        node1.assertLeftTuple(tuple1,
                                              context0,
                                              ksession);
                    }

                    public void readExternal(ObjectInput in) throws IOException,
                            ClassNotFoundException {

                    }

                    public void writeExternal(ObjectOutput out) throws IOException {

                    }

                    public String getName() {
                        return "default";
                    }
                };

        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setAgendaGroup("rule-flow-group-0");
        rule0.setConsequence(consequence0);

        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");

        // Create one activation for rule0 only
        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple0,
                              context0,
                              ksession);
        agenda.unstageActivations();

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals(1,
                     ruleFlowGroup0.size());

        // Activate the RuleFlowGroup, the activation stays in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(1,
                     ruleFlowGroup0.size());

        // As we fire the rule, an new activation is created for rule1, and it should be added to group AND the agenda.
        agenda.fireNextItem(null, 0, -1);
        assertEquals(1,
                     ruleFlowGroup0.size());

        // After firing all activations of RuleFlowGroup 0, the agenda is empty
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());
        assertEquals(0,
                     agenda.agendaSize());
    }

    /**
     * RuleFlowGroup test that makes sure that, if an activation in an active
     * RuleFlowGroup gets deactivated, the activation is no longer executed.
     */
    @Test
    public void testRuleFlowGroup2() {
        final StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create rule1
        final Consequence consequence1 = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        final RuleImpl rule1 = new RuleImpl("test-rule1");
        rule1.setAgendaGroup("rule-flow-group-0");
        rule1.setConsequence(consequence1);

        final RuleTerminalNode node1 = new RuleTerminalNode(4,
                                                            new MockTupleSource(2),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);

        // create context
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node1,
                                                                               true);

        // create rule0
        final Consequence consequence0 = new

                Consequence() {
                    private static final long serialVersionUID = 510l;

                    public void evaluate(KnowledgeHelper knowledgeHelper,
                                         WorkingMemory w) {
                        // deactivate rule1
                        node1.retractLeftTuple(tuple1,
                                               context0,
                                               ksession);
                    }

                    public void readExternal(ObjectInput in) throws IOException,
                            ClassNotFoundException {

                    }

                    public void writeExternal(ObjectOutput out) throws IOException {

                    }

                    public String getName() {
                        return "default";
                    }
                };

        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setAgendaGroup("rule-flow-group-0");
        rule0.setConsequence(consequence0);
        rule0.setSalience(new SalienceInteger(10));

        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");

        // Create an activation for both rules
        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple0,
                              context0,
                              ksession);

        node1.assertLeftTuple(tuple1,
                              context0,
                              ksession);
        agenda.unstageActivations();

        // RuleFlowGroup should be populated
        assertEquals(2,
                     ruleFlowGroup0.size());

        // Activate the RuleFlowGroup, the activations stay in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());

        // As we fire the rule, rule0 should execute first, as it has higher salience.
        // Rule0 should deactivate rule1 as well, so the everything should be empty
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());

        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());

    }

    /**
     * RuleFlowGroup test that makes sure that, when deactivating a RuleFlowGroup,
     * all activations for that group are no longer on the agenda.  When
     * reactivating the RuleFlowGroup however, they get added to the agenda again.
     */
    @Test
    public void testRuleFlowGroup3() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setAgendaGroup("rule-flow-group-0");
        rule0.setConsequence(consequence0);

        final RuleTerminalNode node0 = new RuleTerminalNode(1,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");

        // create context
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        // Create two activation for this rule
        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple0,
                              context0,
                              ksession);
        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple1,
                              context0,
                              ksession);
        agenda.unstageActivations();

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals(2,
                     ruleFlowGroup0.size());

        // Activate the RuleFlowGroup
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());

        // Reactivate an already active RuleFlowGroup should not have any effect
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());

        // Deactivate the RuleFlowGroup, the activations should be removed from
        // the agenda but still in the RuleFlowGroup
        agenda.deactivateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());

        // Reactivate the RuleFlowGroup, the activations stay in the group, but
        // should now also be in the Agenda again
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(2,
                     ruleFlowGroup0.size());

    }

    /**
     * Test auto-deactivation of RuleFlowGroup.
     */
    @Test
    public void testRuleFlowGroup4() {
        IdGenerator idGenerator = kBase.getReteooBuilder().getIdGenerator();
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setAgendaGroup("rule-flow-group-0");
        rule0.setConsequence(consequence0);

        final RuleTerminalNode node0 = new RuleTerminalNode(idGenerator.getNextId(),
                                                            new MockTupleSource(idGenerator.getNextId()),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");
        assertTrue(ruleFlowGroup0.isAutoDeactivate());
        ruleFlowGroup0.setAutoDeactivate(false);
        assertFalse(ruleFlowGroup0.isAutoDeactivate());

        // create context
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        // Create an activation for this rule
        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple0,
                              context0,
                              ksession);

        ksession.fireAllRules();

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals(1,
                     ruleFlowGroup0.size());

        // Activate the RuleFlowGroup
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(1,
                     ruleFlowGroup0.size());

        // Execute activation
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());
        assertTrue(ruleFlowGroup0.isActive());

        // Set auto-deactivation status to true
        ruleFlowGroup0.setAutoDeactivate(true);
        assertTrue(ruleFlowGroup0.isAutoDeactivate());
        agenda.fireNextItem(null, 0, -1);
        assertFalse(ruleFlowGroup0.isActive());

        // Add another activation and activate RuleFlowGroup again
        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple1,
                              context0,
                              ksession);
        agenda.unstageActivations();
        agenda.activateRuleFlowGroup("rule-flow-group-0");
        assertEquals(1,
                     ruleFlowGroup0.size());
        assertTrue(ruleFlowGroup0.isActive());

        // Execute the activation, the RuleFlowGroup should automatically deactivate
        agenda.fireNextItem(null, 0, -1);
        assertEquals(0,
                     ruleFlowGroup0.size());
        ksession.executeQueuedActions();
        assertEquals(0, ruleFlowGroup0.size());
        agenda.fireNextItem(null, 0, -1);
        assertFalse(ruleFlowGroup0.isActive());

        // A new activation should now be added to the RuleFlowGroup but not to the agenda
        final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1,
                                                                                                     "cheese"),
                                                                               node0,
                                                                               true);
        node0.assertLeftTuple(tuple2,
                              context0,
                              ksession);
        agenda.unstageActivations();
        assertEquals(1,
                     ruleFlowGroup0.size());
    }

    /**
     * Test auto-deactivation of empty ruleflow group.
     */
    @Test
    public void testRuleFlowGroup5() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();

        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        final RuleImpl rule0 = new RuleImpl("test-rule0");
        rule0.setRuleFlowGroup("rule-flow-group-0");
        rule0.setConsequence(consequence0);

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup("rule-flow-group-0");
        assertTrue(ruleFlowGroup0.isAutoDeactivate());

        // RuleFlowGroup should be empty, as well as the agenda
        assertEquals(0,
                     ruleFlowGroup0.size());
        assertEquals(0,
                     agenda.agendaSize());

        // @TODO FIXME (mdp)
        //        // Activate the RuleFlowGroup, the activations stay in the group, but
        //        // should now also be in the Agenda
        //        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        //        assertEquals( 0,
        //                      ruleFlowGroup0.size() );
        //        assertEquals( 0,
        //                      agenda.agendaSize() );
        //        workingMemory.executeQueuedActions();
        //
        //        assertFalse( ruleFlowGroup0.isActive() );
    }

    @Test
    public void testRuleFlowGroupLockOnActive() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        final InternalAgenda agenda = (InternalAgenda) ksession.getAgenda();

        // create the agendaGroup
        //final AgendaGroupImpl agendaGroup = new AgendaGroupImpl( "agendaGroup" );
        //agenda.addAgendaGroup( agendaGroup );

        final RuleFlowGroup ruleFlowGroup = (RuleFlowGroup) agenda.getRuleFlowGroup("rule-flow-group-0");

        // create a rule for the agendaGroup
        final RuleImpl rule = new RuleImpl("test-rule");
        rule.setAgendaGroup("rule-flow-group-0");
        final RuleTerminalNode node = new RuleTerminalNode(2,
                                                           new MockTupleSource(2),
                                                           rule,
                                                           rule.getLhs(),
                                                           0,
                                                           buildContext);

        final RuleTerminalNodeLeftTuple tuple1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node, true);
        final RuleTerminalNodeLeftTuple tuple2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node, true);
        final RuleTerminalNodeLeftTuple tuple3 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node, true);

        final PropagationContext context = pctxFactory.createPropagationContext(0,
                                                                                PropagationContext.INSERTION,
                                                                                rule,
                                                                                null,
                                                                                new DefaultFactHandle());

        // When both the rule is lock-on-active and the agenda group is active, activations should be ignored
        rule.setLockOnActive(true);
        ruleFlowGroup.setAutoDeactivate(false);
        ((InternalRuleFlowGroup) ruleFlowGroup).setActive(true);
        node.assertLeftTuple(tuple1,
                             context,
                             ksession);
        // activation should be ignored
        assertEquals(0, ruleFlowGroup.size());

        // lock-on-active is now false so activation should propagate
        rule.setLockOnActive(false);
        node.assertLeftTuple(tuple2,
                             context,
                             ksession);
        agenda.unstageActivations();
        assertEquals(1,
                     ruleFlowGroup.size());

        // even if lock-on-active is true, unless the agenda group is active the activation will still propagate
        rule.setLockOnActive(true);
        ((InternalAgendaGroup) ruleFlowGroup).setActive(false);
        node.assertLeftTuple(tuple3,
                             context,
                             ksession);
        agenda.unstageActivations();
        assertEquals(2,
                     ruleFlowGroup.size());
    }

    @Test
    public void testSequentialAgenda() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setPhreakEnabled(false);
        conf.setSequential(true);
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase(conf);

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 510l;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }

            public String getName() {
                return "default";
            }
        };

        // create a rule for each agendaGroup
        final RuleImpl rule0 = new RuleImpl("test-rule0");
        final RuleTerminalNode node0 = new RuleTerminalNode(3,
                                                            new MockTupleSource(2),
                                                            rule0,
                                                            rule0.getLhs(),
                                                            0,
                                                            buildContext);

        rule0.setConsequence(consequence);
        final PropagationContext context0 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule0,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule1 = new RuleImpl("test-rule1",
                                    "agendaGroup1");
        final RuleTerminalNode node1 = new RuleTerminalNode(5,
                                                            new MockTupleSource(4),
                                                            rule1,
                                                            rule1.getLhs(),
                                                            0,
                                                            buildContext);
        rule1.setConsequence(consequence);
        final PropagationContext context1 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule1,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule2 = new RuleImpl("test-rule2",
                                    "agendaGroup1");
        final RuleTerminalNode node2 = new RuleTerminalNode(7,
                                                            new MockTupleSource(6),
                                                            rule2,
                                                            rule2.getLhs(),
                                                            0,
                                                            buildContext);
        rule2.setConsequence(consequence);
        final PropagationContext context2 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule2,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleImpl rule3 = new RuleImpl("test-rule3",
                                    "agendaGroup2");
        final RuleTerminalNode node3 = new RuleTerminalNode(9,
                                                            new MockTupleSource(8),
                                                            rule3,
                                                            rule3.getLhs(),
                                                            0,
                                                            buildContext);
        rule3.setConsequence(consequence);
        final PropagationContext context3 = pctxFactory.createPropagationContext(0,
                                                                                 PropagationContext.INSERTION,
                                                                                 rule3,
                                                                                 null,
                                                                                 new DefaultFactHandle());

        final RuleTerminalNodeLeftTuple tuple0 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(1, "cheese"), node0, true);
        final RuleTerminalNodeLeftTuple tuple2_1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(2, "cheese"), node2, true);
        final RuleTerminalNodeLeftTuple tuple2_2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(3, "cheese"), node2, true);
        final RuleTerminalNodeLeftTuple tuple3_1 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(4, "cheese"), node3, true);
        final RuleTerminalNodeLeftTuple tuple3_2 = new RuleTerminalNodeLeftTuple(new DefaultFactHandle(5, "cheese"), node3, true);

        InternalWorkingMemory workingMemory = new StatefulKnowledgeSessionImpl(0L, kBase);

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        final AgendaGroup agendaGroup1 = agenda.getAgendaGroup("agendaGroup1");
        final AgendaGroup agendaGroup2 = agenda.getAgendaGroup("agendaGroup2");

        // focus at this point is MAIN
        assertEquals(0, agenda.focusStackSize());

        node0.assertLeftTuple(tuple0,
                              context0,
                              workingMemory);

        agenda.unstageActivations();

        // check focus is main
        final AgendaGroup main = agenda.getAgendaGroup(AgendaGroup.MAIN);
        assertEquals(agenda.getFocus(), main);
        // check main got the tuple
        assertEquals(1, agenda.getFocus().size());
        node2.assertLeftTuple(tuple2_1, context2, workingMemory);
        agenda.unstageActivations();

        // main is still focus and this tuple went to agendaGroup1
        assertEquals(1, agenda.getFocus().size());

        // check agendaGroup1 still got the tuple
        assertEquals(1, agendaGroup1.size());

        // make sure total agenda size reflects this
        assertEquals(2, agenda.agendaSize());

        // put another one on agendaGroup 1
        node2.assertLeftTuple(tuple2_2,
                              context2,
                              workingMemory);
        agenda.unstageActivations();

        // main is still focus so shouldn't have increased
        assertEquals(1,
                     agenda.getFocus().size());

        // check agendaGroup2 still got the tuple
        assertEquals(2,
                     agendaGroup1.size());

        // make sure total agenda size reflects this
        assertEquals(3,
                     agenda.agendaSize());

        // set the focus to agendaGroup1, note agendaGroup1 has no activations
        agenda.setFocus("agendaGroup1");
        // add agendaGroup2 onto the focus stack
        agenda.setFocus("agendaGroup2");

        // agendaGroup2, the current focus, has no activations
        assertEquals(0,
                     agenda.getFocus().size());

        // add to agendaGroup2
        node3.assertLeftTuple(tuple3_1,
                              context3,
                              workingMemory);
        agenda.unstageActivations();

        assertEquals(1,
                     agenda.getFocus().size());

        node3.assertLeftTuple(tuple3_2,
                              context3,
                              workingMemory);
        agenda.unstageActivations();

        // agendaGroup2 now has 2 activations
        assertEquals(2,
                     agenda.getFocus().size());

        // check totalAgendaSize still works
        assertEquals(5,
                     agenda.agendaSize());

        // ok now lets check that stacks work with fireNextItem
        agenda.fireNextItem(null, 0, -1);

        // agendaGroup2 should still be the current agendaGroup
        assertEquals(agendaGroup2,
                     agenda.getFocus());
        // agendaGroup2 has gone from 2 to one activations
        assertEquals(1,
                     agenda.getFocus().size());
        // check totalAgendaSize has reduced too
        assertEquals(4,
                     agenda.agendaSize());

        // now repeat the process
        agenda.fireNextItem(null, 0, -1);

        // focus is still agendaGroup2, but now its empty
        assertEquals(agendaGroup2,
                     agenda.getFocus());
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(3,
                     agenda.agendaSize());

        // repeat fire again
        agenda.fireNextItem(null, 0, -1);

        // agendaGroup2 is empty so it should be popped from the stack making agendaGroup1 the current agendaGroup
        assertEquals(agendaGroup1,
                     agenda.getFocus());
        // agendaGroup1 had 2 activations, now it only has 1
        assertEquals(1,
                     agenda.getFocus().size());
        assertEquals(2,
                     agenda.agendaSize());

        // repeat fire again
        agenda.fireNextItem(null, 0, -1);

        assertEquals(agendaGroup1,
                     agenda.getFocus());
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(1,
                     agenda.agendaSize());

        // this last fire is more interesting as it demonstrates that
        // agendaGroup1 on
        // the stack before agendaGroup2 gets skipped as it has no activations
        agenda.fireNextItem(null, 0, -1);

        assertEquals(agenda.getFocus(),
                     main);
        assertEquals(0,
                     agenda.getFocus().size());
        assertEquals(0,
                     agenda.agendaSize());

    }

    @Test
    public void testNullErrorOnGetScheduledActivations() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl)kBase.newStatefulKnowledgeSession();
        try {
            ((InternalAgenda) ksession.getAgenda()).getScheduledActivations();
        } catch (NullPointerException e) {
            fail("Exception Should not have been thrown");
        }

    }

}
