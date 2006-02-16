package org.drools.reteoo;

import java.beans.IntrospectionException;
import java.util.HashSet;
import java.util.Set;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.EvaluatorFactory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Evaluator;
import org.drools.spi.Field;
import org.drools.spi.FieldExtractor;
import org.drools.spi.MockField;
import org.drools.spi.PropagationContext;

public class AlphaNodeTest extends DroolsTestCase {

    public void testAttach() throws Exception {
        MockObjectSource source = new MockObjectSource( 15 );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             null,
                                             source );
        assertEquals( 2,
                      alphaNode.getId() );
        assertLength( 0,
                      source.getObjectSinks() );
        alphaNode.attach();
        assertLength( 1,
                      source.getObjectSinks() );
        assertSame( alphaNode,
                    source.getObjectSinks().get( 0 ) );
    }

    public void testMemory() {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             null,
                                             null );

        Set memory = (HashSet) workingMemory.getNodeMemory( alphaNode );

        assertNotNull( memory );
    }

    public void testLiteralConstraintAssertObjectWithMemory() throws Exception {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        MockObjectSource source = new MockObjectSource( 15 );

        ClassFieldExtractor extractor = new ClassFieldExtractor( Cheese.class, "type" );

        Field field = new MockField( "type",
                                     "cheddar",
                                      extractor.getIndex() );

        Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                           Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        // With Memory
        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             source );

        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        Cheese cheddar = new Cheese( "cheddar",
                                     5 );
        workingMemory.putObject( f0,
                                 cheddar );

        // check sink is empty
        assertLength( 0,
                      sink.getAsserted() );

        // check alpha memory is empty 
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 1,
                      memory );
        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (FactHandleImpl) list[0] ) );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

        FactHandleImpl f1 = new FactHandleImpl( 1 );
        Cheese stilton = new Cheese( "stilton",
                                     6 );
        workingMemory.putObject( f1,
                                 stilton );

        // object should NOT assert as it does not pass test
        alphaNode.assertObject( f1,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        assertLength( 1,
                      memory );
        list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (FactHandleImpl) list[0] ) );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

    }

    /*
     * dont need to test with and without memory on this, as it was already done
     * on the previous two tests. This just test AlphaNode With a different
     * Constraint type.
     */
    public void testReturnValueConstraintAssertObject() throws Exception {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        MockObjectSource source = new MockObjectSource( 15 );

        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            "type" );

        Field field = new MockField( "type",
                                     "cheddar",
                                     extractor.getIndex() );

        Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                           Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             source );
        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        assertLength( 0,
                      sink.getAsserted() );

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      sink.getAsserted() );
        Object[] list = (Object[]) sink.getAsserted().get( 0 );
        assertSame( cheddar,
                    workingMemory.getObject( (FactHandleImpl) list[0] ) );

        Cheese stilton = new Cheese( "stilton",
                                     6 );
        workingMemory.putObject( f0,
                                 stilton );        
        
        sink.getAsserted().clear();

        // object should not assert as it does not pass text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 0,
                      sink.getAsserted() );
    }

    public void testRetractObject() throws Exception {
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        MockObjectSource source = new MockObjectSource( 15 );

        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            "type" );
        
        Field field = new MockField( "type",
                                     "cheddar",
                                     extractor.getIndex() );



        Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                           Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             source );
        MockObjectSink sink = new MockObjectSink();
        alphaNode.addObjectSink( sink );

        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        FactHandleImpl f0 = new FactHandleImpl( 0 );
        workingMemory.putObject( f0,
                                 cheddar );

        // check alpha memory is empty
        Set memory = (Set) workingMemory.getNodeMemory( alphaNode );
        assertLength( 0,
                      memory );

        // object should assert as it passes text
        alphaNode.assertObject( f0,
                                context,
                                workingMemory );

        assertLength( 1,
                      memory );

        FactHandleImpl f1 = new FactHandleImpl( 1 );

        // object should NOT retract as it doesn't exist
        alphaNode.retractObject( f1,
                                 context,
                                 workingMemory );

        assertLength( 0,
                      sink.getRetracted() );
        assertLength( 1,
                      memory );
        assertTrue( "Should contain 'cheddar handle'",
                    memory.contains( f0 ) );

        // object should retract as it does exist
        alphaNode.retractObject( f0,
                                 context,
                                 workingMemory );

        assertLength( 1,
                      sink.getRetracted() );
        assertLength( 0,
                      memory );
        Object[] list = (Object[]) sink.getRetracted().get( 0 );
        assertSame( f0,
                    list[0] );

    }

    public void testUpdateNewNode() throws FactException,
                                             IntrospectionException {
        // An AlphaNode with memory should not try and repropagate from its
        // source
        // Also it should only update the latest tuple sinky

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        MockObjectSource source = new MockObjectSource( 1 );

        FieldExtractor extractor = new ClassFieldExtractor( Cheese.class,
                                                            "type");

        Field field = new MockField( "type",
                                     "cheddar",
                                     extractor.getIndex());



        Evaluator evaluator = EvaluatorFactory.getInstance().getEvaluator( Evaluator.OBJECT_TYPE,
                                                                           Evaluator.EQUAL );
        LiteralConstraint constraint = new LiteralConstraint( field,
                                                              extractor,
                                                              evaluator );

        AlphaNode alphaNode = new AlphaNode( 2,
                                             constraint,
                                             source );

        alphaNode.attach();

        MockObjectSink sink1 = new MockObjectSink();
        alphaNode.addObjectSink( sink1 );

        // Assert a single fact which should be in the AlphaNode memory and also
        // propagated to the
        // the tuple sink
        Cheese cheese = new Cheese( "cheddar",
                                    0 );
        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        workingMemory.putObject( handle1,
                                 cheese );

        source.propagateAssertObject( handle1,
                                      context,
                                      workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        // Attach a new tuple sink
        MockObjectSink sink2 = new MockObjectSink();
        alphaNode.addObjectSink( sink2 );

        // Tell the alphanode to update the new node. Make sure the first sink1
        // is not updated
        // likewise the source should not do anything
        alphaNode.updateNewNode( workingMemory,
                                 context );

        assertLength( 1,
                      sink1.getAsserted() );
        assertLength( 1,
                      sink2.getAsserted() );
        assertEquals( 0,
                      source.getUdated() );
    }
}
