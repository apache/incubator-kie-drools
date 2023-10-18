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
package org.kie.kogito.index.service.json;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.index.model.ProcessInstanceState;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.test.TestUtils.getProcessCloudEvent;

public class ProcessInstanceMetaMapperTest {

    @Test
    public void testProcessInstanceMapper() {
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        String piPrefix = KOGITO_DOMAIN_ATTRIBUTE + "." + PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;

        ProcessInstanceStateDataEvent event =
                getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, rootProcessInstanceId, rootProcessId, rootProcessInstanceId, "currentUser");

        ObjectNode json = new ProcessInstanceMetaMapper().apply(event);
        assertThat(json).isNotNull();
        assertThatJson(json.toString()).and(
                a -> a.node("id").isEqualTo(rootProcessInstanceId),
                a -> a.node("processId").isEqualTo(rootProcessId),
                a -> a.node(KOGITO_DOMAIN_ATTRIBUTE).isNotNull(),
                a -> a.node(KOGITO_DOMAIN_ATTRIBUTE + ".lastUpdate").isEqualTo(event.getTime().toInstant().toEpochMilli()),
                a -> a.node(piPrefix).isArray().hasSize(1),
                a -> a.node(piPrefix + "[0].id").isEqualTo(processInstanceId),
                a -> a.node(piPrefix + "[0].processId").isEqualTo(processId),
                a -> a.node(piPrefix + "[0].rootProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(piPrefix + "[0].parentProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(piPrefix + "[0].rootProcessId").isEqualTo(rootProcessId),
                a -> a.node(piPrefix + "[0].state").isEqualTo(ProcessInstanceState.ACTIVE.ordinal()),
                a -> a.node(piPrefix + "[0].endpoint").isEqualTo(event.getSource().toString()),
                a -> a.node(piPrefix + "[0].updatedBy").isEqualTo(event.getData().getEventUser().toString()),
                a -> a.node(piPrefix + "[0].start").isEqualTo(event.getData().getEventDate().toInstant().toEpochMilli()),
                a -> a.node(piPrefix + "[0].end").isAbsent(),
                a -> a.node(piPrefix + "[0].lastUpdate").isEqualTo(event.getTime().toInstant().toEpochMilli()));
    }

    @Test
    public void testProcessInstanceMapperWithBusinessKey() {
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        String piPrefix = KOGITO_DOMAIN_ATTRIBUTE + "." + PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;

        ProcessInstanceStateDataEvent event =
                getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.ACTIVE, rootProcessInstanceId, rootProcessId, rootProcessInstanceId, "currentUser");

        event.getData().update().businessKey("custom-key");
        ObjectNode json = new ProcessInstanceMetaMapper().apply(event);
        assertThat(json).isNotNull();
        assertThatJson(json.toString()).and(
                a -> a.node("id").isEqualTo(rootProcessInstanceId),
                a -> a.node("processId").isEqualTo(rootProcessId),
                a -> a.node(KOGITO_DOMAIN_ATTRIBUTE).isNotNull(),
                a -> a.node(KOGITO_DOMAIN_ATTRIBUTE + ".lastUpdate").isEqualTo(event.getTime().toInstant().toEpochMilli()),
                a -> a.node(piPrefix).isArray().hasSize(1),
                a -> a.node(piPrefix + "[0].id").isEqualTo(processInstanceId),
                a -> a.node(piPrefix + "[0].processId").isEqualTo(processId),
                a -> a.node(piPrefix + "[0].rootProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(piPrefix + "[0].parentProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(piPrefix + "[0].rootProcessId").isEqualTo(rootProcessId),
                a -> a.node(piPrefix + "[0].state").isEqualTo(ProcessInstanceState.ACTIVE.ordinal()),
                a -> a.node(piPrefix + "[0].endpoint").isEqualTo(event.getSource().toString()),
                a -> a.node(piPrefix + "[0].start").isEqualTo(event.getData().getEventDate().toInstant().toEpochMilli()),
                a -> a.node(piPrefix + "[0].end").isAbsent(),
                a -> a.node(piPrefix + "[0].lastUpdate").isEqualTo(event.getTime().toInstant().toEpochMilli()),
                a -> a.node(piPrefix + "[0].businessKey").isEqualTo(event.getData().getBusinessKey()),
                a -> a.node(piPrefix + "[0].updatedBy").isEqualTo(event.getData().getEventUser().toString()));
    }
}
