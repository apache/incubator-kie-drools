package org.drools.reteoo;

import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Consequence;
import org.drools.spi.PropagationContext;

public class LogicalAssertionTest extends DroolsTestCase {

    public void testSingleLogicalRelationship() throws Exception {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void invoke(Activation activation) {
                // do nothing
            }
        };
        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          handle1,
                                          workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );
        ModuleImpl main = (ModuleImpl) agenda.getFocus();
        Activation activation1 = (Activation) main.getActivationQueue().get();

        String logicalString = new String( "logical" );
        FactHandle logicalHandle = workingMemory.assertObject( logicalString,
                                                               false,
                                                               true,
                                                               rule1,
                                                               activation1 );
        agenda.removeFromAgenda( tuple1.getKey(),
                                 context1,
                                 rule1 );

        assertLength( 1,
                      sink.getRetracted() );

        Object[] values = (Object[]) sink.getRetracted().get( 0 );

        assertSame( logicalHandle,
                    values[0] );

        // Test single activation for a single logical assertions. This also
        // tests that logical assertions live on after the related Activation
        // has fired.
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );
        activation1 = (Activation) main.getActivationQueue().get();

        logicalHandle = workingMemory.assertObject( logicalString,
                                                    false,
                                                    true,
                                                    rule1,
                                                    activation1 );

        agenda.fireNextItem( null );

        agenda.removeFromAgenda( tuple1.getKey(),
                                 context1,
                                 rule1 );

        assertLength( 2,
                      sink.getRetracted() );

        values = (Object[]) sink.getRetracted().get( 1 );

        assertSame( logicalHandle,
                    values[0] );
    }

    public void testEqualsMap() throws Exception {
        // create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void invoke(Activation activation) {
                // do nothing
            }
        };
        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          handle1,
                                          workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test single activation for a single logical assertions
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );
        ModuleImpl main = (ModuleImpl) agenda.getFocus();
        Activation activation1 = (Activation) main.getActivationQueue().get();

        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                activation1 );

        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                rule1,
                                                                activation1 );

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
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void invoke(Activation activation) {
                // do nothing
            }
        };
        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          handle1,
                                          workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // Test that a STATED assertion overrides a logical assertion
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );
        ModuleImpl main = (ModuleImpl) agenda.getFocus();
        Activation activation1 = (Activation) main.getActivationQueue().get();

        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                activation1 );

        /*
         * This assertion is stated and should override any previous justified
         * "equals" objects.
         */
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2 );

        agenda.removeFromAgenda( tuple1.getKey(),
                                 context1,
                                 rule1 );

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
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );
        activation1 = (Activation) main.getActivationQueue().get();

        logicalString2 = new String( "logical" );
        logicalHandle2 = workingMemory.assertObject( logicalString2 );

        // This logical assertion will be ignored as there is already
        // an equals STATED assertion.
        logicalString1 = new String( "logical" );
        logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                     false,
                                                     true,
                                                     rule1,
                                                     activation1 );
        // Already an equals object but not identity same, so will do nothing
        // and return null
        assertNull( logicalHandle1 );

        logicalHandle1 = workingMemory.assertObject( logicalString2,
                                                     false,
                                                     true,
                                                     rule1,
                                                     activation1 );
        // already an equals object and identity same, so will do nothing and
        // return the matched handle
        assertSame( logicalHandle2,
                    logicalHandle1 );

        agenda.removeFromAgenda( tuple1.getKey(),
                                 context1,
                                 rule1 );

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
        Rete rete = new Rete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );
        RuleBase ruleBase = new RuleBaseImpl();
        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        Consequence consequence = new Consequence() {
            public void invoke(Activation activation) {
                // do nothing
            }
        };

        // create the first activation which will justify the fact "logical"
        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          handle1,
                                          workingMemory );

        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        Activation activation1 = new AgendaItem( 0,
                                                 tuple1,
                                                 context,
                                                 rule1 );

        // Assert the logical "logical" fact
        String logicalString1 = new String( "logical" );
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                rule1,
                                                                activation1 );

        // create the second activation to justify the "logical" fact
        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setConsequence( consequence );

        FactHandleImpl handle2 = new FactHandleImpl( 2 );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          handle2,
                                          workingMemory );

        Activation activation2 = new AgendaItem( 0,
                                                 tuple2,
                                                 context,
                                                 rule2 );

        // Assert the logical "logical" fact
        String logicalString2 = new String( "logical" );
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                rule2,
                                                                activation2 );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getJustified().values() );

        // but has two justifications
        assertLength( 2,
                      workingMemory.getJustifiers().values() );

        // retract the logical object
        workingMemory.retractObject( logicalHandle2 );

        // The logical object should never appear
        assertLength( 0,
                      workingMemory.getJustified().values() );

        // And its justifers should also be removed
        assertLength( 0,
                      workingMemory.getJustifiers().values() );

    }

    public void testMultipleLogicalRelationships() throws FactException {
        RuleBaseImpl ruleBase = new RuleBaseImpl();
        Rete rete = ruleBase.getRete();

        // Create a RuleBase with a single ObjectTypeNode we attach a
        // MockObjectSink so we can detect assertions and retractions
        ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                            new ClassObjectType( String.class ),
                                                            rete );
        objectTypeNode.attach();
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink( sink );

        WorkingMemoryImpl workingMemory = (WorkingMemoryImpl) ruleBase.newWorkingMemory();

        final Agenda agenda = workingMemory.getAgenda();

        Consequence consequence = new Consequence() {
            public void invoke(Activation activation) {
                // do nothing
            }
        };

        // Create first justifier
        final Rule rule1 = new Rule( "test-rule1" );
        rule1.setConsequence( consequence );

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        ReteTuple tuple1 = new ReteTuple( 0,
                                          handle1,
                                          workingMemory );

        final PropagationContext context1 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // get the activation onto the agenda
        agenda.addToAgenda( tuple1,
                            context1,
                            rule1 );

        // Create the second justifer
        final Rule rule2 = new Rule( "test-rule2" );
        rule2.setConsequence( consequence );

        FactHandleImpl handle2 = new FactHandleImpl( 2 );
        ReteTuple tuple2 = new ReteTuple( 0,
                                          handle2,
                                          workingMemory );

        final PropagationContext context2 = new PropagationContextImpl( 0,
                                                                        PropagationContext.ASSERTION,
                                                                        null,
                                                                        null );

        // get the activations onto the agenda
        agenda.addToAgenda( tuple2,
                            context2,
                            rule2 );

        // We have two activations on the Agenda, so use toArray to get them out
        ModuleImpl main = (ModuleImpl) agenda.getFocus();
        Activation[] activations = (Activation[]) main.getActivationQueue().toArray( new Activation[]{} );

        // Create the first justifieable relationship
        String logicalString1 = new String( "logical" );
        Activation activation1 = activations[0];
        FactHandle logicalHandle1 = workingMemory.assertObject( logicalString1,
                                                                false,
                                                                true,
                                                                activation1.getRule(),
                                                                activation1 );

        // Create the second justifieable relationship
        String logicalString2 = new String( "logical" );
        Activation activation2 = activations[1];
        FactHandle logicalHandle2 = workingMemory.assertObject( logicalString2,
                                                                false,
                                                                true,
                                                                activation2.getRule(),
                                                                activation2 );

        // "logical" should only appear once
        assertLength( 1,
                      workingMemory.getJustified().values() );
        // but has two justifications
        assertLength( 2,
                      workingMemory.getJustifiers().values() );

        // Now lets cancel the first activation
        agenda.removeFromAgenda( ((ReteTuple) activation2.getTuple()).getKey(),
                                 context2,
                                 activation2.getRule() );

        // because this logical fact has two relationships it shouldn't retract
        // yet
        assertLength( 0,
                      sink.getRetracted() );

        // check "logical" is still in the system
        assertLength( 1,
                      workingMemory.getJustified().values() );
        // but now it only has one justifier
        assertLength( 1,
                      workingMemory.getJustifiers().values() );

        // now remove that final justification
        agenda.removeFromAgenda( ((ReteTuple) activation1.getTuple()).getKey(),
                                 context1,
                                 activation1.getRule() );

        // Should cause the logical fact to be retracted
        assertLength( 1,
                      sink.getRetracted() );

        // "logical" fact should no longer be in the system
        assertLength( 0,
                      workingMemory.getJustified().values() );
        // all justifiers should be removed
        assertLength( 0,
                      workingMemory.getJustifiers().values() );
    }

}
