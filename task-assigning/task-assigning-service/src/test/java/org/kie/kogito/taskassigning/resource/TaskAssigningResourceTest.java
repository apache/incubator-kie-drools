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

package org.kie.kogito.taskassigning.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.service.ServiceMessage;
import org.kie.kogito.taskassigning.service.ServiceStatus;
import org.kie.kogito.taskassigning.service.ServiceStatusInfo;
import org.kie.kogito.taskassigning.service.TaskAssigningService;
import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TaskAssigningResourceTest {

    private static final String SERVICE_MESSAGE_VALUE = "SERVICE_MESSAGE_VALUE";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    TaskAssigningService service;

    private TaskAssigningResource resource;

    private TaskAssigningServiceContext context;

    @BeforeEach
    void setUp() {
        context = new TaskAssigningServiceContext();
        doReturn(context).when(service).getContext();
        resource = new TaskAssigningResource();
        resource.service = service;
    }

    @Test
    void getServiceStatus() throws Exception {
        context.setStatus(ServiceStatus.READY, ServiceMessage.info(SERVICE_MESSAGE_VALUE));
        String json = resource.getServiceStatus();
        ServiceStatusInfo result = OBJECT_MAPPER.readValue(json, ServiceStatusInfo.class);
        assertThat(result.getStatus()).isEqualTo(ServiceStatus.READY);
        assertThat(result.getStatusMessage().getValue()).isEqualTo(SERVICE_MESSAGE_VALUE);
        assertThat(result.getStatusMessage().getTime()).isEqualTo(context.getStatusInfo().getStatusMessage().getTime());
    }
}
