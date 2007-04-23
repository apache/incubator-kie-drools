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

import org.drools.DroolsTestCase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.rule.Rule;
import org.drools.ruleflow.common.instance.IProcessInstance;
import org.drools.ruleflow.core.IConnection;
import org.drools.ruleflow.core.IEndNode;
import org.drools.ruleflow.core.IJoin;
import org.drools.ruleflow.core.IRuleFlowProcess;
import org.drools.ruleflow.core.IRuleSetNode;
import org.drools.ruleflow.core.ISplit;
import org.drools.ruleflow.core.IStartNode;
import org.drools.ruleflow.core.impl.Connection;
import org.drools.ruleflow.core.impl.EndNode;
import org.drools.ruleflow.core.impl.Join;
import org.drools.ruleflow.core.impl.RuleFlowProcess;
import org.drools.ruleflow.core.impl.RuleSetNode;
import org.drools.ruleflow.core.impl.Split;
import org.drools.ruleflow.core.impl.StartNode;
import org.drools.ruleflow.instance.IRuleFlowProcessInstance;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstance;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;

/**
 * @author mproctor
 */

public class RuleFlowGroupTest extends DroolsTestCase {

    public void testRuleFlowGroup() {
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        final List list = new ArrayList();

        // create the consequence
        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -2596133893109870505L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                list.add( knowledgeHelper.getRule() );
            }
        };

        // create a rule for each rule flow groups
        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence );

        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs() );

        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setRuleFlowGroup( "rule-flow-group-1" );
        rule1.setConsequence( consequence );

        final RuleTerminalNode node1 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 2 ),
                                                             rule1,
                                                             rule1.getLhs() );

        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setRuleFlowGroup( "rule-flow-group-2" );
        rule2.setConsequence( consequence );
        rule2.setSalience( 10 );

        final RuleTerminalNode node2 = new RuleTerminalNode( 5,
                                                             new MockTupleSource( 2 ),
                                                             rule2,
                                                             rule2.getLhs() );

        final Rule rule3 = new Rule( "test-rule3" );
        rule3.setRuleFlowGroup( "rule-flow-group-3" );
        rule3.setConsequence( consequence );

        final RuleTerminalNode node3 = new RuleTerminalNode( 6,
                                                             new MockTupleSource( 2 ),
                                                             rule3,
                                                             rule3.getLhs() );

        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null );

        // nodes
        final IStartNode start = new StartNode();
        final IRuleSetNode ruleSet0 = new RuleSetNode();
        ruleSet0.setRuleFlowGroup( "rule-flow-group-0" );
        final IRuleSetNode ruleSet1 = new RuleSetNode();
        ruleSet1.setRuleFlowGroup( "rule-flow-group-1" );
        final IRuleSetNode ruleSet2 = new RuleSetNode();
        ruleSet2.setRuleFlowGroup( "rule-flow-group-2" );
        final IRuleSetNode ruleSet3 = new RuleSetNode();
        ruleSet3.setRuleFlowGroup( "rule-flow-group-3" );
        final ISplit split = new Split();
        split.setType( ISplit.TYPE_AND );
        final IJoin join = new Join();
        join.setType( IJoin.TYPE_AND );
        final IEndNode end = new EndNode();
        // connections
        new Connection( start,
                        ruleSet0,
                        IConnection.TYPE_NORMAL );
        new Connection( ruleSet0,
                        split,
                        IConnection.TYPE_NORMAL );
        new Connection( split,
                        ruleSet1,
                        IConnection.TYPE_NORMAL );
        new Connection( split,
                        ruleSet2,
                        IConnection.TYPE_NORMAL );
        new Connection( ruleSet1,
                        join,
                        IConnection.TYPE_NORMAL );
        new Connection( ruleSet2,
                        join,
                        IConnection.TYPE_NORMAL );
        new Connection( join,
                        ruleSet3,
                        IConnection.TYPE_NORMAL );
        new Connection( ruleSet3,
                        end,
                        IConnection.TYPE_NORMAL );

        // process
        final IRuleFlowProcess process = new RuleFlowProcess();
        process.addNode( start );
        process.addNode( ruleSet0 );
        process.addNode( ruleSet1 );
        process.addNode( ruleSet2 );
        process.addNode( ruleSet3 );
        process.addNode( split );
        process.addNode( join );
        process.addNode( end );

        // proces instance
        final IRuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setAgenda( agenda );
        processInstance.setProcess( process );
        assertEquals( IProcessInstance.STATE_PENDING,
                      processInstance.getState() );

        final RuleFlowGroupImpl ruleFlowGroup0 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-0" );
        final RuleFlowGroupImpl ruleFlowGroup1 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-1" );
        final RuleFlowGroupImpl ruleFlowGroup2 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-2" );
        final RuleFlowGroupImpl ruleFlowGroup3 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-3" );

        final ReteTuple tuple0 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node0.assertTuple( tuple0,
                           context0,
                           workingMemory );

        final ReteTuple tuple1 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node0.assertTuple( tuple1,
                           context0,
                           workingMemory );

        final ReteTuple tuple2 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node1.assertTuple( tuple2,
                           context0,
                           workingMemory );

        final ReteTuple tuple3 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node2.assertTuple( tuple3,
                           context0,
                           workingMemory );

        final ReteTuple tuple4 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );
        node3.assertTuple( tuple4,
                           context0,
                           workingMemory );

        // RuleFlowGroups should be populated, but the agenda shouldn't
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 1,
                      ruleFlowGroup3.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate process instance, the activations stay in the group,
        // but should now also be in the Agenda
        processInstance.start();
        assertEquals( IProcessInstance.STATE_ACTIVE,
                      processInstance.getState() );
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

        // on firing the last activation the child rule flow groups should 
        // activate and thus repopulate the agenda
        agenda.fireNextItem( null );
        workingMemory.executeQueuedActions();
        assertEquals( 0,
                      ruleFlowGroup0.size() );
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

        // executing rule1, which should activate AND-join and thus group 3 
        agenda.fireNextItem( null );
        workingMemory.executeQueuedActions();
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      ruleFlowGroup1.size() );
        assertEquals( 0,
                      ruleFlowGroup2.size() );
        assertEquals( 1,
                      ruleFlowGroup3.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // executing rule3, and finishing execution
        agenda.fireNextItem( null );
        workingMemory.executeQueuedActions();
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 0,
                      ruleFlowGroup1.size() );
        assertEquals( 0,
                      ruleFlowGroup2.size() );
        assertEquals( 0,
                      ruleFlowGroup3.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
        assertEquals( IProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
    }
}
