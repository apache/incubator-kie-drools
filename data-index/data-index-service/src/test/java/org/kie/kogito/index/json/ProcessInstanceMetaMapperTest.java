/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.json;

import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.model.ProcessInstanceState;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;

public class ProcessInstanceMetaMapperTest {

    @Test
    public void testProcessInstanceMapper() {
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        KogitoProcessCloudEvent event = getProcessCloudEvent(processId, processInstanceId, ProcessInstanceState.COMPLETED, rootProcessInstanceId, rootProcessId, rootProcessInstanceId);
        ObjectNode json = new ProcessInstanceMetaMapper().apply(event);
        assertThat(json).isNotNull();
        assertThatJson(json.toString()).and(
                a -> a.node("id").isEqualTo(rootProcessInstanceId),
                a -> a.node("processId").isEqualTo(rootProcessId),
                a -> a.node("traveller.firstName").isEqualTo("Maciej"),
                a -> a.node("hotel.name").isEqualTo("Meriton"),
                a -> a.node("flight.flightNumber").isEqualTo("MX555"),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE).isArray().hasSize(1),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].id").isEqualTo(processInstanceId),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].processId").isEqualTo(processId),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].rootProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].parentProcessInstanceId").isEqualTo(rootProcessInstanceId),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].rootProcessId").isEqualTo(rootProcessId),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].state").isEqualTo(ProcessInstanceState.COMPLETED.ordinal()),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].endpoint").isEqualTo(event.getSource().toString()),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].start").isEqualTo(event.getData().getStart().toInstant().toEpochMilli()),
                a -> a.node(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE + "[0].end").isEqualTo(event.getData().getEnd().toInstant().toEpochMilli())
        );
    }
}
