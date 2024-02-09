/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import java.lang.reflect.Field;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectSourceTest  {

    @Test
    public void testObjectSourceConstructor() {
        final MockObjectSource source = new MockObjectSource( 15 );
        assertThat(source.getId()).isEqualTo(15);

        assertThat(source.getAttached()).isEqualTo(0);
        source.attach();
        assertThat(source.getAttached()).isEqualTo(1);
    }

    @Test
    public void testAddObjectSink() throws Exception {
        final MockObjectSource source = new MockObjectSource( 15 );

        // We need to re-assign this var each time the sink changes references
        final Field field = ObjectSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( source );

        assertThat(sink).isSameAs(EmptyObjectSinkAdapter.getInstance());
        final MockObjectSink sink1 = new MockObjectSink();
        source.addObjectSink( sink1 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(SingleObjectSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(1);

        final MockObjectSink sink2 = new MockObjectSink();
        source.addObjectSink( sink2 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(CompositeObjectSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(2);

        final MockObjectSink sink3 = new MockObjectSink();
        source.addObjectSink( sink3 );
        assertThat(sink).isInstanceOf(CompositeObjectSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(3);

        source.removeObjectSink( sink2 );
        assertThat(sink).isInstanceOf(CompositeObjectSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(2);

        source.removeObjectSink( sink1 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(SingleObjectSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(1);

        source.removeObjectSink( sink3 );
        sink = (ObjectSinkPropagator) field.get( source );
        assertThat(sink).isSameAs(EmptyObjectSinkAdapter.getInstance());
        assertThat(sink.getSinks()).hasSize(0);
    }
}
