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
import java.util.Collections;

import org.drools.core.impl.InternalRuleBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.builder.BuildContext;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class TupleSourceTest {

    @Test
    public void testObjectTupleConstructor() {
        KieBaseConfiguration kconf = RuleBaseFactory.newKnowledgeBaseConfiguration();
        InternalRuleBase kBase =  RuleBaseFactory.newRuleBase(kconf);
        BuildContext          buildContext = new BuildContext(kBase, Collections.emptyList());

        final MockTupleSource source = new MockTupleSource(15, buildContext);
        assertThat(source.getId()).isEqualTo(15);
    }

    @Test
    public void testAddTupleSink() throws Exception {

        KieBaseConfiguration kconf = RuleBaseFactory.newKnowledgeBaseConfiguration();
        InternalRuleBase kBase = RuleBaseFactory.newRuleBase(kconf);
        BuildContext          buildContext = new BuildContext(kBase, Collections.emptyList());

        final MockTupleSource source       = new MockTupleSource(15, buildContext);

        // We need to re-assign this var each time the sink changes references
        final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
        field.setAccessible( true );
        LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( source );

        assertThat(sink).isSameAs(EmptyLeftTupleSinkAdapter.getInstance());
        final MockLeftTupleSink sink1 = new MockLeftTupleSink(buildContext);
        source.addTupleSink( sink1 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(SingleLeftTupleSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(1);

        final MockLeftTupleSink sink2 = new MockLeftTupleSink(buildContext);
        source.addTupleSink( sink2 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(CompositeLeftTupleSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(2);

        final MockLeftTupleSink sink3 = new MockLeftTupleSink(buildContext);
        source.addTupleSink( sink3 );
        assertThat(sink).isInstanceOf(CompositeLeftTupleSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(3);

        source.removeTupleSink( sink2 );
        assertThat(sink).isInstanceOf(CompositeLeftTupleSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(2);

        source.removeTupleSink( sink1 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertThat(sink).isInstanceOf(SingleLeftTupleSinkAdapter.class);
        assertThat(sink.getSinks()).hasSize(1);

        source.removeTupleSink( sink3 );
        sink = (LeftTupleSinkPropagator) field.get( source );
        assertThat(sink).isSameAs(EmptyLeftTupleSinkAdapter.getInstance());
        assertThat(sink.getSinks()).hasSize(0);
    }

}
