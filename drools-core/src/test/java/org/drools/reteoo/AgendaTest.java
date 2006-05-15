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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.spi.ActivationGroup;

/**
 * @author mproctor
 */

public class AgendaTest extends DroolsTestCase {
    public void testClearAgenda() {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule1 = new Rule( "test-rule1" );

        TerminalNode node1 = new TerminalNode( 3,
                                               new MockTupleSource( 2 ),
                                               rule1 );

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        null );

        // Add consequence. Notice here the context here for the add to ageyunda
        // is itself
        rule1.setConsequence( new org.drools.spi.Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        } );

        assertEquals( 0,
                      agenda.getFocus().size() );

        rule1.setNoLoop( false );
        node1.assertTuple( tuple,
                           context1,
                           workingMemory );

        // make sure we have an activation in the current focus
        assertEquals( 1,
                      agenda.getFocus().size() );

        agenda.clearAgenda();

        assertEquals( 0,
                      agenda.getFocus().size() );
    }

    public void testFilters() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );
        TerminalNode node = new TerminalNode( 3,
                                              new MockTupleSource( 2 ),
                                              rule );

        final Map results = new HashMap();
        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                results.put( "fired",
                             new Boolean( true ) );
            }
        } );

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       null );

        // test agenda is empty
        assertEquals( 0,
                      agenda.getFocus().size() );

        // True filter, activations should always add
        AgendaFilter filterTrue = new AgendaFilter() {
            public boolean accept(Activation item) {
                return true;
            }
        };

        rule.setNoLoop( false );
        node.assertTuple( tuple,
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

        // clear the agenda and the result map
        agenda.clearAgenda();
        results.clear();

        // False filter, activations should always be denied
        AgendaFilter filterFalse = new AgendaFilter() {
            public boolean accept(Activation item) {
                return false;
            }
        };

        rule.setNoLoop( false );
        node.assertTuple( tuple,
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
    }

    public void testFocusStack() throws ConsequenceException {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        //       // create the AgendaGroups
        AgendaGroupImpl agendaGroup1 = new AgendaGroupImpl( "agendaGroup1" );
        agenda.addAgendaGroup( agendaGroup1 );
        //        ActivationQueue queue1 = agendaGroup1.getActivationQueue( 0 );

        AgendaGroupImpl agendaGroup2 = new AgendaGroupImpl( "agendaGroup2" );
        agenda.addAgendaGroup( agendaGroup2 );
        //        ActivationQueue queue2 = agendaGroup2.getActivationQueue( 0 );

        AgendaGroupImpl agendaGroup3 = new AgendaGroupImpl( "agendaGroup3" );
        agenda.addAgendaGroup( agendaGroup3 );
        //        ActivationQueue queue3 = agendaGroup3.getActivationQueue( 0 );

        // create the consequence
        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );

        // create a rule for each agendaGroup
        Rule rule0 = new Rule( "test-rule0" );
        TerminalNode node0 = new TerminalNode( 3,
                                               new MockTupleSource( 2 ),
                                               rule0 );
        rule0.setConsequence( consequence );
        PropagationContext context0 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule0,
                                                                  null );

        Rule rule1 = new Rule( "test-rule1",
                               "agendaGroup1" );
        TerminalNode node1 = new TerminalNode( 5,
                                               new MockTupleSource( 4 ),
                                               rule1 );
        rule1.setConsequence( consequence );
        PropagationContext context1 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule1,
                                                                  null );

        Rule rule2 = new Rule( "test-rule2",
                               "agendaGroup2" );
        TerminalNode node2 = new TerminalNode( 7,
                                               new MockTupleSource( 6 ),
                                               rule2 );
        rule2.setConsequence( consequence );
        PropagationContext context2 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule2,
                                                                  null );

        Rule rule3 = new Rule( "test-rule3",
                               "agendaGroup3" );
        TerminalNode node3 = new TerminalNode( 9,
                                               new MockTupleSource( 8 ),
                                               rule3 );
        rule3.setConsequence( consequence );
        PropagationContext context3 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule3,
                                                                  null );

        // focus at this point is MAIN
        assertEquals( 0,
                      agenda.focusStackSize() );

        node0.assertTuple( tuple,
                           context0,
                           workingMemory );

        // check focus is main
        AgendaGroupImpl main = (AgendaGroupImpl) agenda.getAgendaGroup( AgendaGroup.MAIN );
        assertEquals( agenda.getFocus(),
                      main );
        // check main got the tuple
        assertEquals( 1,
                      agenda.getFocus().size() );
        node2.assertTuple( tuple,
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
        node2.assertTuple( tuple,
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
        node3.assertTuple( tuple,
                           context3,
                           workingMemory );

        assertEquals( 1,
                      agenda.getFocus().size() );

        node3.assertTuple( tuple,
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
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        // create the agendaGroup
        AgendaGroupImpl agendaGroup = new AgendaGroupImpl( "agendaGroup" );
        agenda.addAgendaGroup( agendaGroup );
        //        ActivationQueue queue = agendaGroup.getActivationQueue( 0 );

        // create the consequence
        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );

        // create a rule for the agendaGroup
        Rule rule = new Rule( "test-rule",
                              "agendaGroup" );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule );
        rule.setConsequence( consequence );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 rule,
                                                                 null );

        // first test that autoFocus=false works. Here the rule should not fire
        // as its agendaGroup does not have focus.
        rule.setAutoFocus( false );

        node.assertTuple( tuple,
                          context,
                          workingMemory );

        //        // check activation as added to the agendaGroup
        //        assertEquals( 1,
        //                      queue.size() );
        //
        //        // fire next item, agendaGroup should not fire as its not on the focus
        //        // stack
        //        // and thus should retain its sinle activation
        //        agenda.fireNextItem( null );
        //        assertEquals( 1,
        //                      queue.size() );
        //
        //        // Clear the agenda we we can test again
        //        agenda.clearAgenda();
        //        assertEquals( 0,
        //                      queue.size() );
        //
        //        // Now test that autoFocus=true works. Here the rule should fire as its
        //        // agendaGroup gets the focus when the activation is created.
        //        rule.setAutoFocus( true );
        //
        //        node.assertTuple( tuple,
        //                          context,
        //                          workingMemory );
        //
        //        assertEquals( 1,
        //                      queue.size() );
        //        agenda.fireNextItem( null );
        //        assertEquals( 0,
        //                      queue.size() );
    }
    
    public void testXorGroup() {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();
        
        final List list = new ArrayList();

        // create the consequence
        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add( knowledgeHelper.getRule() );
            }
        };

        ReteTuple tuple = new ReteTuple( new FactHandleImpl( 1 ) );

        // create a rule for each agendaGroup
        Rule rule0 = new Rule( "test-rule0" );       
        rule0.setXorGroup( "activation-group-0" );        
        TerminalNode node0 = new TerminalNode( 3,
                                               new MockTupleSource( 2 ),
                                               rule0 );
        rule0.setConsequence( consequence );
        PropagationContext context0 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule0,
                                                                  null );

        Rule rule1 = new Rule( "test-rule1" );
        rule1.setXorGroup( "activation-group-0" );
        TerminalNode node1 = new TerminalNode( 5,
                                               new MockTupleSource( 4 ),
                                               rule1 );
        rule1.setConsequence( consequence );
        PropagationContext context1 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule1,
                                                                  null );

        Rule rule2 = new Rule( "test-rule2" );
        TerminalNode node2 = new TerminalNode( 7,
                                               new MockTupleSource( 6 ),
                                               rule2 );
        rule2.setConsequence( consequence );
        PropagationContext context2 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule2,
                                                                  null );

        Rule rule3 = new Rule( "test-rule3",
                               "agendaGroup3" );
        rule3.setXorGroup( "activation-group-3" );
        TerminalNode node3 = new TerminalNode( 9,
                                               new MockTupleSource( 8 ),
                                               rule3 );
        rule3.setConsequence( consequence );
        PropagationContext context3 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule3,
                                                                  null );
        
        // Assert the tuple and check it was added to activation-group-0
        node0.assertTuple( tuple, context0, workingMemory );        
        ActivationGroup activationGroup0 = agenda.getActivationGroup( "activation-group-0" );
        assertEquals( 1, activationGroup0.size() );
        
        // Assert another tuple and check it was added to activation-group-0        
        node1.assertTuple( tuple, context1, workingMemory );        
        assertEquals( 2, activationGroup0.size() );
        
        // There should now be two potential activations to fire
        assertEquals( 2, agenda.focusStackSize() );
        
        // The first tuple should fire, adding itself to the List and clearing and cancelling the other Activations in the activation-group-0        
        agenda.fireNextItem( null );
        
        // Make sure the activation-group-0 is clear
        assertEquals( 0, activationGroup0.size() );
        
        // Make sure the Agenda  is  empty
        assertEquals( 0, agenda.focusStackSize() );
        
        // List should only have a single item, "rule0"
        assertEquals( 1, list.size() );
        assertSame( rule0, list.get( 0 ) );
        
        list.clear();
        
        //-------------------
        // Now try a more complex scenario involving  two Xor Groups and one  rule not in a Group
        node0.assertTuple( tuple, context0, workingMemory );
        node1.assertTuple( tuple, context1, workingMemory );        
        node2.assertTuple( tuple, context2, workingMemory );        
        node3.assertTuple( tuple, context3, workingMemory );        
        
        // activation-group-0 should be populated again
        assertEquals( 2, activationGroup0.size() ); 
        
        // make sure the activation-group-3 is cleared when we can clear the Agenda Group for the activation that is in both
        ActivationGroup activationGroup3 = agenda.getActivationGroup( "activation-group-3" );
        
        assertEquals( 4, agenda.agendaSize() );
        assertEquals( 1, activationGroup3.size() );
        
        agenda.clearAgendaGroup( "agendaGroup3" );
        assertEquals( 3, agenda.agendaSize() );
        assertEquals( 0, activationGroup3.size() );
        
        // Activation for activation-group-0 should be next - the activation in no activation/agenda group should remain on the agenda
        agenda.fireNextItem( null );
        assertEquals( 1, agenda.agendaSize() );
        assertEquals( 0, activationGroup0.size() );        
        
        
        // Fire  the  last activation and  make sure the Agenda Empties
        agenda.fireNextItem( null );
        assertEquals( 0, agenda.agendaSize() );
        
        assertEquals( 2, list.size() );
        assertEquals( rule0, list.get(0));
        assertEquals( rule2, list.get(1));        
        
    }

}
