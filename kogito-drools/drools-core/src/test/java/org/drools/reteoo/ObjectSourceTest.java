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

import org.drools.DroolsTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectSourceTest extends DroolsTestCase {

    @Test
    public void testObjectSourceConstructor() {
        final MockObjectSource source = new MockObjectSource( 15 );
        assertEquals( 15,
                      source.getId() );

        assertEquals( 0,
                      source.getAttached() );
        source.attach();
        assertEquals( 1,
                      source.getAttached() );
    }

    @Test
    public void testAddObjectSink() throws Exception {
        final MockObjectSource source = new MockObjectSource( 15 );

        // We need to re-assign this var each time the sink changes references
        final Field field = ObjectSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( source );

        assertSame( EmptyObjectSinkAdapter.getInstance(),
                    sink );

        final MockObjectSink sink1 = new MockObjectSink();
        source.addObjectSink( sink1 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertSame( SingleObjectSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );

        final MockObjectSink sink2 = new MockObjectSink();
        source.addObjectSink( sink2 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertSame( CompositeObjectSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        final MockObjectSink sink3 = new MockObjectSink();
        source.addObjectSink( sink3 );
        assertSame( CompositeObjectSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 3,
                      sink.getSinks().length );

        source.removeObjectSink( sink2 );
        assertSame( CompositeObjectSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 2,
                      sink.getSinks().length );

        source.removeObjectSink( sink1 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertSame( SingleObjectSinkAdapter.class,
                    sink.getClass() );
        assertEquals( 1,
                      sink.getSinks().length );

        source.removeObjectSink( sink3 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertSame( EmptyObjectSinkAdapter.getInstance(),
                    sink );
        assertEquals( 0,
                      sink.getSinks().length );
    }
}
