/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.api.event.serialization;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.api.event.ProcessInstanceContextJobCloudEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.CALLBACK_ENDPOINT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.EXPIRATION_TIME;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.JOB_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.KOGITO_ADDONS;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.NODE_INSTANCE_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PRIORITY;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PROCESS_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.REPEAT_INTERVAL;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.REPEAT_LIMIT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ROOT_PROCESS_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ROOT_PROCESS_INSTANCE_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SOURCE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SPEC_VERSION;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SUBJECT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.TIME;

class JobCloudEventDeserializerTest {

    private static final String CANCEL_JOB_REQUEST_EVENT_RESOURCE = "org/kie/kogito/jobs/api/event/serialization/CancelJobRequestEvent.json";
    private static final String CREATE_PROCESS_INSTANCE_JOB_REQUEST_EVENT_RESOURCE = "org/kie/kogito/jobs/api/event/serialization/CreateProcessInstanceJobRequestEvent.json";
    private static final String UNEXPECTED_TYPE_EVENT_RESOURCE = "org/kie/kogito/jobs/api/event/serialization/UnexpectedTypeRequestEvent.json";

    private JobCloudEventDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new JobCloudEventDeserializer();
    }

    @Test
    void deserializeCancelJobRequestEvent() throws Exception {
        JobCloudEvent<?> result = deserializer.deserialize(readFileContent(CANCEL_JOB_REQUEST_EVENT_RESOURCE));
        assertThat(result).isInstanceOf(CancelJobRequestEvent.class);
        CancelJobRequestEvent event = (CancelJobRequestEvent) result;
        assertBaseFields(event);
        assertProcessContextFields(event);
        assertThat(event.getData()).isNotNull();
        assertThat(event.getData().getId()).isEqualTo(JOB_ID);
    }

    @Test
    void deserializeCreateProcessInstanceJobRequestEvent() throws Exception {
        JobCloudEvent<?> result = deserializer.deserialize(readFileContent(CREATE_PROCESS_INSTANCE_JOB_REQUEST_EVENT_RESOURCE));
        assertThat(result).isInstanceOf(CreateProcessInstanceJobRequestEvent.class);
        CreateProcessInstanceJobRequestEvent event = (CreateProcessInstanceJobRequestEvent) result;
        assertBaseFields(event);
        assertProcessContextFields(event);
        Job job = event.getData();
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(JOB_ID);
        assertThat(job.getExpirationTime()).isEqualTo(EXPIRATION_TIME);
        assertThat(job.getPriority()).isEqualTo(PRIORITY);
        assertThat(job.getCallbackEndpoint()).isEqualTo(CALLBACK_ENDPOINT);
        assertThat(job.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(job.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(job.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(job.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(job.getNodeInstanceId()).isEqualTo(NODE_INSTANCE_ID);
        assertThat(job.getRepeatInterval()).isEqualTo(REPEAT_INTERVAL);
        assertThat(job.getRepeatLimit()).isEqualTo(REPEAT_LIMIT);
    }

    @Test
    void deserializeUnexpectedType() throws Exception {
        byte[] fileContent = readFileContent(UNEXPECTED_TYPE_EVENT_RESOURCE);
        assertThatThrownBy(() -> deserializer.deserialize(fileContent))
                .isInstanceOf(DeserializationException.class)
                .hasMessage("Unknown JobCloudEvent event type: UnexpectedTypeRequest");
    }

    private static void assertProcessContextFields(ProcessInstanceContextJobCloudEvent<?> event) {
        assertThat(event.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(event.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(event.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(event.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(event.getKogitoAddons()).isEqualTo(KOGITO_ADDONS);
    }

    private static void assertBaseFields(JobCloudEvent<?> event) {
        assertThat(event.getId()).isEqualTo(ID);
        assertThat(event.getSpecVersion()).isEqualTo(SPEC_VERSION);
        assertThat(event.getSource()).isEqualTo(SOURCE);
        assertThat(event.getTime()).isEqualTo(TIME);
        assertThat(event.getSubject()).isEqualTo(SUBJECT);
    }

    private static byte[] readFileContent(String resource) throws Exception {
        URL url = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(resource), "Required test resource was not found in class path: " + resource);
        Path path = Paths.get(url.toURI());
        return Files.readAllBytes(path);
    }
}
