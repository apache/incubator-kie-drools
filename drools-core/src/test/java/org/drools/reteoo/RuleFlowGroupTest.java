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
import org.drools.base.SalienceInteger;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Constraint;
import org.drools.ruleflow.core.EndNode;
import org.drools.ruleflow.core.Join;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.RuleSetNode;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.StartNode;
import org.drools.ruleflow.core.impl.ConnectionImpl;
import org.drools.ruleflow.core.impl.EndNodeImpl;
import org.drools.ruleflow.core.impl.JoinImpl;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.RuleSetNodeImpl;
import org.drools.ruleflow.core.impl.SplitImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstanceImpl;
import org.drools.ruleflow.nodes.split.ConstraintEvaluator;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;

/**
 * @author mproctor
 */

public class RuleFlowGroupTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext buildContext;
    
    protected void setUp() throws Exception {
        ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
    }
    
    public void testRuleFlowGroup() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

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
        };

        // create a rule for each rule flow groups
        final Rule rule0 = new Rule( "test-rule0" );
        rule0.setRuleFlowGroup( "rule-flow-group-0" );
        rule0.setConsequence( consequence );

        final RuleTerminalNode node0 = new RuleTerminalNode( 3,
                                                             new MockTupleSource( 2 ),
                                                             rule0,
                                                             rule0.getLhs(),
                                                             buildContext);

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

        final Rule rule3 = new Rule( "test-rule3" );
        rule3.setRuleFlowGroup( "rule-flow-group-3" );
        rule3.setConsequence( consequence );

        final RuleTerminalNode node3 = new RuleTerminalNode( 6,
                                                             new MockTupleSource( 2 ),
                                                             rule3,
                                                             rule3.getLhs(),
                                                             buildContext );

        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null );

        // nodes
        final StartNode start = new StartNodeImpl();
        final RuleSetNode ruleSet0 = new RuleSetNodeImpl();
        ruleSet0.setRuleFlowGroup( "rule-flow-group-0" );
        final RuleSetNode ruleSet1 = new RuleSetNodeImpl();
        ruleSet1.setRuleFlowGroup( "rule-flow-group-1" );
        final RuleSetNode ruleSet2 = new RuleSetNodeImpl();
        ruleSet2.setRuleFlowGroup( "rule-flow-group-2" );
        final RuleSetNode ruleSet3 = new RuleSetNodeImpl();
        ruleSet3.setRuleFlowGroup( "rule-flow-group-3" );
        final Split split = new SplitImpl();
        split.setType( Split.TYPE_AND );
        final Join join = new JoinImpl();
        join.setType( Join.TYPE_AND );
        final EndNode end = new EndNodeImpl();
        // connections
        new ConnectionImpl( start,
                        ruleSet0,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet0,
                        split,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( split,
                        ruleSet1,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( split,
                        ruleSet2,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet1,
                        join,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet2,
                        join,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( join,
                        ruleSet3,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet3,
                        end,
                        Connection.TYPE_NORMAL );

        // process
        final RuleFlowProcess process = new RuleFlowProcessImpl();
        process.addNode( start );
        process.addNode( ruleSet0 );
        process.addNode( ruleSet1 );
        process.addNode( ruleSet2 );
        process.addNode( ruleSet3 );
        process.addNode( split );
        process.addNode( join );
        process.addNode( end );

        // proces instance
        final RuleFlowProcessInstance processInstance = new RuleFlowProcessInstanceImpl();
        processInstance.setWorkingMemory( workingMemory );
        processInstance.setProcess( process );
        assertEquals( ProcessInstance.STATE_PENDING,
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
        assertEquals( ProcessInstance.STATE_ACTIVE,
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
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
    }
    
    /** XOR split and join */
    public void testRuleFlowGroup2() {
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

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

        final Rule rule3 = new Rule( "test-rule3" );
        rule3.setRuleFlowGroup( "rule-flow-group-3" );
        rule3.setConsequence( consequence );

        final RuleTerminalNode node3 = new RuleTerminalNode( 6,
                                                             new MockTupleSource( 2 ),
                                                             rule3,
                                                             rule3.getLhs(),
                                                             buildContext );

        final PropagationContext context0 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        rule0,
                                                                        null );

        // nodes
        final StartNode start = new StartNodeImpl();
        final RuleSetNode ruleSet0 = new RuleSetNodeImpl();
        ruleSet0.setRuleFlowGroup( "rule-flow-group-0" );
        final RuleSetNode ruleSet1 = new RuleSetNodeImpl();
        ruleSet1.setRuleFlowGroup( "rule-flow-group-1" );
        final RuleSetNode ruleSet2 = new RuleSetNodeImpl();
        ruleSet2.setRuleFlowGroup( "rule-flow-group-2" );
        final RuleSetNode ruleSet3 = new RuleSetNodeImpl();
        ruleSet3.setRuleFlowGroup( "rule-flow-group-3" );
        final Split split = new SplitImpl();
        split.setType( Split.TYPE_XOR );
        final Join join = new JoinImpl();
        join.setType( Join.TYPE_XOR );
        final EndNode end = new EndNodeImpl();
        // connections
        new ConnectionImpl( start,
                        ruleSet0,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet0,
                        split,
                        Connection.TYPE_NORMAL );
        Connection out1 = new ConnectionImpl( split,
                        ruleSet1,
                        Connection.TYPE_NORMAL );
        Connection out2 = new ConnectionImpl( split,
                        ruleSet2,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet1,
                        join,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet2,
                        join,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( join,
                        ruleSet3,
                        Connection.TYPE_NORMAL );
        new ConnectionImpl( ruleSet3,
                        end,
                        Connection.TYPE_NORMAL );
        ConstraintEvaluator constraint1 = new org.drools.ruleflow.core.impl.RuleFlowConstraintEvaluator();
        constraint1.setPriority(1);
        split.setConstraint(out1, constraint1);
        ConstraintEvaluator constraint2 = new org.drools.ruleflow.core.impl.RuleFlowConstraintEvaluator();
        constraint2.setPriority(2);
        split.setConstraint(out2, constraint2);

        // process
        final RuleFlowProcess process = new RuleFlowProcessImpl();
        process.setId( "1" );
        process.addNode( start );
        process.addNode( ruleSet0 );
        process.addNode( ruleSet1 );
        process.addNode( ruleSet2 );
        process.addNode( ruleSet3 );
        process.addNode( split );
        process.addNode( join );
        process.addNode( end );

        // rules for split
        final Rule splitRule1 = new Rule( "RuleFlow-Split-1-" + split.getId() + "-" + ruleSet1.getId());
        splitRule1.setRuleFlowGroup( "DROOLS_SYSTEM" );
        splitRule1.setConsequence( consequence );

        final RuleTerminalNode splitNode1 = new RuleTerminalNode( 7,
                                                             	  new MockTupleSource( 2 ),
                                                             	  splitRule1,
                                                             	  splitRule1.getLhs(),
                                                                  buildContext );

        final Rule splitRule2 = new Rule( "RuleFlow-Split-1-" + split.getId() + "-" + ruleSet2.getId());
        splitRule2.setRuleFlowGroup( "DROOLS_SYSTEM" );
        splitRule2.setConsequence( consequence );

        final RuleTerminalNode splitNode2 = new RuleTerminalNode( 8,
                                                             	  new MockTupleSource( 2 ),
                                                             	  splitRule2,
                                                             	  splitRule2.getLhs(),
                                                                  buildContext );

        // proces instance
        final RuleFlowProcessInstance processInstance = new RuleFlowProcessInstanceImpl();
        processInstance.setWorkingMemory( workingMemory );
        processInstance.setProcess( process );
        assertEquals( ProcessInstance.STATE_PENDING,
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

        final ReteTuple splitTuple1 = new ReteTuple( new DefaultFactHandle( 1,
        															   	    "cheese" ) );
		splitNode1.assertTuple( splitTuple1,
							    context0,
							    workingMemory );

        final ReteTuple splitTuple2 = new ReteTuple( new DefaultFactHandle( 1,
		   															        "cheese" ) );
    	splitNode2.assertTuple( splitTuple2,
    							context0,
								workingMemory );

        final RuleFlowGroupImpl systemRuleFlowGroup = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "DROOLS_SYSTEM" );
        
        

        // RuleFlowGroups should be populated, but the agenda shouldn't
        assertEquals( 2,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 1,
                      ruleFlowGroup3.size() );
        assertEquals( 2,
          	  	      systemRuleFlowGroup.size() );
        assertEquals( 0,
                      agenda.agendaSize() );

        // Activate process instance, the activations stay in the group,
        // but should now also be in the Agenda
        processInstance.start();
        assertEquals( ProcessInstance.STATE_ACTIVE,
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

        // XOR split should activate group1
        agenda.fireNextItem( null );
        workingMemory.executeQueuedActions();
        assertEquals( 0,
                      ruleFlowGroup0.size() );
        assertEquals( 1,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 1,
                      agenda.agendaSize() );

        // executing group1, XOR join should activate group3 
        agenda.fireNextItem( null );
        workingMemory.executeQueuedActions();
        assertEquals( 0,
                      ruleFlowGroup1.size() );
        assertEquals( 1,
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
        assertEquals( 1,
                      ruleFlowGroup2.size() );
        assertEquals( 0,
                      ruleFlowGroup3.size() );
        assertEquals( 0,
                      agenda.agendaSize() );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
    }
}
