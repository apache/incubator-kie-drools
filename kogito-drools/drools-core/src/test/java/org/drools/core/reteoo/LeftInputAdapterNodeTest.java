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

package org.drools.core.reteoo;

import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.test.model.DroolsTestCase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.internal.KnowledgeBaseFactory;

import static org.junit.Assert.*;

@Ignore("phreak")
public class LeftInputAdapterNodeTest extends DroolsTestCase {
    private InternalKnowledgeBase kBase;
    private BuildContext buildContext;
    
    @Before
    public void setUp() throws Exception {
        this.kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        this.buildContext = new BuildContext( kBase, kBase.getReteooBuilder().getIdGenerator() );
    }
    
    @Test
    
    public void testLeftInputAdapterNode() {
        BuildContext context = new BuildContext(kBase, kBase.getReteooBuilder().getIdGenerator() );
        final EntryPointNode entryPoint = new EntryPointNode( -1,
                                                              kBase.getRete(),
                                                              context );
        entryPoint.attach(context);
                        
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  entryPoint,
                                                                  new ClassObjectType( Object.class ),
                                                                  context );
        
        objectTypeNode.attach(context);
        
        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
                                                                       objectTypeNode,
                 
                                                                       buildContext );
        
        assertEquals( 23,
                      liaNode.getId() );

        assertEquals( 0,
                      objectTypeNode.getSinkPropagator().getSinks().length );
        liaNode.attach(context);
        assertEquals( 1,
                      objectTypeNode.getSinkPropagator().getSinks().length );
    }

    /**
     * Tests the assertion of objects into LeftInputAdapterNode
     * 
     * @throws Exception
     */
    @Test
    public void testAssertObjectWithoutMemory() throws Exception {
        PropagationContextFactory pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        final PropagationContext pcontext = pctxFactory.createPropagationContext(0, PropagationContext.INSERTION, null, null, null);

        BuildContext context = new BuildContext(kBase, kBase.getReteooBuilder().getIdGenerator() );
        final EntryPointNode entryPoint = new EntryPointNode( -1,
                                                              kBase.getRete(),
                                                              context );
        entryPoint.attach(context);

        final ObjectTypeNode objectTypeNode = new ObjectTypeNode( 0,
                                                                  entryPoint,
                                                                  new ClassObjectType( Object.class ),
                                                                  context );

        objectTypeNode.attach(context);

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
                                                                       objectTypeNode,
                                                                       buildContext );
        liaNode.attach(context);

        final StatefulKnowledgeSessionImpl workingMemory = new StatefulKnowledgeSessionImpl( 1L, kBase );

        final MockLeftTupleSink sink = new MockLeftTupleSink();
        liaNode.addTupleSink( sink );

        final Object string1 = "cheese";

        // assert object
        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.insert( string1 );
        liaNode.assertObject( f0,
                              pcontext,
                              workingMemory );

        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        final Tuple tuple0 = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    workingMemory.getObject( tuple0.get( 0 ) ) );

    }


}
