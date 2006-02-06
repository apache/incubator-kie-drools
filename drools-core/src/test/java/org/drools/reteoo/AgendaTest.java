package org.drools.reteoo;

/*
 * $Id: AgendaTest.java,v 1.4 2005/08/16 22:55:37 mproctor Exp $
 *
 * Copyright 2004-2005 (C) The Werken Company. All Rights Reserved.
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
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
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

import java.util.HashMap;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;

/**
 * @author mproctor
 */

public class AgendaTest extends DroolsTestCase {
    PropagationContext initContext = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

    public void testAddToAgenda() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule1 = new Rule( "test-rule1" );

        final Rule rule2 = new Rule( "test-rule2" );

        final Map results = new HashMap();

        final ReteTuple tuple = new ReteTuple( 0,
                                               new FactHandleImpl( 1 ),
                                               workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        new AgendaItem( 0,
                                                                                        tuple,
                                                                                        this.initContext,
                                                                                        rule1 ) );

        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule2,
                                                                        new AgendaItem( 0,
                                                                                        tuple,
                                                                                        this.initContext,
                                                                                        rule2 ) );

        // Add consequence.
        rule1.setConsequence( new org.drools.spi.Consequence() {
            public void invoke(Activation activation, WorkingMemory workingMemory) {
                /*
                 * context1 one shows we are adding to the agenda where the rule
                 * is the same as its propogation context
                 */
                agenda.addToAgenda( (ReteTuple) tuple,
                                    context1,
                                    rule1 );
                results.put( "fired",
                             new Boolean( true ) );
            }
        } );
        /* make sure the focus is empty */
        assertEquals( 0,
                      agenda.focusSize() );

        // This is not recursive so a rule should not be able to activate itself
        // Notice here the context is the other rule, so should add this time.
        rule1.setNoLoop( true );
        agenda.addToAgenda( tuple,
                            context2,
                            rule1 );
        // check tuple was added to the focus
        assertEquals( 1,
                      agenda.focusSize() );
        agenda.fireNextItem( null );

        // make sure it fired
        assertEquals( new Boolean( true ),
                      results.get( "fired" ) );

         // the addToAgenda in the consequence should fail as the context is the
         // same as the current rule
        assertEquals( 0,
                      agenda.focusSize() );

        // reset agenda and results map
        agenda.clearAgenda();
        results.clear();

        // This is recursive so a rule should be able to activate itself
        rule1.setNoLoop( false );
        agenda.addToAgenda( tuple,
                            context2,
                            rule1 );
        assertEquals( 1,
                      agenda.focusSize() );
        agenda.fireNextItem( null );
        // check rule fired
        assertEquals( new Boolean( true ),
                      results.get( "fired" ) );
        /* check rule was able to add itself to the agenda */
        assertEquals( 1,
                      agenda.focusSize() );
    }

    public void testClearAgenda() {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule1 = new Rule( "test-rule1" );

        ReteTuple tuple = new ReteTuple( 0,
                                         new FactHandleImpl( 1 ),
                                         workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule1,
                                                                        new AgendaItem( 0,
                                                                                        tuple,
                                                                                        this.initContext,
                                                                                        rule1 ) );

         // Add consequence. Notice here the context here for the add to agenda
         // is itself
        rule1.setConsequence( new org.drools.spi.Consequence() {
            public void invoke(Activation activation, WorkingMemory workingMemory) {
                // do nothing
            }
        } );

        assertEquals( 0,
                      agenda.focusSize() );

        rule1.setNoLoop( false );
        agenda.addToAgenda( tuple,
                            context1,
                            rule1 );
        // make sure we have an activation in the current focus
        assertEquals( 1,
                      agenda.focusSize() );

        agenda.clearAgenda();

        assertEquals( 0,
                      agenda.focusSize() );
    }

    public void testFilters() throws Exception {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();
        final Agenda agenda = workingMemory.getAgenda();

        final Rule rule = new Rule( "test-rule" );

        final Map results = new HashMap();
        // add consequence
        rule.setConsequence( new org.drools.spi.Consequence() {
            public void invoke(Activation activation, WorkingMemory workingMemory) {
                results.put( "fired",
                             new Boolean( true ) );
            }
        } );

        ReteTuple tuple = new ReteTuple( 0,
                                         new FactHandleImpl( 1 ),
                                         workingMemory );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       rule,
                                                                       new AgendaItem( 0,
                                                                                       tuple,
                                                                                       this.initContext,
                                                                                       rule ) );

        // test agenda is empty
        assertEquals( 0,
                      agenda.focusSize() );

        // True filter, activations should always add
        AgendaFilter filterTrue = new AgendaFilter() {
            public boolean accept(Activation item) {
                return true;
            }
        };
        
        rule.setNoLoop( false );
        agenda.addToAgenda( tuple,
                            context,
                            rule );
        // check there is an item to fire
        assertEquals( 1,
                      agenda.focusSize() );
        agenda.fireNextItem( filterTrue );

        // check focus is empty
        assertEquals( 0,
                      agenda.focusSize() );

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
        agenda.addToAgenda( tuple,
                            context,
                            rule );
        // check we have an item to fire
        assertEquals( 1,
                      agenda.focusSize() );
        agenda.fireNextItem( filterFalse );
        
        // make sure the focus is empty
        assertEquals( 0,
                      agenda.focusSize() );

        // check the consequence never fired 
        assertNull( results.get( "fired" ) );
    }

    public void testFocusStack() throws ConsequenceException {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        // create the AgendaGroups
        AgendaGroupImpl agendaGroup1 = new AgendaGroupImpl( "agendaGroup1",
                                             ruleBase.getConflictResolver() );
        agenda.addAgendaGroup( agendaGroup1 );

        AgendaGroupImpl agendaGroup2 = new AgendaGroupImpl( "agendaGroup2",
                                             ruleBase.getConflictResolver() );
        agenda.addAgendaGroup( agendaGroup2 );

        AgendaGroupImpl agendaGroup3 = new AgendaGroupImpl( "agendaGroup3",
                                             ruleBase.getConflictResolver() );
        agenda.addAgendaGroup( agendaGroup3 );

        // create the consequence
        Consequence consequence = new Consequence() {
            public void invoke(Activation activation, WorkingMemory workingMemory) {
                // do nothing
            }
        };

        ReteTuple tuple = new ReteTuple( 0,
                                         new FactHandleImpl( 1 ),
                                         workingMemory );

        // create a rule for each agendaGroup
        Rule rule0 = new Rule( "test-rule0" );
        rule0.setConsequence( consequence );
        PropagationContext context0 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule0,
                                                                  new AgendaItem( 0,
                                                                                  tuple,
                                                                                  this.initContext,
                                                                                  rule0 ) );

        Rule rule1 = new Rule( "test-rule1",
                               "agendaGroup1" );
        rule1.setConsequence( consequence );
        PropagationContext context1 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule1,
                                                                  new AgendaItem( 0,
                                                                                  tuple,
                                                                                  this.initContext,
                                                                                  rule0 ) );

        Rule rule2 = new Rule( "test-rule2",
                               "agendaGroup2" );
        rule2.setConsequence( consequence );
        PropagationContext context2 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule2,
                                                                  new AgendaItem( 0,
                                                                                  tuple,
                                                                                  this.initContext,
                                                                                  rule0 ) );

        Rule rule3 = new Rule( "test-rule3",
                               "agendaGroup3" );
        rule3.setConsequence( consequence );
        PropagationContext context3 = new PropagationContextImpl( 0,
                                                                  PropagationContext.ASSERTION,
                                                                  rule3,
                                                                  new AgendaItem( 0,
                                                                                  tuple,
                                                                                  this.initContext,
                                                                                  rule0 ) );

        // focus at this point is MAIN
        assertEquals( 0,
                      agenda.focusSize() );

        agenda.addToAgenda( tuple,
                            context0,
                            rule0 );

        // check focus is main
        AgendaGroupImpl main = (AgendaGroupImpl) agenda.getAgendaGroup( AgendaGroup.MAIN );
        assertEquals( agenda.getFocus(),
                      main );
        // check main got the tuple
        assertEquals( 1,
                      agenda.focusSize() );

        agenda.addToAgenda( tuple,
                            context2,
                            rule2 );
        // main is still focus and this tuple went to agendaGroup 2
        assertEquals( 1,
                      agenda.focusSize() );

        // check agendaGroup2 still got the tuple 
        assertEquals( 1,
                      agendaGroup2.getPriorityQueue().size() );

        // make sure total agenda size reflects this
        assertEquals( 2,
                      agenda.totalAgendaSize() );

        // put another one on agendaGroup 2 
        agenda.addToAgenda( tuple,
                            context2,
                            rule2 );

        // main is still focus so shouldn't have increased 
        assertEquals( 1,
                      agenda.focusSize() );

        // check agendaGroup2 still got the tuple
        assertEquals( 2,
                      agendaGroup2.getPriorityQueue().size() );

        // make sure total agenda size reflects this 
        assertEquals( 3,
                      agenda.totalAgendaSize() );

        // set the focus to agendaGroup1, note agendaGroup1 has no activations
        agenda.setFocus( "agendaGroup1" );
        // add agendaGroup2 onto the focus stack 
        agenda.setFocus( "agendaGroup2" );
        // finally add agendaGroup3 to the top of the focus stack 
        agenda.setFocus( "agendaGroup3" );

        // agendaGroup3, the current focus, has no activations 
        assertEquals( 0,
                      agenda.focusSize() );

        // add to agendaGroup 3
        agenda.addToAgenda( tuple,
                            context3,
                            rule3 );
        assertEquals( 1,
                      agenda.focusSize() );

        agenda.addToAgenda( tuple,
                            context3,
                            rule3 );

        // agendaGroup3 now has 2 activations
        assertEquals( 2,
                      agenda.focusSize() );
        // check totalAgendaSize still works 
        assertEquals( 5,
                      agenda.totalAgendaSize() );

        // ok now lets check that stacks work with fireNextItem 
        agenda.fireNextItem( null );

        // agendaGroup3 should still be the current agendaGroup 
        assertEquals( agenda.getFocus(),
                      agendaGroup3 );
        // agendaGroup3 has gone from 2 to one activations 
        assertEquals( 1,
                      agenda.focusSize() );
        // check totalAgendaSize has reduced too 
        assertEquals( 4,
                      agenda.totalAgendaSize() );

        // now repeat the process
        agenda.fireNextItem( null );

        // focus is still agendaGroup3, but now its empty 
        assertEquals( agenda.getFocus(),
                      agendaGroup3 );
        assertEquals( 0,
                      agenda.focusSize() );
        assertEquals( 3,
                      agenda.totalAgendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        // agendaGroup3 is empty so it should be popped from the stack making agendaGroup2
        // the current agendaGroup
        assertEquals( agenda.getFocus(),
                      agendaGroup2 );
        // agendaGroup2 had 2 activations, now it only has 1
        assertEquals( 1,
                      agenda.focusSize() );
        assertEquals( 2,
                      agenda.totalAgendaSize() );

        // repeat fire again
        agenda.fireNextItem( null );

        assertEquals( agenda.getFocus(),
                      agendaGroup2 );
        assertEquals( 0,
                      agenda.focusSize() );
        assertEquals( 1,
                      agenda.totalAgendaSize() );

         // this last fire is more interesting as it demonstrates that agendaGroup1 on
         // the stack before agendaGroup2 gets skipped as it has no activations
        agenda.fireNextItem( null );

        assertEquals( agenda.getFocus(),
                      main );
        assertEquals( 0,
                      agenda.focusSize() );
        assertEquals( 0,
                      agenda.totalAgendaSize() );

    }

    public void testAutoFocus() throws ConsequenceException {
        RuleBase ruleBase = new RuleBaseImpl();

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        // create the agendaGroup
        AgendaGroupImpl agendaGroup = new AgendaGroupImpl( "agendaGroup",
                                            ruleBase.getConflictResolver() );
        agenda.addAgendaGroup( agendaGroup );

        // create the consequence
        Consequence consequence = new Consequence() {
            public void invoke(Activation activation, WorkingMemory workingMemory) {
                // do nothing
            }
        };

        ReteTuple tuple = new ReteTuple( 0,
                                         new FactHandleImpl( 1 ),
                                         workingMemory );

        // create a rule for the agendaGroup 
        Rule rule = new Rule( "test-rule",
                              "agendaGroup" );
        rule.setConsequence( consequence );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 rule,
                                                                 new AgendaItem( 0,
                                                                                 tuple,
                                                                                 this.initContext,
                                                                                 rule ) );

        // first test that autoFocus=false works. Here the rule should not fire
        // as its agendaGroup does not have focus.
        rule.setAutoFocus( false );

        agenda.addToAgenda( tuple,
                            context,
                            rule );

        // check activation as added to the agendaGroup
        assertEquals( 1,
                      agendaGroup.getPriorityQueue().size() );

        // fire next item, agendaGroup should not fire as its not on the focus stack
        // and thus should retain its sinle activation
        agenda.fireNextItem( null );
        assertEquals( 1,
                      agendaGroup.getPriorityQueue().size() );

        // Clear the agenda we we can test again
        agenda.clearAgenda();
        assertEquals( 0,
                      agendaGroup.getPriorityQueue().size() );

        // Now test that autoFocus=true works. Here the rule should fire as its
        // agendaGroup gets the focus when the activation is created.
        rule.setAutoFocus( true );

        agenda.addToAgenda( tuple,
                            context,
                            rule );

        assertEquals( 1,
                      agendaGroup.getPriorityQueue().size() );
        agenda.fireNextItem( null );
        assertEquals( 0,
                      agendaGroup.getPriorityQueue().size() );
    }
}
