/*
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
package org.kie.kogito.serverless.workflow.monitoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.monitoring.SonataFlowMetricProcessEventListener.ArrayStoreMode;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.node.ArrayNode;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonataFlowMetricProcessEventListenerTest {

    private static final String PROCESS_ID = "testMetric";

    private Counter counter;
    private MeterRegistry meterRegistry;
    private KogitoGAV kogitoGAV;
    private Counter.Builder builder;
    private MockedStatic<Counter> factory;

    @BeforeEach
    void setup() {
        counter = mock(Counter.class);
        meterRegistry = mock(MeterRegistry.class);
        builder = mock(Counter.Builder.class);
        factory = mockStatic(Counter.class);
        factory.when(() -> Counter.builder(SonataFlowMetricProcessEventListener.INPUT_PARAMS_COUNTER_NAME)).thenReturn(builder);
        when(builder.register(meterRegistry)).thenReturn(counter);
        when(builder.description(anyString())).thenReturn(builder);
        when(builder.tag(anyString(), anyString())).thenReturn(builder);
        kogitoGAV = new KogitoGAV("org.kogito", "test-artifact", "999-SNAPSHOT");
    }

    @AfterEach
    void clean() {
        factory.close();
    }

    @Test
    void testSimpleCall() {
        SonataFlowMetricProcessEventListener listener = new SonataFlowMetricProcessEventListener(kogitoGAV, meterRegistry, ArrayStoreMode.JSON_STRING);
        listener.registerObject(PROCESS_ID, null, ObjectMapperFactory.get().createObjectNode().put("number", 1));
        listener.registerObject(PROCESS_ID, null, ObjectMapperFactory.get().createObjectNode().put("number", 2));
        verify(builder, times(2)).tag("process_id", PROCESS_ID);
        verify(builder, times(2)).tag("param_name", "number");
        verify(builder).tag("param_value", "1");
        verify(builder).tag("param_value", "2");
        verify(counter, times(2)).increment();
    }

    @Test
    void testComplexCall() {
        SonataFlowMetricProcessEventListener listener = new SonataFlowMetricProcessEventListener(kogitoGAV, meterRegistry, ArrayStoreMode.JSON_STRING);
        listener.registerObject(PROCESS_ID, null,
                ObjectMapperFactory.get().createObjectNode().set("team", ObjectMapperFactory.get().createObjectNode().put("name", "Real Betis Balompie").put("age", 117)));
        verify(builder, times(2)).tag("process_id", PROCESS_ID);
        verify(builder).tag("param_name", "team.name");
        verify(builder).tag("param_value", "Real Betis Balompie");
        verify(builder).tag("param_name", "team.age");
        verify(builder).tag("param_value", "117");
        verify(counter, times(2)).increment();
    }

    @Test
    void testArrayMultiParam() {
        SonataFlowMetricProcessEventListener listener = new SonataFlowMetricProcessEventListener(kogitoGAV, meterRegistry, ArrayStoreMode.MULTI_PARAM);
        listener.registerObject(PROCESS_ID, null,
                ObjectMapperFactory.get().createObjectNode().set("teams",
                        ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Betis Balompie"))
                                .add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Sociedad"))));
        verify(builder, times(2)).tag("process_id", PROCESS_ID);
        verify(builder).tag("param_name", "teams[0].name");
        verify(builder).tag("param_value", "Real Betis Balompie");
        verify(builder).tag("param_name", "teams[1].name");
        verify(builder).tag("param_value", "Real Sociedad");
        verify(counter, times(2)).increment();
    }

    @Test
    void testArrayJsonString() {
        SonataFlowMetricProcessEventListener listener = new SonataFlowMetricProcessEventListener(kogitoGAV, meterRegistry, ArrayStoreMode.JSON_STRING);
        ArrayNode arrayNode = ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Betis Balompie"))
                .add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Sociedad"));
        listener.registerObject(PROCESS_ID, null,
                ObjectMapperFactory.get().createObjectNode().set("teams", arrayNode));
        verify(builder).tag("process_id", PROCESS_ID);
        verify(builder).tag("param_name", "teams");
        verify(builder).tag("param_value", arrayNode.toString());
        verify(counter).increment();
    }

    @Test
    void testArrayString() {
        SonataFlowMetricProcessEventListener listener = new SonataFlowMetricProcessEventListener(kogitoGAV, meterRegistry, ArrayStoreMode.STRING);
        ArrayNode arrayNode = ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Betis Balompie"))
                .add(ObjectMapperFactory.get().createObjectNode().put("name", "Real Sociedad"));
        listener.registerObject(PROCESS_ID, null,
                ObjectMapperFactory.get().createObjectNode().set("teams", arrayNode));
        verify(builder).tag("process_id", PROCESS_ID);
        verify(builder).tag("param_name", "teams");
        verify(builder).tag("param_value", "[{name=Real Betis Balompie}, {name=Real Sociedad}]");
        verify(counter).increment();
    }
}
