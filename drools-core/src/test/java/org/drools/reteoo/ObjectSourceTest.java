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
import org.drools.FactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class ObjectSourceTest extends DroolsTestCase {

    public void testObjectSourceConstructor() {
        MockObjectSource source = new MockObjectSource( 15 );
        assertEquals( 15,
                      source.getId() );

        assertEquals( 0,
                      source.getAttached() );
        source.attach();
        assertEquals( 1,
                      source.getAttached() );
    }

    public void testAddObjectSink() {
        MockObjectSource source = new MockObjectSource( 15 );
        assertLength( 0,
                      source.getObjectSinksAsList() );

        source.addObjectSink( new MockObjectSink() );
        assertLength( 1,
                      source.getObjectSinksAsList() );

        source.addObjectSink( new MockObjectSink() );
        assertLength( 2,
                      source.getObjectSinksAsList() );
    }

    public void testPropagateAssertObject() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.ASSERTION,
                                                                 null,
                                                                 null );
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockObjectSource source = new MockObjectSource( 15 );
        MockObjectSink sink1 = new MockObjectSink();
        source.addObjectSink( sink1 );
        assertLength( 0,
                      sink1.getAsserted() );

        FactHandleImpl f1 = (FactHandleImpl) workingMemory.assertObject( new Integer( 1 ) );
        source.propagateAssertObject( f1,
                                      context,
                                      workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        Object[] list = (Object[]) sink1.getAsserted().get( 0 );
        assertEquals( new Integer( 1 ),
                      workingMemory.getObject( (FactHandleImpl) list[0] ) );

        MockObjectSink sink2 = new MockObjectSink();
        source.addObjectSink( sink2 );

        FactHandleImpl f2 = (FactHandleImpl) workingMemory.assertObject( new Integer( 2 ) );
        source.propagateAssertObject( f2,
                                      context,
                                      workingMemory );

        assertLength( 2,
                      sink1.getAsserted() );

        assertLength( 1,
                      sink2.getAsserted() );

        list = (Object[]) sink1.getAsserted().get( 0 );
        assertEquals( new Integer( 1 ),
                      workingMemory.getObject( (FactHandle) list[0] ) );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink1.getAsserted().get( 1 );
        assertEquals( new Integer( 2 ),
                      workingMemory.getObject( (FactHandle) list[0] ) );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink2.getAsserted().get( 0 );
        assertEquals( new Integer( 2 ),
                      workingMemory.getObject( (FactHandle) list[0] ) );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );
    }

    public void testPropagateRetractObject() throws Exception {
        Rule rule = new Rule( "test-rule" );
        PropagationContext context = new PropagationContextImpl( 0,
                                                                 PropagationContext.RETRACTION,
                                                                 null,
                                                                 null );
        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );

        MockObjectSource source = new MockObjectSource( 15 );

        // Test propagation with one ObjectSink
        MockObjectSink sink1 = new MockObjectSink();
        source.addObjectSink( sink1 );
        assertLength( 0,
                      sink1.getRetracted() );

        source.propagateRetractObject( new FactHandleImpl( 2 ),
                                       context,
                                       workingMemory );

        assertLength( 1,
                      sink1.getRetracted() );

        Object[] list = (Object[]) sink1.getRetracted().get( 0 );
        assertEquals( new FactHandleImpl( 2 ),
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        // Test propagation with two ObjectSinks
        MockObjectSink sink2 = new MockObjectSink();
        source.addObjectSink( sink2 );

        source.propagateRetractObject( new FactHandleImpl( 3 ),
                                       context,
                                       workingMemory );

        assertLength( 2,
                      sink1.getRetracted() );

        assertLength( 1,
                      sink2.getRetracted() );

        list = (Object[]) sink1.getRetracted().get( 0 );
        assertEquals( new FactHandleImpl( 2 ),
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink1.getRetracted().get( 1 );
        assertEquals( new FactHandleImpl( 3 ),
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink2.getRetracted().get( 0 );
        assertEquals( new FactHandleImpl( 3 ),
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );
    }
}