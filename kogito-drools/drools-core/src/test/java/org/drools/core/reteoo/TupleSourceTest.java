/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Field;

import org.drools.core.test.model.DroolsTestCase;

import org.junit.Test;

import static org.junit.Assert.*;

public class TupleSourceTest extends DroolsTestCase {

    @Test
    public void testObjectTupleConstructor() {
        final MockTupleSource source = new MockTupleSource( 15 );
        assertEquals( 15,
                      source.getId() );
    }

    @Test
    public void testAddTupleSink() throws Exception {
        final MockTupleSource source = new MockTupleSource( 15 );

        // We need to re-assign this var each time the sink changes references
        final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( source );

        assertSame( EmptyLeftTupleSinkAdapter.getInstance(),
                    sink );

        final MockLeftTupleSink sink1 = new MockLeftTupleSink();
        source.addTupleSink( sink1 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertSame( SingleLeftTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );

        final MockLeftTupleSink sink2 = new MockLeftTupleSink();
        source.addTupleSink( sink2 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertSame( CompositeLeftTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        final MockLeftTupleSink sink3 = new MockLeftTupleSink();
        source.addTupleSink( sink3 );
        assertSame( CompositeLeftTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 3,
                      sink.getSinks().length );

        source.removeTupleSink( sink2 );
        assertSame( CompositeLeftTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        source.removeTupleSink( sink1 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertSame( SingleLeftTupleSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );

        source.removeTupleSink( sink3 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertSame( EmptyLeftTupleSinkAdapter.getInstance(),
                    sink );
        assertEquals( 0,
                      sink.getSinks().length );
    }

}
