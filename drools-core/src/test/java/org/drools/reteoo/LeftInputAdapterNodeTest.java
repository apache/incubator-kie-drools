/**
 * Copyright 2010 JBoss Inc
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

import java.lang.reflect.Field;
import java.util.List;

import org.drools.DroolsTestCase;
import org.drools.RuleBaseFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class LeftInputAdapterNodeTest extends DroolsTestCase {
    private ReteooRuleBase ruleBase;
    private BuildContext buildContext;
    
    protected void setUp() throws Exception {
        this.ruleBase = ( ReteooRuleBase ) RuleBaseFactory.newRuleBase();
        this.buildContext = new BuildContext( ruleBase, ((ReteooRuleBase)ruleBase).getReteooBuilder().getIdGenerator() );
    }
    
    public void testLeftInputAdapterNode() {
        final MockObjectSource source = new MockObjectSource( 15 );
        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 23,
                                                                       source,
                                                                       buildContext );
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
                                                                       source,
                                                                       buildContext );
        final Field field = ObjectSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( source );

        assertEquals( 1,
                      liaNode.getId() );
        assertNotNull( sink );

        liaNode.attach();

        sink = (ObjectSinkPropagator) field.get( source );

        assertEquals( 1,
                      sink.getSinks().length );

        assertSame( liaNode,
                    sink.getSinks()[0] );
    }

    /**
     * Tests the assertion of objects into LeftInputAdapterNode
     * 
     * @throws Exception
     */
    public void testAssertObjectWithoutMemory() throws Exception {
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.ASSERTION,
                                                                       null,
                                                                       null,
                                                                       null );

        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           (ReteooRuleBase) RuleBaseFactory.newRuleBase() );

        final LeftInputAdapterNode liaNode = new LeftInputAdapterNode( 1,
                                                                       new MockObjectSource( 15 ),
                                                                       buildContext );
        final MockLeftTupleSink sink = new MockLeftTupleSink();
        liaNode.addTupleSink( sink );

        final Object string1 = "cheese";

        // assert object
        final DefaultFactHandle f0 = (DefaultFactHandle) workingMemory.insert( string1 );
        liaNode.assertObject( f0,
                              context,
                              workingMemory );

        final List asserted = sink.getAsserted();
        assertLength( 1,
                      asserted );
        final Tuple tuple0 = (Tuple) ((Object[]) asserted.get( 0 ))[0];
        assertSame( string1,
                    workingMemory.getObject( tuple0.get( 0 ) ) );

    }


}