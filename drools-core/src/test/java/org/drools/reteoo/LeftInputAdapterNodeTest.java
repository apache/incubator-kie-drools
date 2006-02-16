package org.drools.reteoo;

import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNodeWrapper;

public class LeftInputAdapterNodeTest extends DroolsTestCase {

    public void testLeftInputAdapterNode() {
        MockObjectSource source = new MockObjectSource( 15 );
        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
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

        LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                 new MockObjectSource( 15 ) );
        MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        Object string1 = "cheese";

        
        // assert object
        FactHandleImpl f0 = (FactHandleImpl) workingMemory.assertObject( string1 ); 
        liaNode.assertObject( f0,
                              context,
                              workingMemory );

        List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        ReteTuple tuple0 = (ReteTuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    workingMemory.getObject( tuple0.get( 0 ) ) );
        
        // check node memory
        Map map = (Map) workingMemory.getNodeMemory( liaNode );        
        LinkedList list0 = (LinkedList) (LinkedList)map.get( f0 ); 
        assertEquals( 1, list0.size() );
        assertSame( tuple0, ((LinkedListNodeWrapper)list0.getFirst()).getNode() );
        
        // check memory stacks correctly
        FactHandleImpl f1 = (FactHandleImpl) workingMemory.assertObject( "test1" ); 
        liaNode.assertObject( f1,
                              context,
                              workingMemory );
        
        assertLength( 2,
                      asserted );
        ReteTuple tuple1 = (ReteTuple) ((Object[]) asserted.get( 1 ))[0];        
        
        LinkedList list1 = (LinkedList) (LinkedList)map.get( f1 );         
        assertEquals( 1, list1.size() );
        assertSame( tuple1, ((LinkedListNodeWrapper)list1.getFirst()).getNode() );
        
        assertNotSame( tuple0, tuple1 );
        
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
                                                                 source );
        MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        FactHandleImpl f0 = (FactHandleImpl) workingMemory.assertObject( "f1" );

        /* assert object */
        liaNode.assertObject( f0,
                              context,
                              workingMemory );
        
        ReteTuple tuple = (ReteTuple) ((Object[]) sink.getAsserted().get( 0 ))[0];

        liaNode.retractObject( f0, context, workingMemory );

        Map map = (Map) workingMemory.getNodeMemory( liaNode );        
        assertNull( map.get( f0 ) );     
        
        assertSame( tuple, (ReteTuple) ((Object[]) sink.getRetracted().get( 0 ))[0] );
        
    }

}
