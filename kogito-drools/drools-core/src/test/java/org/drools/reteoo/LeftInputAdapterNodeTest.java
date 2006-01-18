package org.drools.reteoo;

import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class LeftInputAdapterNodeTest extends DroolsTestCase {

    public void testLeftInputAdapterNode() {
        MockObjectSource source = new MockObjectSource( 15 );
        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
                                                                 0,
                                                                 source );
        assertEquals( 23,
                      liaNode.getId() );

        assertEquals( 0,
                      source.getAttached() );
        source.attach();
        assertEquals( 1,
                      source.getAttached() );
    }

    public void testAttach() throws Exception {
        MockObjectSource source = new MockObjectSource( 15 );

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                 0,
                                                                 source );

        assertEquals( 1,
                      liaNode.getId() );

        assertLength( 0,
                      source.getObjectSinks() );

        liaNode.attach();

        assertLength( 1,
                      source.getObjectSinks() );

        assertSame( liaNode,
                    source.getObjectSinks().get( 0 ) );
    }

    public void testAssertObject() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockObjectSource source = new MockObjectSource( 15 );

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                 0,
                                                                 source );
        MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        Object string1 = "cheese";
        Object object1 = new Object();

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        workingMemory.putObject( handle1,
                                 string1 );

        workingMemory.putObject( handle2,
                                 object1 );

        /* assert object */
        liaNode.assertObject( string1,
                              handle1,
                              context,
                              workingMemory );

        List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );

        /* assert object */
        liaNode.assertObject( object1,
                              handle2,
                              context,
                              workingMemory );
        assertLength( 2,
                      asserted );

        /* check tuple comes out */
        ReteTuple tuple = (ReteTuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    tuple.get( handle1 ) );

        /* check tuple comes out */
        tuple = (ReteTuple) ((Object[]) asserted.get( 1 ))[0];
        assertSame( object1,
                    tuple.get( handle2 ) );
    }

    public void testRetractObject() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );

        WorkingMemoryImpl memory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockObjectSource source = new MockObjectSource( 15 );

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                 0,
                                                                 source );
        MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        Object string1 = "cheese";

        Object object1 = new Object();

        FactHandleImpl handle1 = new FactHandleImpl( 1 );
        FactHandleImpl handle2 = new FactHandleImpl( 2 );

        memory.putObject( handle1,
                          string1 );

        memory.putObject( handle2,
                          object1 );

        /* retract object */
        liaNode.retractObject( handle1,
                               context,
                               memory );

        List retracted = sink.getRetracted();
        assertLength( 1,
                      retracted );

        /* retract object */
        liaNode.retractObject( handle2,
                               context,
                               memory );

        assertLength( 2,
                      retracted );

        /* check TupleKey comes out */
        TupleKey key = (TupleKey) ((Object[]) retracted.get( 0 ))[0];
        assertSame( handle1,
                    key.get( 0 ) );

        /* check TupleKey comes out */
        key = (TupleKey) ((Object[]) retracted.get( 1 ))[0];
        assertSame( handle2,
                    key.get( 0 ) );

    }

}
