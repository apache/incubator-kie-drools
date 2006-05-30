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

import java.util.List;
import java.util.Map;

import org.drools.DroolsTestCase;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

public class LeftInputAdapterNodeTest extends DroolsTestCase {

    public void testLeftInputAdapterNode() {
        final MockObjectSource source = new MockObjectSource( 15 );
        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
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
        final MockObjectSource source = new MockObjectSource( 15 );

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                       source );

        assertEquals( 1,
                      liaNode.getId() );

        assertLength( 0,
                      source.getObjectSinksAsList() );

        liaNode.attach();

        assertLength( 1,
                      source.getObjectSinksAsList() );

        assertSame( liaNode,
                    source.getObjectSinks().getLastObjectSink() );
    }

    /**
     * Tests the assertion of objects into LeftInputAdapterNode
     * 
     * @throws Exception
     */
    public void testAssertObject() throws Exception {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( new ReteooRuleBase() );

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                       new MockObjectSource( 15 ) );
        final MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        final Object string1 = "cheese";

        // assert object
        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.assertObject( string1 );
        liaNode.assertObject( f0,
                              context,
                              workingMemory );

        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        final ReteTuple tuple0 = (ReteTuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    workingMemory.getObject( tuple0.get( 0 ) ) );

        // check node memory
        final Map map = (Map) workingMemory.getNodeMemory( liaNode );
        final LinkedList list0 = (LinkedList) map.get( f0 );
        assertEquals( 1,
                      list0.size() );
        assertSame( tuple0,
                    ((LinkedListObjectWrapper) list0.getFirst()).getObject() );

        // check memory stacks correctly
        final DefaultFactHandle f1 = (DefaultFactHandle) workingMemory.assertObject( "test1" );
        liaNode.assertObject( f1,
                              context,
                              workingMemory );

        assertLength( 2,
                      asserted );
        final ReteTuple tuple1 = (ReteTuple) ((Object[]) asserted.get( 1 ))[0];

        final LinkedList list1 = (LinkedList) map.get( f1 );
        assertEquals( 1,
                      list1.size() );
        assertSame( tuple1,
                    ((LinkedListObjectWrapper) list1.getFirst()).getObject() );

        assertNotSame( tuple0,
                       tuple1 );

    }

    /**
     * Tests the retractions from a LeftInputAdapterNode.
     * Object Assertions result in tuple propagations, so we 
     * test the remove(...) method
     * @throws Exception
     */
    public void testRetractObject() throws Exception {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( new ReteooRuleBase() );

        final MockObjectSource source = new MockObjectSource( 15 );

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                       source );
        final MockTupleSink sink = new MockTupleSink();
        liaNode.addTupleSink( sink );

        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.assertObject( "f1" );

        /* assert object */
        liaNode.assertObject( f0,
                              context,
                              workingMemory );

        final ReteTuple tuple = (ReteTuple) ((Object[]) sink.getAsserted().get( 0 ))[0];

        liaNode.retractObject( f0,
                               context,
                               workingMemory );

        final Map map = (Map) workingMemory.getNodeMemory( liaNode );
        assertNull( map.get( f0 ) );

        assertSame( tuple,
                    ((Object[]) sink.getRetracted().get( 0 ))[0] );

    }

}