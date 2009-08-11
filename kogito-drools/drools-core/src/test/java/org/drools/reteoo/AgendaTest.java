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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.base.SalienceInteger;
import org.drools.common.ArrayAgendaGroup;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.AgendaEventSupport;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.WorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.spi.Salience;

/**
 * @author mproctor
 */

public class AgendaTest extends DroolsTestCase {
    private InternalRuleBase ruleBase;
    private BuildContext     buildContext;

    protected void setUp() throws Exception {
        ruleBase = (InternalRuleBase) RuleBaseFactory.newRuleBase();
        buildContext = new BuildContext( ruleBase,
                                         ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
    }

    public void testClearAgenda() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        final Rule rule1 = new Rule( "test-rule1" );
        final Rule rule2 = new Rule( "test-rule2" );

        final RuleTerminalNode node1 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );

        final RuleTerminalNode node2 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 4 ),
                                                             rule2,
                                                             rule2.getLhs(),
                                                             buildContext );

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule1.setConsequence( new org.drools.spi.Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        } );

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule2.setConsequence( new org.drools.spi.Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        } );

        assertEquals( 0,
                      agenda.getFocus().size() );

        rule1.setNoLoop( false );
        rule2.setDuration( 5000 );

        node1.assertLeftTuple( tuple,
                               context1,
                               workingMemory );

        node2.assertLeftTuple( tuple,
                               context1,
                               workingMemory );

        // make sure we have an activation in the current focus
        assertEquals( 1,
                      agenda.getFocus().size() );

        assertEquals( 1,
                      agenda.getScheduledActivations().length );

        agenda.clearAndCancel();

        assertEquals( 0,
                      agenda.getFocus().size() );

        assertEquals( 0,
                      agenda.getScheduledActivations().length );
    }

    public void testFilters() throws Exception {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        
        final Boolean[] filtered = new Boolean[] { false };
        
        workingMemory.addEventListener( new DefaultAgendaEventListener() {
         
            public void activationCancelled(ActivationCancelledEvent event,
                                            WorkingMemory workingMemory) {
                if ( event.getCause() == ActivationCancelledCause.FILTER ) {
                    filtered[0] = true;
                }
            }
        });

        final Rule rule = new Rule( "test-rule" );
        final RuleTerminalNode node = new RuleTerminalNode( 3,
                                                            new MockTupleSource( 2 ),
                                                            rule,
                                                            rule.getLhs(),
                                                            buildContext );

        final Map results = new HashMap();
        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) {
                results.put( "fired",
                             new Boolean( true ) );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        } );

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               node,
                                               true );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null,
                                                                       null );

        // test agenda is empty
        assertEquals( 0,
                      agenda.getFocus().size() );

        // True filter, activations should always add
        final AgendaFilter filterTrue = new AgendaFilter() {
            public boolean accept(Activation item) {
                return true;
            }
        };

        rule.setNoLoop( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        // check there is an item to fire
        assertEquals( 1,
                      agenda.getFocus().size() );
        agenda.fireNextItem( filterTrue );

        // check focus is empty
        assertEquals( 0,
                      agenda.getFocus().size() );

        // make sure it also fired
        assertEquals( new Boolean( true ),
                      results.get( "fired" ) );
        
        assertEquals( false, filtered[0].booleanValue() );

        // clear the agenda and the result map
        agenda.clearAndCancel();
        results.clear();
        
        // False filter, activations should always be denied
        final AgendaFilter filterFalse = new AgendaFilter() {
            public boolean accept(Activation item) {
                return false;
            }
        };

        rule.setNoLoop( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        // check we have an item to fire
        assertEquals( 1,
                      agenda.getFocus().size() );
        agenda.fireNextItem( filterFalse );

        // make sure the focus is empty
        assertEquals( 0,
                      agenda.getFocus().size() );

        // check the consequence never fired
        assertNull( results.get( "fired" ) );
        
        assertEquals( true, filtered[0].booleanValue() );
    }

    public void testFocusStack() throws ConsequenceException {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        // create the consequence
        final Consequence consequence = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for each agendaGroup
        final Rule rule0 = new Rule( "test-rule0" );
        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );
        rule0.setConsequence( consequence );
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        final Rule rule1 = new Rule( "test-rule1",
                                     "agendaGroup1" );
        final RuleTerminalNode node1 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 4 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );
        rule1.setConsequence( consequence );
        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        final Rule rule2 = new Rule( "test-rule2",
                                     "agendaGroup2" );
        final RuleTerminalNode node2 = new RuleTerminalNode( 7,
                                                             new MockTupleSource( 6 ),
                                                             rule2,
                                                             rule2.getLhs(),
                                                             buildContext );
        rule2.setConsequence( consequence );
        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        null,
                                                                        null );

        final Rule rule3 = new Rule( "test-rule3",
                                     "agendaGroup3" );
        final RuleTerminalNode node3 = new RuleTerminalNode( 9,
                                                             new MockTupleSource( 8 ),
                                                             rule3,
                                                             rule3.getLhs(),
                                                             buildContext );
        rule3.setConsequence( consequence );
        final PropagationContext context3 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule3,
                                                                        null,
                                                                        null );

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create the AgendaGroups
        final AgendaGroup agendaGroup1 = new BinaryHeapQueueAgendaGroup( "agendaGroup1",
                                                                         ruleBase );
        agenda.addAgendaGroup( agendaGroup1 );

        final AgendaGroup agendaGroup2 = new BinaryHeapQueueAgendaGroup( "agendaGroup2",
                                                                         ruleBase );
        agenda.addAgendaGroup( agendaGroup2 );

        final AgendaGroup agendaGroup3 = new BinaryHeapQueueAgendaGroup( "agendaGroup3",
                                                                         ruleBase );
        agenda.addAgendaGroup( agendaGroup3 );

        // focus at this point is MAIN
        assertEquals( 0,
                      agenda.focusStackSize() );

        node0.assertLeftTuple( tuple,
                               context0,
                               workingMemory );

        // check focus is main
        final AgendaGroup main = agenda.getAgendaGroup( AgendaGroup.MAIN );
        assertEquals( agenda.getFocus(),
                      main );
        // check main got the tuple
        assertEquals( 1,
                      agenda.getFocus().size() );
        node2.assertLeftTuple( tuple,
                               context2,
                               workingMemory );

        // main is still focus and this tuple went to agendaGroup 2
        assertEquals( 1,
                      agenda.getFocus().size() );

        // check agendaGroup2 still got the tuple
        assertEquals( 1,
                      agendaGroup2.size() );

        // make sure total agenda size reflects this
        assertEquals( 2,
                      agenda.agendaSize() );

        // put another one on agendaGroup 2
        node2.assertLeftTuple( tuple,
                               context2,
                               workingMemory );

        // main is still focus so shouldn't have increased
        assertEquals( 1,
                      agenda.getFocus().size() );

        // check agendaGroup2 still got the tuple
        assertEquals( 2,
                      agendaGroup2.size() );

        // make sure total agenda size reflects this
        assertEquals( 3,
                      agenda.agendaSize() );

        // set the focus to agendaGroup1, note agendaGroup1 has no activations
        agenda.setFocus( "agendaGroup1" );
        // add agendaGroup2 onto the focus stack
        agenda.setFocus( "agendaGroup2" );
        // finally add agendaGroup3 to the top of the focus stack
        agenda.setFocus( "agendaGroup3" );

        // agendaGroup3, the current focus, has no activations
        assertEquals( 0,
                      agenda.getFocus().size() );

        // add to agendaGroup 3
        node3.assertLeftTuple( tuple,
                               context3,
                               workingMemory );

        assertEquals( 1,
                      agenda.getFocus().size() );

        node3.assertLeftTuple( tuple,
                               context3,
                               workingMemory );

        // agendaGroup3 now has 2 activations
        assertEquals( 2,
                      agenda.getFocus().size() );
        // check totalAgendaSize still works
        assertEquals( 5,
                      agenda.agendaSize() );

        // ok now lets check that stacks work with fireNextItem
        agenda.fireNextItem( null );

        // agendaGroup3 should still be the current agendaGroup
        assertEquals( agenda.getFocus(),
                      agendaGroup3 );
        // agendaGroup3 has gone from 2 to one activations
        assertEquals( 1,
                      agenda.getFocus().size() );
        // check totalAgendaSize has reduced too
        assertEquals( 4,
                      agenda.agendaSize() );

        // now repeat the process
        agenda.fireNextItem( null );

        // focus is still agendaGroup3, but now its empty
        assertEquals( agenda.getFocus(),
                      agendaGroup3 );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 3,
                      agenda.agendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        // agendaGroup3 is empty so it should be popped from the stack making````````````````````
        // agendaGroup2
        // the current agendaGroup
        assertEquals( agendaGroup2,
                      agenda.getFocus() );
        // agendaGroup2 had 2 activations, now it only has 1
        assertEquals( 1,
                      agenda.getFocus().size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        assertEquals( agenda.getFocus(),
                      agendaGroup2 );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // this last fire is more interesting as it demonstrates that
        // agendaGroup1 on
        // the stack before agendaGroup2 gets skipped as it has no activations
        agenda.fireNextItem( null );

        assertEquals( agenda.getFocus(),
                      main );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 0,
                      agenda.agendaSize() );

    }

    //
    public void testAutoFocus() throws ConsequenceException {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        // create the agendaGroup
        final AgendaGroup agendaGroup = new BinaryHeapQueueAgendaGroup( "agendaGroup",
                                                                        ruleBase );
        agenda.addAgendaGroup( agendaGroup );

        // create the consequence
        final Consequence consequence = new Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for the agendaGroup
        final Rule rule = new Rule( "test-rule",
                                    "agendaGroup" );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule,
                                                            rule.getLhs(),
                                                            buildContext );
        rule.setConsequence( consequence );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null,
                                                                       null );

        // first test that autoFocus=false works. Here the rule should not fire
        // as its agendaGroup does not have focus.
        rule.setAutoFocus( false );

        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        // check activation as added to the agendaGroup
        assertEquals( 1,
                      agendaGroup.size() );

        // fire next item, agendaGroup should not fire as its not on the focus stack
        // and thus should retain its sinle activation
        agenda.fireNextItem( null );
        assertEquals( 1,
                      agendaGroup.size() );

        // Clear the agenda we we can test again
        agenda.clearAndCancel();
        assertEquals( 0,
                      agendaGroup.size() );

        // Now test that autoFocus=true works. Here the rule should fire as its
        // agendaGroup gets the focus when the activation is created.
        rule.setAutoFocus( true );

        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );

        assertEquals( 1,
                      agendaGroup.size() );
        agenda.fireNextItem( null );
        assertEquals( 0,
                      agendaGroup.size() );
    }

    public void testAgendaGroupLockOnActive() {
        final InternalRuleBase ruleBase = (InternalRuleBase) RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        // create the agendaGroup
        final InternalAgendaGroup agendaGroup = new BinaryHeapQueueAgendaGroup( "agendaGroup",
                                                                                ruleBase );
        agenda.addAgendaGroup( agendaGroup );

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for the agendaGroup
        final Rule rule = new Rule( "test-rule",
                                    "agendaGroup" );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule,
                                                            rule.getLhs(),
                                                            buildContext );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null,
                                                                       null );

        // When both the rule is lock-on-active and the agenda group is active, activations should be ignored
        rule.setLockOnActive( true );
        agendaGroup.setActive( true );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        // activation should be ignored
        assertEquals( 0,
                      agendaGroup.size() );

        // lock-on-active is now false so activation should propagate
        rule.setLockOnActive( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        assertEquals( 1,
                      agendaGroup.size() );

        // even if lock-on-active is true, unless the agenda group is active the activation will still propagate
        rule.setLockOnActive( true );
        agendaGroup.setActive( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        assertEquals( 2,
                      agendaGroup.size() );
    }

    public void testActivationGroup() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        final List list = new ArrayList();

        // create the consequence
        final Consequence consequence = new Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add( knowledgeHelper.getRule() );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for each agendaGroup
        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setActivationGroup( "activation-group-0" );
        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );
        rule0.setConsequence( consequence );
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setActivationGroup( "activation-group-0" );
        final RuleTerminalNode node1 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 4 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );
        rule1.setConsequence( consequence );
        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setSalience( new SalienceInteger( -5 ) );
        final RuleTerminalNode node2 = new RuleTerminalNode( 7,
                                                             new MockTupleSource( 6 ),
                                                             rule2,
                                                             rule2.getLhs(),
                                                             buildContext );
        rule2.setConsequence( consequence );
        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        null,
                                                                        null );

        final Rule rule3 = new Rule( "test-rule3",
                                     "agendaGroup3" );
        rule3.setSalience( new SalienceInteger( -10 ) );
        rule3.setActivationGroup( "activation-group-3" );
        final RuleTerminalNode node3 = new RuleTerminalNode( 9,
                                                             new MockTupleSource( 8 ),
                                                             rule3,
                                                             rule3.getLhs(),
                                                             buildContext );
        rule3.setConsequence( consequence );
        final PropagationContext context3 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule3,
                                                                        null,
                                                                        null );

        // Assert the tuple and check it was added to activation-group-0
        node0.assertLeftTuple( tuple,
                               context0,
                               workingMemory );
        final ActivationGroup activationGroup0 = agenda.getActivationGroup( "activation-group-0" );
        assertEquals( 1,
                      activationGroup0.size() );

        // Removing a tuple should remove the activation from the activation-group-0 again
        node0.retractLeftTuple( tuple,
                                context0,
                                workingMemory );
        assertEquals( 0,
                      activationGroup0.size() );

        // Assert the tuple again and check it was added to activation-group-0
        node0.assertLeftTuple( tuple,
                               context0,
                               workingMemory );
        assertEquals( 1,
                      activationGroup0.size() );

        // Assert another tuple and check it was added to activation-group-0
        node1.assertLeftTuple( tuple,
                               context1,
                               workingMemory );
        assertEquals( 2,
                      activationGroup0.size() );

        // There should now be two potential activations to fire
        assertEquals( 2,
                      agenda.focusStackSize() );

        // The first tuple should fire, adding itself to the List and clearing and cancelling the other Activations in the activation-group-0
        agenda.fireNextItem( null );

        // Make sure the activation-group-0 is clear
        assertEquals( 0,
                      activationGroup0.size() );

        // Make sure the Agenda  is  empty
        assertEquals( 0,
                      agenda.focusStackSize() );

        // List should only have a single item, "rule0"
        assertEquals( 1,
                      list.size() );
        assertSame( rule1,
                    list.get( 0 ) );

        list.clear();

        //-------------------
        // Now try a more complex scenario involving  two Xor Groups and one  rule not in a Group
        node0.assertLeftTuple( tuple,
                               context0,
                               workingMemory );
        node1.assertLeftTuple( tuple,
                               context1,
                               workingMemory );
        node2.assertLeftTuple( tuple,
                               context2,
                               workingMemory );
        node3.assertLeftTuple( tuple,
                               context3,
                               workingMemory );

        // activation-group-0 should be populated again
        assertEquals( 2,
                      activationGroup0.size() );

        // make sure the activation-group-3 is cleared when we can clear the Agenda Group for the activation that is in both
        final ActivationGroup activationGroup3 = agenda.getActivationGroup( "activation-group-3" );

        assertEquals( 4,
                      agenda.agendaSize() );
        assertEquals( 1,
                      activationGroup3.size() );

        agenda.clearAndCancelAgendaGroup( "agendaGroup3" );
        assertEquals( 3,
                      agenda.agendaSize() );
        assertEquals( 0,
                      activationGroup3.size() );

        // Activation for activation-group-0 should be next - the activation in no activation/agenda group should remain on the agenda
        agenda.fireNextItem( null );
        assertEquals( 1,
                      agenda.agendaSize() );
        assertEquals( 0,
                      activationGroup0.size() );

        // Fire  the  last activation and  make sure the Agenda Empties
        agenda.fireNextItem( null );
        assertEquals( 0,
                      agenda.agendaSize() );

        assertEquals( 2,
                      list.size() );
        assertEquals( rule1,
                      list.get( 0 ) );
        assertEquals( rule2,
                      list.get( 1 ) );

    }

    /**
     * Basic RuleFlowGroup test where there are three rules, each in their own
     * RuleFlowGroup.  First only rule-flow-group-0 is activated and rule0 is
     * executed.  When the two remaining groups are activated, the rule with the
     * highest priority is executed first.
     */
    public void testRuleFlowGroup() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        final List list = new ArrayList();

        // create the consequence
        final Consequence consequence = new Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add( knowledgeHelper.getRule() );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        // create a rule for each rule flow groups
        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence );

        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );

        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setRuleFlowGroup( "rule-flow-group-1" );
        rule1.setConsequence( consequence );

        final RuleTerminalNode node1 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 2 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );

        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setRuleFlowGroup( "rule-flow-group-2" );
        rule2.setConsequence( consequence );
        rule2.setSalience( new SalienceInteger( 10 ) );

        final RuleTerminalNode node2 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 2 ),
                                                             rule2,
                                                             rule2.getLhs(),
                                                             buildContext );

        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );
        final RuleFlowGroup ruleFlowGroup1 = agenda.getRuleFlowGroup( "rule-flow-group-1" );
        final RuleFlowGroup ruleFlowGroup2 = agenda.getRuleFlowGroup( "rule-flow-group-2" );

        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );

        final LeftTuple tuple2 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node1.assertLeftTuple( tuple2,
                               context0,
                               workingMemory );

        final LeftTuple tuple3 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node2.assertLeftTuple( tuple3,
                               context0,
                               workingMemory );

        // RuleFlowGroups should be populated, but the agenda shouldn't be
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the nodes stay in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // As we fire each rule they are removed from both the Agenda and the RuleFlowGroup
        agenda.fireNextItem( null );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // After firing all activations of RuleFlowGroup 0, the agenda is empty
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Now we activate two RuleFlowGroups together
        // All their activations should be added to the agenda.
        agenda.activateRuleFlowGroup( "rule-flow-group-1" );
        agenda.activateRuleFlowGroup( "rule-flow-group-2" );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // we set the salience higher on rule2, so it sould fire first and empty ruleFlowGroup2
        agenda.fireNextItem( null );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 0,
                      ruleFlowGroup2.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // this is the last activation, so everything should be empty after this
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      ruleFlowGroup1.size() );
        assertEquals( 0,
                      ruleFlowGroup2.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
    }

    /**
     * RuleFlowGroup test that makes sure that, if new activations are created
     * for an active RuleFlowGroup, those activations get added to the agenda
     * directly as well.
     */
    public void testRuleFlowGroup1() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create rule1
        final Consequence consequence1 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setRuleFlowGroup( "rule-flow-group-0" );
        rule1.setConsequence( consequence1 );

        final RuleTerminalNode node1 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 2 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );

        // create context
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // activate rule1
                final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                               "cheese" ),
                                                        null,
                                                        true );
                node1.assertLeftTuple( tuple1,
                                       context0,
                                       workingMemory );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence0 );

        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );

        // Create one activation for rule0 only
        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the activation stays in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // As we fire the rule, an new activation is created for rule1, and it should be added to group AND the agenda.
        agenda.fireNextItem( null );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // After firing all activations of RuleFlowGroup 0, the agenda is empty
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
    }

    /**
     * RuleFlowGroup test that makes sure that, if an activation in an active
     * RuleFlowGroup gets deactivated, the activation is no longer executed.
     */
    public void testRuleFlowGroup2() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create rule1
        final Consequence consequence1 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setRuleFlowGroup( "rule-flow-group-0" );
        rule1.setConsequence( consequence1 );

        final RuleTerminalNode node1 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 2 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );

        // create context
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // deactivate rule1
                node1.retractLeftTuple( tuple1,
                                        context0,
                                        workingMemory );
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence0 );
        rule0.setSalience( new SalienceInteger( 10 ) );

        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );

        // Create an activation for both rules
        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        node1.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the activations stay in the group, but should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // As we fire the rule, rule0 should execute first, as it has higher salience.
        // Rule0 should deactivate rule1 as well, so the everything should be empty
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

    }

    /**
     * RuleFlowGroup test that makes sure that, when deactivating a RuleFlowGroup,
     * all activations for that group are no longer on the agenda.  When
     * reactivating the RuleFlowGroup however, they get added to the agenda again.
     */
    public void testRuleFlowGroup3() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence0 );

        final RuleTerminalNode node0 = new RuleTerminalNode( 1,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );

        // create context
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        // Create two activation for this rule
        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );
        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the activations stay in the group, but
        // should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // Reactivate an already active RuleFlowGroup should not have any effect
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // Deactivate the RuleFlowGroup, the activations should be removed from
        // the agenda but still in the RuleFlowGroup
        agenda.deactivateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Reactivate the RuleFlowGroup, the activations stay in the group, but
        // should now also be in the Agenda again
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 2,
                      agenda.agendaSize() );

    }

    /**
     * Test auto-deactivation of RuleFlowGroup.
     */
    public void testRuleFlowGroup4() {
        ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        IdGenerator idGenerator = ruleBase.getReteooBuilder().getIdGenerator();
        final InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newStatefulSession();;

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence0 );

        final RuleTerminalNode node0 = new RuleTerminalNode( idGenerator.getNextId(),
                                                             new MockTupleSource( idGenerator.getNextId() ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );
        assertTrue( ruleFlowGroup0.isAutoDeactivate() );
        ruleFlowGroup0.setAutoDeactivate( false );
        assertFalse( ruleFlowGroup0.isAutoDeactivate() );

        // create context
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        // Create an activation for this rule
        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        // RuleFlowGroup should be populated, but the agenda shouldn't be
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the activations stay in the group, but
        // should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // Execute activation
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
        assertTrue( ruleFlowGroup0.isActive() );

        // Set auto-deactivation status to true
        ruleFlowGroup0.setAutoDeactivate( true );
        assertTrue( ruleFlowGroup0.isAutoDeactivate() );
        assertFalse( ruleFlowGroup0.isActive() );

        // Add another activation and activate RuleFlowGroup again
        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      agenda.agendaSize() );
        assertTrue( ruleFlowGroup0.isActive() );

        // Execute the activation, the RuleFlowGroup should automatically deactivate
        agenda.fireNextItem( null );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
        workingMemory.executeQueuedActions();
        assertFalse( ruleFlowGroup0.isActive() );

        // A new activation should now be added to the RuleFlowGroup but not to the agenda
        final LeftTuple tuple2 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true );
        node0.assertLeftTuple( tuple2,
                               context0,
                               workingMemory );
        assertEquals( 1,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
    }

    /**
     * Test auto-deactivation of empty ruleflow group.
     */
    public void testRuleFlowGroup5() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create rule0
        final Consequence consequence0 = new Consequence() {
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory w) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence0 );

        final RuleFlowGroup ruleFlowGroup0 = agenda.getRuleFlowGroup( "rule-flow-group-0" );
        assertTrue( ruleFlowGroup0.isAutoDeactivate() );

        // RuleFlowGroup should be empty, as well as the agenda
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate the RuleFlowGroup, the activations stay in the group, but
        // should now also be in the Agenda
        agenda.activateRuleFlowGroup( "rule-flow-group-0" );
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
        workingMemory.executeQueuedActions();

        assertFalse( ruleFlowGroup0.isActive() );
    }

    public void testRuleFlowGroupLockOnActive() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        // create the agendaGroup
        //final AgendaGroupImpl agendaGroup = new AgendaGroupImpl( "agendaGroup" );
        //agenda.addAgendaGroup( agendaGroup );

        final RuleFlowGroupImpl ruleFlowGroup = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-0" );

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for the agendaGroup
        final Rule rule = new Rule( "test-rule" );
        rule.setRuleFlowGroup( "rule-flow-group-0" );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule,
                                                            rule.getLhs(),
                                                            buildContext );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null,
                                                                       null );

        // When both the rule is lock-on-active and the agenda group is active, activations should be ignored
        rule.setLockOnActive( true );
        ruleFlowGroup.setAutoDeactivate( false );
        ruleFlowGroup.setActive( true );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        // activation should be ignored
        assertEquals( 0,
                      ruleFlowGroup.size() );

        // lock-on-active is now false so activation should propagate
        rule.setLockOnActive( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        assertEquals( 1,
                      ruleFlowGroup.size() );

        // even if lock-on-active is true, unless the agenda group is active the activation will still propagate
        rule.setLockOnActive( true );
        ruleFlowGroup.setActive( false );
        node.assertLeftTuple( tuple,
                              context,
                              workingMemory );
        assertEquals( 2,
                      ruleFlowGroup.size() );
    }

    public void testSequentialAgenda() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        InternalRuleBase ruleBase = (InternalRuleBase) RuleBaseFactory.newRuleBase( conf );

        // create the consequence
        final Consequence consequence = new Consequence() {
            /**
             *
             */
            private static final long serialVersionUID = 400L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }

            public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
        };

        final LeftTuple tuple = new LeftTuple( new DefaultFactHandle( 1,
                                                                      "cheese" ),
                                               null,
                                               true );

        // create a rule for each agendaGroup
        final Rule rule0 = new Rule( "test-rule0" );
        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext );
        node0.setSequence( 72 );
        rule0.setConsequence( consequence );
        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null,
                                                                        null );

        final Rule rule1 = new Rule( "test-rule1",
                                     "agendaGroup1" );
        final RuleTerminalNode node1 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 4 ),
                                                             rule1,
                                                             rule1.getLhs(),
                                                             buildContext );
        node1.setSequence( 10 );
        rule1.setConsequence( consequence );
        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null,
                                                                        null );

        final Rule rule2 = new Rule( "test-rule2",
                                     "agendaGroup1" );
        final RuleTerminalNode node2 = new RuleTerminalNode( 7,
                                                             new MockTupleSource( 6 ),
                                                             rule2,
                                                             rule2.getLhs(),
                                                             buildContext );
        node2.setSequence( 7 );
        rule2.setConsequence( consequence );
        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        null,
                                                                        null );

        final Rule rule3 = new Rule( "test-rule3",
                                     "agendaGroup2" );
        final RuleTerminalNode node3 = new RuleTerminalNode( 9,
                                                             new MockTupleSource( 8 ),
                                                             rule3,
                                                             rule3.getLhs(),
                                                             buildContext );
        node3.setSequence( 0 );
        rule3.setConsequence( consequence );
        final PropagationContext context3 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule3,
                                                                        null,
                                                                        null );

        ruleBase.getAgendaGroupRuleTotals().put( "MAIN",
                                                 new Integer( 100 ) );
        ruleBase.getAgendaGroupRuleTotals().put( "agendaGroup1",
                                                 new Integer( 10 ) );
        ruleBase.getAgendaGroupRuleTotals().put( "agendaGroup2",
                                                 new Integer( 1 ) );

        InternalWorkingMemory workingMemory = new ReteooWorkingMemory( 0,
                                                                       ruleBase );

        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();

        final AgendaGroup agendaGroup1 = new ArrayAgendaGroup( "agendaGroup1",
                                                               ruleBase );
        agenda.addAgendaGroup( agendaGroup1 );

        final AgendaGroup agendaGroup2 = new ArrayAgendaGroup( "agendaGroup2",
                                                               ruleBase );
        agenda.addAgendaGroup( agendaGroup2 );

        // focus at this point is MAIN
        assertEquals( 0,
                      agenda.focusStackSize() );

        node0.assertLeftTuple( tuple,
                               context0,
                               workingMemory );

        // check focus is main
        final AgendaGroup main = agenda.getAgendaGroup( AgendaGroup.MAIN );
        assertEquals( agenda.getFocus(),
                      main );
        // check main got the tuple
        assertEquals( 1,
                      agenda.getFocus().size() );
        node2.assertLeftTuple( tuple,
                               context2,
                               workingMemory );

        // main is still focus and this tuple went to agendaGroup1
        assertEquals( 1,
                      agenda.getFocus().size() );

        // check agendaGroup1 still got the tuple
        assertEquals( 1,
                      agendaGroup1.size() );

        // make sure total agenda size reflects this
        assertEquals( 2,
                      agenda.agendaSize() );

        // put another one on agendaGroup 1
        node2.assertLeftTuple( tuple,
                               context2,
                               workingMemory );

        // main is still focus so shouldn't have increased
        assertEquals( 1,
                      agenda.getFocus().size() );

        // check agendaGroup2 still got the tuple
        assertEquals( 2,
                      agendaGroup1.size() );

        // make sure total agenda size reflects this
        assertEquals( 3,
                      agenda.agendaSize() );

        // set the focus to agendaGroup1, note agendaGroup1 has no activations
        agenda.setFocus( "agendaGroup1" );
        // add agendaGroup2 onto the focus stack
        agenda.setFocus( "agendaGroup2" );

        // agendaGroup2, the current focus, has no activations
        assertEquals( 0,
                      agenda.getFocus().size() );

        // add to agendaGroup2
        node3.assertLeftTuple( tuple,
                               context3,
                               workingMemory );

        assertEquals( 1,
                      agenda.getFocus().size() );

        node3.assertLeftTuple( tuple,
                               context3,
                               workingMemory );

        // agendaGroup2 now has 2 activations
        assertEquals( 2,
                      agenda.getFocus().size() );

        // check totalAgendaSize still works
        assertEquals( 5,
                      agenda.agendaSize() );

        // ok now lets check that stacks work with fireNextItem
        agenda.fireNextItem( null );

        // agendaGroup2 should still be the current agendaGroup
        assertEquals( agendaGroup2,
                      agenda.getFocus() );
        // agendaGroup2 has gone from 2 to one activations
        assertEquals( 1,
                      agenda.getFocus().size() );
        // check totalAgendaSize has reduced too
        assertEquals( 4,
                      agenda.agendaSize() );

        // now repeat the process
        agenda.fireNextItem( null );

        // focus is still agendaGroup2, but now its empty
        assertEquals( agendaGroup2,
                      agenda.getFocus() );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 3,
                      agenda.agendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        // agendaGroup2 is empty so it should be popped from the stack making agendaGroup1 the current agendaGroup
        assertEquals( agendaGroup1,
                      agenda.getFocus() );
        // agendaGroup1 had 2 activations, now it only has 1
        assertEquals( 1,
                      agenda.getFocus().size() );
        assertEquals( 2,
                      agenda.agendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        assertEquals( agendaGroup1,
                      agenda.getFocus() );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // this last fire is more interesting as it demonstrates that
        // agendaGroup1 on
        // the stack before agendaGroup2 gets skipped as it has no activations
        agenda.fireNextItem( null );

        assertEquals( agenda.getFocus(),
                      main );
        assertEquals( 0,
                      agenda.getFocus().size() );
        assertEquals( 0,
                      agenda.agendaSize() );

    }
    
    public void testNullErrorOnGetScheduledActivations() { 
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();
        try {
            ((DefaultAgenda) workingMemory.getAgenda()).getScheduledActivations();
        } catch ( NullPointerException e ) {
            fail( "Exception Should not have been thrown" );
        }
        
    }

}
