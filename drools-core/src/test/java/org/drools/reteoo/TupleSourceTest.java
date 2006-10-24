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

import org.drools.DroolsTestCase;

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

    public void testAddTupleSink() throws Exception {
        final MockTupleSource source = new MockTupleSource( 15 );

        // We need to re-assign this var each time the sink changes references
        final Field field = TupleSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        TupleSinkPropagator sink = (TupleSinkPropagator) field.get( source );

        assertSame( EmptyTupleSinkAdapter.getInstance(), sink );

        final MockTupleSink sink1 = new MockTupleSink();
        source.addTupleSink( sink1 );
        sink = (TupleSinkPropagator) field.get( source );
        assertSame( SingleTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );

        final MockTupleSink sink2 = new MockTupleSink();
        source.addTupleSink( sink2 );
        sink = (TupleSinkPropagator) field.get( source );
        assertSame( CompositeTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        final MockTupleSink sink3 = new MockTupleSink();
        source.addTupleSink( sink3 );
        assertSame( CompositeTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 3,
                      sink.getSinks().length );

        source.removeTupleSink( sink2 );
        assertSame( CompositeTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        source.removeTupleSink( sink1 );
        sink = (TupleSinkPropagator) field.get( source );
        assertSame( SingleTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );
        
        source.removeTupleSink( sink3 );
        sink = (TupleSinkPropagator) field.get( source );
        assertSame( EmptyTupleSinkAdapter.getInstance(),
                    sink );
        assertEquals( 0,
                      sink.getSinks().length );        
    }

}