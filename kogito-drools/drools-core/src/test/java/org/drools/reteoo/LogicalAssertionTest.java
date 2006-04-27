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



import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.common.Agenda;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.PropagationContext;

public class LogicalAssertionTest extends DroolsTestCase {

    public void testSingleLogicalRelationship() throws Exception {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final Rule rule1 = new Rule( "test-rule1" );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = (FactHandleImpl) workingMemory.assertObject( "o1" );
        ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        String logicalString = new String( "logical" );
        FactHandle logicalHandle = workingMemory.assertObject( logicalString,
                                                               false,
                                                               true,
                                                               rule1,
                                                               tuple1.getActivation() );
        // Retract the tuple and test the logically asserted fact was also retracted
        node.retractTuple( tuple1,
                           context1,
                           workingMemory );
        
        workingMemory.propagateQueuedActions();

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
        
        workingMemory.propagateQueuedActions();

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

        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );

        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        String logicalString2 = new String( "logical" );
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
        assertNotSame( logicalHandle1,
                       logicalHandle2 );
    }

    /**
     * This tests that Stated asserts always take precedent
     * 
     * @throws Exception
     */
    public void testStatedOverride() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( handle1 );

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

        // Should keep the same handle when overriding
        assertSame( logicalHandle1,
                    logicalHandle2 );

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
        // Already an equals object but not identity same, so will do nothing
        // and return null
        assertNull( logicalHandle1 );

        // Alreyad identify same so return previously assigned handle
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

    public void testRetract() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        final Rule rule1 = new Rule( "test-rule1" );
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        // create the first activation which will justify the fact "logical"
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( handle1 );

        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        node.assertTuple( tuple1,
                          context,
                          workingMemory );

        // Assert the logical "logical" fact
        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        // create the second activation to justify the "logical" fact
        final Rule rule2 = new Rule( "test-rule2" );
        final TerminalNode node2 = new TerminalNode( 4,
                                                     new MockTupleSource( 3 ),
                                                     rule2 );
        rule2.setConsequence( consequence );

        FactHandleImpl handle2 = new FactHandleImpl( 2 );
        ReteTuple tuple2 = new ReteTuple( handle2 );

        node.assertTuple( tuple2,
                          context,
                          workingMemory );

        node2.assertTuple( tuple2,
                           context,
                           workingMemory );

        // Assert the logical "logical" fact
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                rule2,
                                                                tuple2.getActivation() );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getJustified().values() );

        // retract the logical object
        workingMemory.retractObject( logicalHandle2 );

        // The logical object should never appear
        assertLength( 0,
                      workingMemory.getJustified().values() );

    }

    public void testMultipleLogicalRelationships() throws FactException {
        final Rule rule1 = new Rule( "test-rule1" );
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();

        // Create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 1,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };

        // Create first justifier
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( handle1 );

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
        final TerminalNode node2 = new TerminalNode( 4,
                                                     new MockTupleSource( 3 ),
                                                     rule2 );
        rule2.setConsequence( consequence );

        FactHandleImpl handle2 = new FactHandleImpl( 2 );
        ReteTuple tuple2 = new ReteTuple( handle2 );

        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // get the activations onto the agenda
        node2.assertTuple( tuple2,
                           context2,
                           workingMemory );

        // Create the first justifieable relationship
        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        // Create the second justifieable relationship
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                rule2,
                                                                tuple2.getActivation() );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getJustified().values() );

        // Now lets cancel the first activation
        node2.retractTuple( tuple2,
                            context2,
                            workingMemory );

        // because this logical fact has two relationships it shouldn't retract yet
        assertLength( 0,
                      sink.getRetracted() );

        // check "logical" is still in the system
        assertLength( 1,
                      workingMemory.getJustified().values() );

        // now remove that final justification
        node.retractTuple( tuple1,
                           context1,
                           workingMemory );
        
        workingMemory.propagateQueuedActions();

        // Should cause the logical fact to be retracted
        assertLength( 1,
                      sink.getRetracted() );

        // "logical" fact should no longer be in the system
        assertLength( 0,
                      workingMemory.getJustified().values() );
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
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        final TerminalNode node = new TerminalNode( 2,
                                                    new MockTupleSource( 2 ),
                                                    rule1 );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) {
                // do nothing
            }
        };
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( handle1 );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Assert multiple stated objects
        node.assertTuple( tuple1,
                          context1,
                          workingMemory );

        String statedString1 = new String( "logical" );
        FactHandle statedHandle1 = workingMemory.assertObject( statedString1 );

        String statedString2 = new String( "logical" );
        FactHandle statedHandle2 = workingMemory.assertObject( statedString2 );

        // This assertion is logical should fail as there is previous stated objects
        String logicalString3 = new String( "logical" );
        FactHandle logicalHandle3 = workingMemory.assertObject( logicalString3,
                                                                false,
                                                                true,
                                                                rule1,
                                                                tuple1.getActivation() );

        // Checks that previous LogicalAssert failed 
        assertNull( logicalHandle3 );
        
        workingMemory.retractObject( statedHandle2 );

        logicalHandle3 = workingMemory.assertObject( logicalString3,
                                                    false,
                                                    true,
                                                    rule1,
                                                    tuple1.getActivation() );

        // Checks that previous LogicalAssert failed as there is still one 
        // stated string in the working memory
        assertNull( logicalHandle3 );
        
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

    

}