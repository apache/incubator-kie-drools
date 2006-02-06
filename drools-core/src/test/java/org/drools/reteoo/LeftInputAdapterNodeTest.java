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

    /**
     * Tests the attaching of the LeftInputAdapterNode to an ObjectSource
     * @throws Exception
     */
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

    /**
     * Tests the assertion of objects into LeftInputAdapterNode
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
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

        FactHandleImpl handle1 = new FactHandleImpl( 1 );

        workingMemory.putObject( handle1,
                                 string1 );

        /* assert object */
        liaNode.assertObject( handle1,
                              context,
                              workingMemory );

        List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );

        /* check tuple comes out */
        ReteTuple tuple = (ReteTuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    workingMemory.getObject( tuple.get( 0 ) ) );
    }

    /**
     * Tests the retractions from a LeftInputAdapterNode.
     * Object Assertions result in tuple propagations, so we 
     * test the remove(...) method
     * @throws Exception
     */
    public void testRetractObject() throws Exception {
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

        FactHandleImpl handle1 = new FactHandleImpl( 1 );

        workingMemory.putObject( handle1,
                          string1 );

        /* assert object */
        liaNode.assertObject( handle1,
                              context,
                              workingMemory );
        
        ReteTuple tuple = (ReteTuple) ((Object[]) sink.getAsserted().get( 0 ))[0];
        
        ReteTuple previous = new ReteTuple(0, handle1, workingMemory);
        ReteTuple next = new ReteTuple(0, handle1, workingMemory);
        
        tuple.setPrevious( previous );
        tuple.setNext( next );
        
        tuple.remove( context, workingMemory );
        
        assertSame(previous.getNext(), next);
        assertSame(next.getPrevious(), previous);
        
        assertNull(tuple.getPrevious());
        assertNull(tuple.getNext());

    }

}
