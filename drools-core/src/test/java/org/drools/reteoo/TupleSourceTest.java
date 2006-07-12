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
import org.drools.common.DefaultFactHandle;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public class TupleSourceTest extends DroolsTestCase {

    public void testObjectTupleConstructor() {
        final MockTupleSource source = new MockTupleSource( 15 );
        assertEquals( 15,
                      source.getId() );

        assertEquals( 0,
                      source.getAttached() );
        source.attach();
        assertEquals( 1,
                      source.getAttached() );
    }

    public void testAddTupleSink() {
        final MockTupleSource source = new MockTupleSource( 15 );
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
        final Rule rule = new Rule( "test-rule" );
        final PropagationContext context = new PropagationContextImpl( 0,
                                                                       PropagationContext.RETRACTION,
                                                                       null,
                                                                       null );
        final ReteooWorkingMemory workingMemory = new ReteooWorkingMemory( 1,
                                                                           new ReteooRuleBase() );

        final MockTupleSource source = new MockTupleSource( 15 );
        final MockTupleSink sink1 = new MockTupleSink();

        source.addTupleSink( sink1 );
        assertLength( 0,
                      sink1.getAsserted() );

        final ReteTuple tuple1 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );

        source.propagateAssertTuple( tuple1,
                                     context,
                                     workingMemory );

        assertLength( 1,
                      sink1.getAsserted() );

        Object[] list = (Object[]) sink1.getAsserted().get( 0 );
        assertEquals( tuple1,
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        final ReteTuple tuple2 = new ReteTuple( new DefaultFactHandle( 1,
                                                                       "cheese" ) );

        final MockTupleSink sink2 = new MockTupleSink();
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
        assertEquals( tuple1,
                      list[0] );
        assertTrue( tuple2.equals( list[0] ) );

        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink1.getAsserted().get( 1 );
        assertEquals( tuple2,
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );

        list = (Object[]) sink2.getAsserted().get( 0 );
        assertEquals( tuple2,
                      list[0] );
        assertSame( context,
                    list[1] );
        assertSame( workingMemory,
                    list[2] );
    }

    //    public void testAttachNewNode() {
    //        PropagationContext context = new PropagationContextImpl( 0,
    //                                                                 PropagationContext.ASSERTION,
    //                                                                 null,
    //                                                                 null );
    //        WorkingMemoryImpl workingMemory = new WorkingMemoryImpl( new RuleBaseImpl() );
    //
    //        MockTupleSource source = new MockTupleSource( 15 );
    //
    //        // Add two Tuple Sinks
    //        MockTupleSink sink1 = new MockTupleSink();
    //        source.addTupleSink( sink1 );
    //
    //        MockTupleSink sink2 = new MockTupleSink();
    //        source.addTupleSink( sink2 );
    //
    //        // Only the last added TupleSink should receive facts
    //        source.attachingNewNode = true;
    //
    //        ReteTuple tuple1 = new ReteTuple( new FactHandleImpl( 2 ) );
    //
    //        source.propagateAssertTuple( tuple1,
    //                                     context,
    //                                     workingMemory );
    //
    //        assertLength( 0,
    //                      sink1.getAsserted() );
    //        assertLength( 1,
    //                      sink2.getAsserted() );
    //
    //        // Now all sinks should receive values
    //        source.attachingNewNode = false;
    //
    //        ReteTuple tuple2 = new ReteTuple( new FactHandleImpl( 3 ) );
    //
    //        source.propagateAssertTuple( tuple2,
    //                                     context,
    //                                     workingMemory );
    //
    //        /* Both sinks receive one object */
    //        assertLength( 1,
    //                      sink1.getAsserted() );
    //        assertLength( 2,
    //                      sink2.getAsserted() );
    //
    //    }

}