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
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.base.SalienceInteger;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.Join;
import org.drools.workflow.core.node.RuleSetNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.instance.impl.ConstraintEvaluator;

/**
 * @author mproctor
 */

public class RuleFlowGroupTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext   buildContext;

    protected void setUp() throws Exception {
        ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        buildContext = new BuildContext( ruleBase,
                                         ((ReteooRuleBase) ruleBase).getReteooBuilder().getIdGenerator() );
    }

    public void testRuleFlowGroup() {
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
                                                                        null,
                                                                        null );

        // nodes
        final StartNode start = new StartNode();
        start.setId(1);
        final RuleSetNode ruleSet0 = new RuleSetNode();
        ruleSet0.setRuleFlowGroup( "rule-flow-group-0" );
        ruleSet0.setId(2);
        final RuleSetNode ruleSet1 = new RuleSetNode();
        ruleSet1.setRuleFlowGroup( "rule-flow-group-1" );
        ruleSet1.setId(3);
        final RuleSetNode ruleSet2 = new RuleSetNode();
        ruleSet2.setRuleFlowGroup( "rule-flow-group-2" );
        ruleSet2.setId(4);
        final RuleSetNode ruleSet3 = new RuleSetNode();
        ruleSet3.setRuleFlowGroup( "rule-flow-group-3" );
        ruleSet3.setId(5);
        final Split split = new Split();
        split.setType( Split.TYPE_AND );
        split.setId(6);
        final Join join = new Join();
        join.setType( Join.TYPE_AND );
        join.setId(7);
        final EndNode end = new EndNode();
        end.setId(8);
        // connections
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet0,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet0,
                            Node.CONNECTION_DEFAULT_TYPE,
                            split,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( split,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet1,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( split,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet2,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet1,
                            Node.CONNECTION_DEFAULT_TYPE,
                            join,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet2,
                            Node.CONNECTION_DEFAULT_TYPE,
                            join,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( join,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet3,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet3,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );

        // process
        final RuleFlowProcess process = new RuleFlowProcess();
        process.addNode( start );
        process.addNode( ruleSet0 );
        process.addNode( ruleSet1 );
        process.addNode( ruleSet2 );
        process.addNode( ruleSet3 );
        process.addNode( split );
        process.addNode( join );
        process.addNode( end );

        // proces instance
        final RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setWorkingMemory( workingMemory );
        processInstance.setProcess( process );
        assertEquals( ProcessInstance.STATE_PENDING,
                      processInstance.getState() );

        final RuleFlowGroupImpl ruleFlowGroup0 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-0" );
        final RuleFlowGroupImpl ruleFlowGroup1 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-1" );
        final RuleFlowGroupImpl ruleFlowGroup2 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-2" );
        final RuleFlowGroupImpl ruleFlowGroup3 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-3" );

        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node0.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );

        final LeftTuple tuple2 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node1.assertLeftTuple( tuple2,
                               context0,
                               workingMemory );

        final LeftTuple tuple3 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node2.assertLeftTuple( tuple3,
                               context0,
                               workingMemory );

        final LeftTuple tuple4 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node3.assertLeftTuple( tuple4,
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
                                                                        null,
                                                                        null );

        // nodes
        final StartNode start = new StartNode();
        start.setId(1);
        final RuleSetNode ruleSet0 = new RuleSetNode();
        ruleSet0.setRuleFlowGroup( "rule-flow-group-0" );
        ruleSet0.setId(2);
        final RuleSetNode ruleSet1 = new RuleSetNode();
        ruleSet1.setRuleFlowGroup( "rule-flow-group-1" );
        ruleSet1.setId(3);
        final RuleSetNode ruleSet2 = new RuleSetNode();
        ruleSet2.setRuleFlowGroup( "rule-flow-group-2" );
        ruleSet2.setId(4);
        final RuleSetNode ruleSet3 = new RuleSetNode();
        ruleSet3.setRuleFlowGroup( "rule-flow-group-3" );
        ruleSet3.setId(5);
        final Split split = new Split();
        split.setType( Split.TYPE_XOR );
        split.setId(6);
        final Join join = new Join();
        join.setType( Join.TYPE_XOR );
        join.setId(7);
        final EndNode end = new EndNode();
        end.setId(8);
        // connections
        new ConnectionImpl( start,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet0,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet0,
                            Node.CONNECTION_DEFAULT_TYPE,
                            split,
                            Node.CONNECTION_DEFAULT_TYPE );
        Connection out1 = new ConnectionImpl( split,
                                              Node.CONNECTION_DEFAULT_TYPE,
                                              ruleSet1,
                                              Node.CONNECTION_DEFAULT_TYPE );
        Connection out2 = new ConnectionImpl( split,
                                              Node.CONNECTION_DEFAULT_TYPE,
                                              ruleSet2,
                                              Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet1,
                            Node.CONNECTION_DEFAULT_TYPE,
                            join,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet2,
                            Node.CONNECTION_DEFAULT_TYPE,
                            join,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( join,
                            Node.CONNECTION_DEFAULT_TYPE,
                            ruleSet3,
                            Node.CONNECTION_DEFAULT_TYPE );
        new ConnectionImpl( ruleSet3,
                            Node.CONNECTION_DEFAULT_TYPE,
                            end,
                            Node.CONNECTION_DEFAULT_TYPE );
        ConstraintEvaluator constraint1 = new org.drools.workflow.instance.impl.RuleConstraintEvaluator();
        constraint1.setPriority( 1 );
        split.setConstraint( out1,
                             constraint1 );
        ConstraintEvaluator constraint2 = new org.drools.workflow.instance.impl.RuleConstraintEvaluator();
        constraint2.setPriority( 2 );
        split.setConstraint( out2,
                             constraint2 );

        // process
        final RuleFlowProcess process = new RuleFlowProcess();
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
        final Rule splitRule1 = new Rule( "RuleFlow-Split-1-" + split.getUniqueId() + "-" + ruleSet1.getUniqueId() + "-" + Node.CONNECTION_DEFAULT_TYPE );
        splitRule1.setRuleFlowGroup( "DROOLS_SYSTEM" );
        splitRule1.setConsequence( consequence );

        final RuleTerminalNode splitNode1 = new RuleTerminalNode( 7,
                                                                  new MockTupleSource( 2 ),
                                                                  splitRule1,
                                                                  splitRule1.getLhs(),
                                                                  buildContext );

        final Rule splitRule2 = new Rule( "RuleFlow-Split-1-" + split.getUniqueId() + "-" + ruleSet2.getUniqueId() + "-" + Node.CONNECTION_DEFAULT_TYPE );
        splitRule2.setRuleFlowGroup( "DROOLS_SYSTEM" );
        splitRule2.setConsequence( consequence );

        final RuleTerminalNode splitNode2 = new RuleTerminalNode( 8,
                                                                  new MockTupleSource( 2 ),
                                                                  splitRule2,
                                                                  splitRule2.getLhs(),
                                                                  buildContext );

        // proces instance
        final RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setWorkingMemory( workingMemory );
        processInstance.setProcess( process );
        assertEquals( ProcessInstance.STATE_PENDING,
                      processInstance.getState() );

        final RuleFlowGroupImpl ruleFlowGroup0 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-0" );
        final RuleFlowGroupImpl ruleFlowGroup1 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-1" );
        final RuleFlowGroupImpl ruleFlowGroup2 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-2" );
        final RuleFlowGroupImpl ruleFlowGroup3 = (RuleFlowGroupImpl) agenda.getRuleFlowGroup( "rule-flow-group-3" );

        final LeftTuple tuple0 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node0.assertLeftTuple( tuple0,
                               context0,
                               workingMemory );

        final LeftTuple tuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node0.assertLeftTuple( tuple1,
                               context0,
                               workingMemory );

        final LeftTuple tuple2 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node1.assertLeftTuple( tuple2,
                               context0,
                               workingMemory );

        final LeftTuple tuple3 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node2.assertLeftTuple( tuple3,
                               context0,
                               workingMemory );

        final LeftTuple tuple4 = new LeftTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ),
                                                null,
                                                true  );
        node3.assertLeftTuple( tuple4,
                               context0,
                               workingMemory );

        final LeftTuple splitTuple1 = new LeftTuple( new DefaultFactHandle( 1,
                                                                            "cheese" ),
                                                     null,
                                                     true  );
        splitNode1.assertLeftTuple( splitTuple1,
                                    context0,
                                    workingMemory );

        final LeftTuple splitTuple2 = new LeftTuple( new DefaultFactHandle( 1,
                                                                            "cheese" ),
                                                     null,
                                                     true  );
        splitNode2.assertLeftTuple( splitTuple2,
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
