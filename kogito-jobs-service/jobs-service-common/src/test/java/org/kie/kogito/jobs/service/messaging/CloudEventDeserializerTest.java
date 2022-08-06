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

package org.kie.kogito.jobs.service.messaging;

import java.net.URI;

import org.junit.jupiter.api.Test;

import io.cloudevents.CloudEvent;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.TestUtils.readFileContent;

@QuarkusTest
class CloudEventDeserializerTest {

    private static final String CLOUD_EVENT_RESOURCE = "org/kie/kogito/jobs/service/messaging/CloudEvent.json";

    @Test
    void deserialize() throws Exception {
        CloudEventDeserializer deserializer = new CloudEventDeserializer();
        CloudEvent cloudEvent = deserializer.deserialize("topic", readFileContent(CLOUD_EVENT_RESOURCE));

        assertThat(cloudEvent).isNotNull();
        assertThat(cloudEvent.getSpecVersion()).hasToString("1.0");
        assertThat(cloudEvent.getId()).isEqualTo("eventId");
        assertThat(cloudEvent.getSource()).isEqualTo(URI.create("http://event_source"));
        assertThat(cloudEvent.getType()).isEqualTo("eventType");
        assertThat(cloudEvent.getDataSchema()).isEqualTo(URI.create("http://event_data_schema/schema.json"));
        assertThat(cloudEvent.getDataContentType()).isEqualTo("application/json; charset=utf-8");
        assertThat(cloudEvent.getSubject()).isEqualTo("eventSubject");
        assertThat(cloudEvent.getData()).isNotNull();
        assertThat(cloudEvent.getData().toBytes()).isEqualTo("{\"dataField1\":\"eventData1\",\"dataField2\":\"eventData2\"}".getBytes());
        assertThat(cloudEvent.getExtensionNames()).containsExactlyInAnyOrder("extension1", "extension2");
        assertThat(cloudEvent.getExtension("extension1")).isEqualTo("eventExtension1");
        assertThat(cloudEvent.getExtension("extension2")).isEqualTo("eventExtension2");
    }

}
