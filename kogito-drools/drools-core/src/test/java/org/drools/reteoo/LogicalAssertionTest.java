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

import org.drools.Agenda;
import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;

public class LogicalAssertionTest extends DroolsTestCase {

    public void testSingleLogicalRelationship() throws Exception {
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final Rete rete = ruleBase.getRete();
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Rule rule1 = new Rule( "test-rule1" );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -5628842901492986740L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = (DefaultFactHandle) workingMemory.assertObject( "o1" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        final String logicalString = new String( "logical" );
        FactHandle logicalHandle = workingMemory.assertObject( logicalString,
                                                               false,
                                                               true,
                                                               rule1,
                                                               tuple1.getActivation() );
        // Retract the tuple and test the logically asserted fact was also retracted
        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        workingMemory.executeQueuedActions();

        assertLength( 1,
                      sink.getRetracted() );

        Object[] values = (Object[]) sink.getRetracted().get( 0 );

        assertSame( logicalHandle,
                    values[0] );

        // Test single activation for a single logical assertions. This also
        // tests that logical assertions live on after the related Activation
        // has fired.
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );
        logicalHandle = workingMemory.assertObject( logicalString,
                                                    false,
                                                    true,
                                                    rule1,
                                                    tuple1.getActivation() );

        agenda.fireNextItem( null );

        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        workingMemory.executeQueuedActions();

        assertLength( 2,
                      sink.getRetracted() );

        values = (Object[]) sink.getRetracted().get( 1 );

        assertSame( logicalHandle,
                    values[0] );
    }

    public void testEqualsMap() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so w can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );

        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -6861606249802351389L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        final String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        final String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        assertSame( logicalHandle1,
                    logicalHandle2 );

        // little sanity check using normal assert
        logicalHandle1 = workingMemory.assertObject( logicalString1 );
        logicalHandle2 = workingMemory.assertObject( logicalString2 );

        // If assert behavior in working memory is IDENTITY, 
        // returned handles must not be the same 
        if ( RuleBaseConfiguration.AssertBehaviour.IDENTITY == ((ReteooRuleBase) ruleBase).getConfiguration().getAssertBehaviour() ) {

            assertNotSame( logicalHandle1,
                           logicalHandle2 );
        } else {
            // in case behavior is EQUALS, handles should be the same
            assertSame( logicalHandle1,
                        logicalHandle2 );
        }
    }

    /**
     * This tests that Stated asserts always take precedent
     * 
     * @throws Exception
     */
    public void testStatedOverrideDiscard() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = 8922139904370747909L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test that a STATED assertion overrides a logical assertion
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        // This assertion is stated and should override any previous justified
        // "equals" objects.
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2 );

        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        assertLength( 0,
                      sink.getRetracted() );

        //  we override and discard the original logical object
        assertSame( logicalHandle2,
                    logicalHandle1 );

        // so while new STATED assertion is equal
        assertEquals( logicalString1,
                      workingMemory.getObject( logicalHandle2 ) );
        // they are not identity same
        assertNotSame( logicalString1,
                       workingMemory.getObject( logicalHandle2 ) );

        // Test that a logical assertion cannot override a STATED assertion
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        logicalString2 = new String( "logical" );
        logicalHandle2 = workingMemory.assertObject( logicalString2 );

        // This logical assertion will be ignored as there is already
        // an equals STATED assertion.
        logicalString1 = new String( "logical" );
        logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                     false,
                                                     true,
                                                     rule1,
                                                     tuple1.getActivation() );

        assertNull( logicalHandle1 );

        // Already identify same so return previously assigned handle
        logicalHandle1 = workingMemory.assertObject( logicalString2,
                                                     false,
                                                     false,
                                                     rule1,
                                                     tuple1.getActivation() );
        // return the matched handle

        assertSame( logicalHandle2,
                    logicalHandle1 );

        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        assertLength( 0,
                      sink.getRetracted() );

        // Should keep the same handle when overriding
        assertSame( logicalHandle1,
                    logicalHandle2 );

        // so while new STATED assertion is equal
        assertEquals( logicalString1,
                      workingMemory.getObject( logicalHandle2 ) );

        // they are not identity same
        assertNotSame( logicalString1,
                       workingMemory.getObject( logicalHandle2 ) );

    }

    /**
     * This tests that Stated asserts always take precedent
     * 
     * @throws Exception
     */
    public void testStatedOverridePreserve() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setLogicalOverride( RuleBaseConfiguration.LogicalOverride.PRESERVE );

        final RuleBase ruleBase = new ReteooRuleBase( conf );
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = 4142527256796002354L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test that a STATED assertion overrides a logical assertion
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        final String logicalString1 = new String( "logical" );
        final FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                      false,
                                                                      true,
                                                                      rule1,
                                                                      tuple1.getActivation() );

        // This assertion is stated and should override any previous justified
        // "equals" objects.
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2 );

        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        assertLength( 0,
                      sink.getRetracted() );

        assertNotSame( logicalHandle2,
                       logicalHandle1 );

        // so while new STATED assertion is equal
        assertEquals( workingMemory.getObject( logicalHandle1 ),
                      workingMemory.getObject( logicalHandle2 ) );

        // they are not identity same
        assertNotSame( workingMemory.getObject( logicalHandle1 ),
                       workingMemory.getObject( logicalHandle2 ) );

        // Test that a logical assertion cannot override a STATED assertion
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        logicalString2 = new String( "logical" );
        logicalHandle2 = workingMemory.assertObject( logicalString2 );
    }

    public void testRetract() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -3139752102258757978L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        // create the first activation which will justify the fact "logical"
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );

        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        // Assert the logical "logical" fact
        final String logicalString1 = new String( "logical" );
        final FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                      false,
                                                                      true,
                                                                      rule1,
                                                                      tuple1.getActivation() );

        // create the second activation to justify the "logical" fact
        final Rule rule2 = new Rule( "test-rule2" );
        final RuleTerminalNode node2 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 3 ),
                                                             rule2,
                                                             rule2.getLhs() );
        rule2.setConsequence( consequence );

        final DefaultFactHandle handle2 = new DefaultFactHandle( 2,
                                                                 "cheese" );
        final ReteTuple tuple2 = new ReteTuple( handle2 );

        node.assertTuple( tuple2,
                          context,
                          workingMemory );

        node2.assertTuple( tuple2,
                           context,
                           workingMemory );

        // Assert the logical "logical" fact
        final String logicalString2 = new String( "logical" );
        final FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                      false,
                                                                      true,
                                                                      rule2,
                                                                      tuple2.getActivation() );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().values() );

        // retract the logical object
        workingMemory.retractObject( logicalHandle2 );

        // The logical object should never appear
        assertLength( 0,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().values() );

    }

    public void testMultipleLogicalRelationships() throws FactException {
        final Rule rule1 = new Rule( "test-rule1" );
        final ReteooRuleBase ruleBase = (ReteooRuleBase) RuleBaseFactory.newRuleBase();
        final Rete rete = ruleBase.getRete();

        // Create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -2467227987792388019L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        // Create first justifier
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );
        // get the activation onto the agenda
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        // Create the second justifer
        final Rule rule2 = new Rule( "test-rule2" );
        final RuleTerminalNode node2 = new RuleTerminalNode( 4,
                                                             new MockTupleSource( 3 ),
                                                             rule2,
                                                             rule2.getLhs() );
        rule2.setConsequence( consequence );

        final DefaultFactHandle handle2 = new DefaultFactHandle( 2,
                                                                 "cheese" );
        final ReteTuple tuple2 = new ReteTuple( handle2 );

        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // get the activations onto the agenda
        node2.assertTuple( tuple2,
                           context2,
                           workingMemory );

        // Create the first justifieable relationship
        final String logicalString1 = new String( "logical" );
        final FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                      false,
                                                                      true,
                                                                      rule1,
                                                                      tuple1.getActivation() );

        // Create the second justifieable relationship
        final String logicalString2 = new String( "logical" );
        final FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                      false,
                                                                      true,
                                                                      rule2,
                                                                      tuple2.getActivation() );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().values() );

        // Now lets cancel the first activation
        node2.retractTuple( tuple2,
                            context2,
                            workingMemory );

        workingMemory.executeQueuedActions();

        // because this logical fact has two relationships it shouldn't retract yet
        assertLength( 0,
                      sink.getRetracted() );

        // check "logical" is still in the system
        assertLength( 1,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().values() );

        // now remove that final justification
        node.retractTuple( tuple1,
                           context1,
                           workingMemory );

        workingMemory.executeQueuedActions();

        // Should cause the logical fact to be retracted
        assertLength( 1,
                      sink.getRetracted() );

        // "logical" fact should no longer be in the system
        assertLength( 0,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().values() );
    }

    /**
     * This tests that when multiple not identical, but equals facts, are asserted
     * into WM, only when all are removed, a logical assert will succeed 
     * 
     * @throws Exception
     */
    public void testMultipleAssert() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = -8086689039438217146L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Assert multiple stated objects
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        final String statedString1 = new String( "logical" );
        final FactHandle statedHandle1 = workingMemory.assertObject( statedString1 );

        final String statedString2 = new String( "logical" );
        final FactHandle statedHandle2 = workingMemory.assertObject( statedString2 );

        // This assertion is logical should fail as there is previous stated objects
        final String logicalString3 = new String( "logical" );
        FactHandle logicalHandle3 = workingMemory.assertObject( logicalString3,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        // Checks that previous LogicalAssert failed 
        assertNull( logicalHandle3 );

        // If assert behavior in working memory is IDENTITY, 
        // we need to retract object 2 times before being able to 
        // succesfully logically assert a new fact
        if ( RuleBaseConfiguration.AssertBehaviour.IDENTITY == ((ReteooRuleBase) ruleBase).getConfiguration().getAssertBehaviour() ) {

            workingMemory.retractObject( statedHandle2 );

            logicalHandle3 = workingMemory.assertObject( logicalString3,
                                                         false,
                                                         true,
                                                         rule1,
                                                         tuple1.getActivation() );

            // Checks that previous LogicalAssert failed 
            assertNull( logicalHandle3 );
        }

        workingMemory.retractObject( statedHandle1 );

        logicalHandle3 = workingMemory.assertObject( logicalString3,
                                                     false,
                                                     true,
                                                     rule1,
                                                     tuple1.getActivation() );

        // Checks that previous LogicalAssert succeeded as there are no more
        // stated strings in the working memory
        assertNotNull( logicalHandle3 );

    }

    /**
     * This test checks that truth maintenance is correctly maintained for modified objects 
     */
    public void testMutableObject() {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        final Rete rete = new Rete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  new ClassObjectType( String.class ),
                                                                  rete,
                                                                  3 );
        objectTypeNode.attach();
        final MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final RuleTerminalNode node = new RuleTerminalNode( 2,
                                                            new MockTupleSource( 2 ),
                                                            rule1,
                                                            rule1.getLhs() );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        final ReteooWorkingMemory workingMemory = (ReteooWorkingMemory) ruleBase.newStatefulSession();

        final Agenda agenda = workingMemory.getAgenda();

        final Consequence consequence = new Consequence() {
            /**
             * 
             */
            private static final long serialVersionUID = 2679144678701462458L;

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        final DefaultFactHandle handle1 = new DefaultFactHandle( 1,
                                                                 "cheese" );
        final ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test that a STATED assertion overrides a logical assertion
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        final Cheese cheese = new Cheese( "brie",
                                          10 );
        final FactHandle cheeseHandle = workingMemory.assertObject( cheese,
                                                                    false,
                                                                    true,
                                                                    rule1,
                                                                    tuple1.getActivation() );

        cheese.setType( "cheddar" );
        cheese.setPrice( 20 );

        assertEquals( 1,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().size() );
        assertEquals( 1,
                      workingMemory.getTruthMaintenanceSystem().getAssertMap().size() );

        workingMemory.retractObject( cheeseHandle );

        assertEquals( 0,
                      workingMemory.getTruthMaintenanceSystem().getJustifiedMap().size() );
        assertEquals( 0,
                      workingMemory.getTruthMaintenanceSystem().getAssertMap().size() );
    }

}