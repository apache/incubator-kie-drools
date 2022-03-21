/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.services.event.correlation;

import org.junit.jupiter.api.Test;
import org.kie.kogito.correlation.Correlation;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.services.event.DummyCloudEvent;
import org.kie.kogito.services.event.DummyEvent;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleAttributeCorrelationResolverTest {

    public static final String SOURCE_KEY = "source";
    public static final String TYPE_KEY = "type";
    public static final String REFERENCE_ID_KEY = CloudEventExtensionConstants.PROCESS_REFERENCE_ID;
    public static final String DATA_KEY = "data";
    private SimpleAttributeCorrelationResolver sourceResolver = new SimpleAttributeCorrelationResolver(SOURCE_KEY);
    private SimpleAttributeCorrelationResolver typeResolver = new SimpleAttributeCorrelationResolver(TYPE_KEY);
    private SimpleAttributeCorrelationResolver kogitoReferenceResolver = new SimpleAttributeCorrelationResolver(REFERENCE_ID_KEY);
    private SimpleAttributeCorrelationResolver dataResolver = new SimpleAttributeCorrelationResolver(DATA_KEY, DummyEvent.class);
    private DummyEvent payload = new DummyEvent("test");
    private DummyCloudEvent event = new DummyCloudEvent(payload, "type", "source", "referenceId");

    @Test
    void testResolveStringAttribute() {
        Correlation source = sourceResolver.resolve(event);
        assertThat(source.getKey()).isEqualTo(SOURCE_KEY);
        assertThat(source.getValue()).isEqualTo("source");

        Correlation type = typeResolver.resolve(event);
        assertThat(type.getKey()).isEqualTo(TYPE_KEY);
        assertThat(type.getValue()).isEqualTo("type");

        Correlation referenceId = kogitoReferenceResolver.resolve(event);
        assertThat(referenceId.getKey()).isEqualTo(REFERENCE_ID_KEY);
        assertThat(referenceId.getValue()).isEqualTo("referenceId");
    }

    @Test
    void testResolveObjectAttribute() {
        Correlation data = dataResolver.resolve(event);
        assertThat(data.getKey()).isEqualTo(DATA_KEY);
        assertThat(data.getValue()).isEqualTo(payload);
    }

    @Test
    void testResolveNullAttribute() {
        Correlation source = sourceResolver.resolve(null);
        assertThat(source.getValue()).isNull();
        Correlation data = dataResolver.resolve(null);
        assertThat(data.getValue()).isNull();
    }
}
