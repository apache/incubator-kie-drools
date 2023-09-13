/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.addon;

import java.io.UncheckedIOException;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.service.IndexingService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.model.ProcessInstanceState.COMPLETED;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class DataIndexEventPublisherTest {
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    public static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    private static final String CALLBACK_ENDPOINT = "http://my_service";
    private static final String JOB_ID = "JOB_ID";
    private static final String STATUS = "SCHEDULED";
    private static final ZonedDateTime LAST_UPDATE = ZonedDateTime.parse("2023-04-12T15:00:00.001Z");
    private static final Integer RETRIES = 1;
    private static final Integer PRIORITY = 3;
    private static final Integer EXECUTION_COUNTER = 1;
    private static final String SCHEDULE_ID = "SCHEDULE_ID";
    private static final ZonedDateTime EXPIRATION_TIME = ZonedDateTime.parse("2023-04-13T00:00:00.001Z");

    @Mock
    IndexingService indexingService;

    private static DataIndexEventPublisher dataIndexEventPublisher;

    @BeforeEach
    public void setup() {
        dataIndexEventPublisher = new DataIndexEventPublisher();
        dataIndexEventPublisher.setIndexingService(indexingService);
    }

    @Test
    void onProcessInstanceEvent() {
        ArgumentCaptor<ProcessInstance> eventCaptor = ArgumentCaptor.forClass(ProcessInstance.class);
        ProcessInstanceDataEvent event = getProcessCloudEvent(PROCESS_ID, PROCESS_INSTANCE_ID, COMPLETED,
                ROOT_PROCESS_INSTANCE_ID, ROOT_PROCESS_ID, ROOT_PROCESS_INSTANCE_ID, "currentUser");

        dataIndexEventPublisher.publish(event);

        verify(indexingService).indexProcessInstance(eventCaptor.capture());
    }

    @Test
    void onJobEvent() throws Exception {
        ArgumentCaptor<Job> eventCaptor = ArgumentCaptor.forClass(Job.class);

        byte[] jsonContent = getObjectMapper().writeValueAsBytes(buildJob());

        DataEvent event = new TestingDataEvent("JobEvent", "source", jsonContent,
                PROCESS_INSTANCE_ID, ROOT_PROCESS_INSTANCE_ID, PROCESS_ID, ROOT_PROCESS_ID);
        dataIndexEventPublisher.publish(event);

        verify(indexingService).indexJob(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getId()).isEqualTo(JOB_ID);
        assertThat(eventCaptor.getValue().getProcessId()).isEqualTo(PROCESS_ID);

        assertThat(eventCaptor.getValue().getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(eventCaptor.getValue().getNodeInstanceId()).isEqualTo(NODE_INSTANCE_ID);
        assertThat(eventCaptor.getValue().getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(eventCaptor.getValue().getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(eventCaptor.getValue().getExpirationTime()).isEqualTo(EXPIRATION_TIME);
        assertThat(eventCaptor.getValue().getPriority()).isEqualTo(PRIORITY);
        assertThat(eventCaptor.getValue().getRepeatInterval()).isZero();
        assertThat(eventCaptor.getValue().getRepeatLimit()).isZero();
        assertThat(eventCaptor.getValue().getScheduledId()).isEqualTo(SCHEDULE_ID);
        assertThat(eventCaptor.getValue().getRetries()).isEqualTo(RETRIES);
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo(STATUS);
        assertThat(eventCaptor.getValue().getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(eventCaptor.getValue().getCallbackEndpoint()).isEqualTo(CALLBACK_ENDPOINT);
        assertThat(eventCaptor.getValue().getExecutionCounter()).isEqualTo(EXECUTION_COUNTER);
    }

    @Test
    void onMalformedJobEvent() throws Exception {
        byte[] jsonContent = getObjectMapper().writeValueAsBytes("MalformedJob");

        DataEvent event = new TestingDataEvent("JobEvent", "source", jsonContent,
                PROCESS_INSTANCE_ID, ROOT_PROCESS_INSTANCE_ID, PROCESS_ID, ROOT_PROCESS_ID);
        assertThrows(UncheckedIOException.class, () -> dataIndexEventPublisher.publish(event));
        verifyNoInteractions(indexingService);
    }

    public static class TestingDataEvent extends AbstractDataEvent<byte[]> {
        public TestingDataEvent(String type,
                String source,
                byte[] data,
                String kogitoProcessInstanceId,
                String kogitoRootProcessInstanceId,
                String kogitoProcessId,
                String kogitoRootProcessId) {
            super(type, source, data, kogitoProcessInstanceId, kogitoRootProcessInstanceId, kogitoProcessId,
                    kogitoRootProcessId, null, null);
        }
    }

    private Job buildJob() {
        Job job = new Job();
        job.setId(JOB_ID);
        job.setProcessId(PROCESS_ID);
        job.setProcessInstanceId(PROCESS_INSTANCE_ID);
        job.setNodeInstanceId(NODE_INSTANCE_ID);
        job.setRootProcessId(ROOT_PROCESS_ID);
        job.setRootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID);

        job.setExpirationTime(EXPIRATION_TIME);
        job.setPriority(PRIORITY);
        job.setRepeatInterval(0L);
        job.setRepeatLimit(0);

        job.setScheduledId(SCHEDULE_ID);
        job.setRetries(RETRIES);
        job.setStatus(STATUS);
        job.setLastUpdate(LAST_UPDATE);
        job.setExecutionCounter(EXECUTION_COUNTER);
        job.setCallbackEndpoint(CALLBACK_ENDPOINT);
        return job;
    }

}
