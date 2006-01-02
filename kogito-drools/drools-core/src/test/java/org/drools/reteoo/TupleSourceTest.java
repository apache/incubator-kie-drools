package org.drools.reteoo;

import org.drools.AssertionException;
import org.drools.DroolsTestCase;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class TupleSourceTest extends DroolsTestCase {

    public void testObjectTupleConstructor() {
        MockTupleSource source = new MockTupleSource( 15 );
        assertEquals( 15,
                      source.getId() );

        assertEquals( 0,
                      source.getAttached() );
        source.attach();
        assertEquals( 1,
                      source.getAttached() );
    }

    public void testAddTupleSink() {
        MockTupleSource source = new MockTupleSource( 15 );
        assertLength( 0,
                      source.getTupleSinks() );

        source.addTupleSink( new MockTupleSink() );
        assertLength( 1,
                      source.getTupleSinks() );

        source.addTupleSink( new MockTupleSink() );
        assertLength( 2,
                      source.getTupleSinks() );
    }

    public void testPropagateAssertTuple() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.RETRACTION,
                                                                 null,
                                                                 null );
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 15 );
        MockTupleSink sink1 = new MockTupleSink();

        source.addTupleSink( sink1 );
        assertLength( 0,
                      sink1.getAsserted() );

        ReteTuple tuple1 = new ReteTuple( 0,
                                          new FactHandleImpl( 1 ),
                                          workingMemory );

        source.propagateAssertTuple( tuple1,
                                     context,
                                     workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        Object[] list = (Object[]) sink1.getAsserted().get( 0 );
        assertSame( tuple1,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        ReteTuple tuple2 = new ReteTuple( 0,
                                          new FactHandleImpl( 1 ),
                                          workingMemory );

        MockTupleSink sink2 = new MockTupleSink();
        source.addTupleSink( sink2 );
        // source.ruleAttached();

        source.propagateAssertTuple( tuple2,
                                     context,
                                     workingMemory );

        assertLength( 2,
                      sink1.getAsserted() );

        assertLength( 1,
                      sink2.getAsserted() );

        list = (Object[]) sink1.getAsserted().get( 0 );
        assertSame( tuple1,
                    list[0] );
        assertNotSame( tuple2,
                       list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink1.getAsserted().get( 1 );
        assertSame( tuple2,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink2.getAsserted().get( 0 );
        assertSame( tuple2,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        try {
            sink1.setAssertionException( new AssertionException( "test" ) );
            source.propagateAssertTuple( tuple1,
                                         context,
                                         workingMemory );
            fail( "Should have thrown 'AssertionException'" );

        } catch ( AssertionException e ) {

        } catch ( Exception e ) {
            fail( "Should have thrown 'AssertionException' and not '" + e.getClass() + "'" );
        }
    }

    public void testPropagateRetractTuple() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( PropagationContext.RETRACTION,
                                                                 null,
                                                                 null );
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 15 );
        MockTupleSink sink1 = new MockTupleSink();
        source.addTupleSink( sink1 );
        assertLength( 0,
                      sink1.getRetracted() );

        TupleKey key1 = new TupleKey( 1,
                                      new FactHandleImpl( 1 ) );

        source.propagateRetractTuples( key1,
                                       context,
                                       workingMemory );

        assertLength( 1,
                      sink1.getRetracted() );

        Object[] list = (Object[]) sink1.getRetracted().get( 0 );
        assertSame( key1,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        MockTupleSink sink2 = new MockTupleSink();
        source.addTupleSink( sink2 );

        TupleKey key2 = new TupleKey( 2,
                                      new FactHandleImpl( 2 ) );

        source.propagateRetractTuples( key2,
                                       context,
                                       workingMemory );

        assertLength( 2,
                      sink1.getRetracted() );

        assertLength( 1,
                      sink2.getRetracted() );

        list = (Object[]) sink1.getRetracted().get( 0 );
        assertSame( key1,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink1.getRetracted().get( 1 );
        assertSame( key2,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink2.getRetracted().get( 0 );
        assertSame( key2,
                    list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        try {
            sink1.setRetractionException( new RetractionException( "test" ) );
            source.propagateRetractTuples( key2,
                                           context,
                                           workingMemory );
            fail( "Should have thrown 'RetractionException'" );

        } catch ( RetractionException e ) {

        } catch ( Exception e ) {
            fail( "Should have thrown 'RetractionException' and not '" + e.getClass() + "'" );
        }
    }

    public void testAttachNewNode() throws FactException {
        PropagationContext context = new PropagationContextImpl( PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockTupleSource source = new MockTupleSource( 15 );

        // Add two Tuple Sinks
        MockTupleSink sink1 = new MockTupleSink();
        source.addTupleSink( sink1 );

        MockTupleSink sink2 = new MockTupleSink();
        source.addTupleSink( sink2 );

        // Only the last added TupleSink should receive facts
        source.attachingNewNode = true;

        ReteTuple tuple1 = new ReteTuple( 0,
                                          new FactHandleImpl( 2 ),
                                          workingMemory );

        source.propagateAssertTuple( tuple1,
                                     context,
                                     workingMemory );

        assertLength( 0,
                      sink1.getAsserted() );
        assertLength( 1,
                      sink2.getAsserted() );

        // Now all sinks should receive values
        source.attachingNewNode = false;

        ReteTuple tuple2 = new ReteTuple( 0,
                                          new FactHandleImpl( 3 ),
                                          workingMemory );

        source.propagateAssertTuple( tuple2,
                                     context,
                                     workingMemory );

        /* Both sinks receive one object */
        assertLength( 1,
                      sink1.getAsserted() );
        assertLength( 2,
                      sink2.getAsserted() );

    }

}
