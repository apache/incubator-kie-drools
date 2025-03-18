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
package org.kie.kogito.index.jpa.storage;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.test.TestUtils;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

public abstract class AbstractProcessInstanceStorageIT {
    private static final String PROCESS_ID = "travels";
    private static final String TRAVELER_NAME = "John";
    private static final String TRAVELER_LAST_NAME = "Doe";

    @Inject
    ProcessInstanceEntityStorage storage;

    @Test
    @Transactional
    public void testProcessInstanceStateEvent() {
        String processInstanceId = createNewProcessInstance();

        ProcessInstance processInstance = storage.get(processInstanceId);
        Assertions.assertThat(processInstance)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", processInstanceId)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("state", ProcessInstanceState.ACTIVE.ordinal())
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("rootProcessId", null)
                .hasFieldOrPropertyWithValue("variables", null);

        storage.indexState(TestUtils.createProcessInstanceEvent(processInstanceId, PROCESS_ID, null, null, ProcessInstanceState.COMPLETED.ordinal()));

        processInstance = storage.get(processInstanceId);
        Assertions.assertThat(processInstance)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", processInstanceId)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("state", ProcessInstanceState.COMPLETED.ordinal())
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("rootProcessId", null)
                .hasFieldOrPropertyWithValue("variables", null);
    }

    @Test
    @Transactional
    public void testProcessInstanceErrorEvent() {
        String processInstanceId = createNewProcessInstance();

        ProcessInstance processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getError())
                .isNull();

        String nodeDefinitionId = UUID.randomUUID().toString();
        storage.indexError(TestUtils.createProcessInstanceErrorDataEvent(processInstanceId, PROCESS_ID, TRAVELER_NAME, "This is really wrong", nodeDefinitionId, UUID.randomUUID().toString()));

        processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getError())
                .isNotNull()
                .hasFieldOrPropertyWithValue("nodeDefinitionId", nodeDefinitionId)
                .hasFieldOrPropertyWithValue("message", "This is really wrong");
    }

    @Test
    @Transactional
    public void testProcessInstanceNodeEvent() {
        String processInstanceId = createNewProcessInstance();

        ProcessInstance processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getNodes())
                .isEmpty();

        String nodeDefinitionId = UUID.randomUUID().toString();
        String nodeInstanceId = UUID.randomUUID().toString();

        storage.indexNode(TestUtils.createProcessInstanceNodeDataEvent(processInstanceId, PROCESS_ID, nodeDefinitionId, nodeInstanceId, "nodeName", "BoundaryEventNode",
                ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER));

        processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getNodes())
                .hasSize(1);

        Assertions.assertThat(processInstance.getNodes().get(0))
                .hasNoNullFieldsOrPropertiesExcept("exit", "slaDueDate", "errorMessage", "retrigger")
                .hasFieldOrPropertyWithValue("name", "nodeName")
                .hasFieldOrPropertyWithValue("type", "BoundaryEventNode")
                .hasFieldOrPropertyWithValue("definitionId", nodeDefinitionId)
                .hasFieldOrPropertyWithValue("nodeId", nodeDefinitionId)
                .hasFieldOrPropertyWithValue("id", nodeInstanceId);

        storage.indexNode(TestUtils.createProcessInstanceNodeDataEvent(processInstanceId, PROCESS_ID, nodeDefinitionId, nodeInstanceId, "nodeName", "BoundaryEventNode",
                ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT));

        processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getNodes())
                .hasSize(1);

        Assertions.assertThat(processInstance.getNodes().get(0))
                .hasNoNullFieldsOrPropertiesExcept("slaDueDate", "errorMessage", "retrigger")
                .hasFieldOrPropertyWithValue("name", "nodeName")
                .hasFieldOrPropertyWithValue("type", "BoundaryEventNode")
                .hasFieldOrPropertyWithValue("definitionId", nodeDefinitionId)
                .hasFieldOrPropertyWithValue("nodeId", nodeDefinitionId)
                .hasFieldOrPropertyWithValue("id", nodeInstanceId);
    }

    @Test
    @Transactional
    public void testProcessInstanceVariableEvent() {
        String processInstanceId = createNewProcessInstance();

        ProcessInstance processInstance = storage.get(processInstanceId);

        Assertions.assertThat(processInstance.getVariables())
                .isNull();

        storage.indexVariable(TestUtils.createProcessInstanceVariableEvent(processInstanceId, PROCESS_ID, TRAVELER_NAME, TRAVELER_LAST_NAME));

        processInstance = storage.get(processInstanceId);

        Assertions.assertThatObject(processInstance.getVariables())
                .isNotNull()
                .extracting(jsonNodes -> jsonNodes.at("/traveller/firstName").asText(), jsonNodes -> jsonNodes.at("/traveller/lastName").asText())
                .contains(TRAVELER_NAME, TRAVELER_LAST_NAME);
    }

    private String createNewProcessInstance() {
        String processInstanceId = UUID.randomUUID().toString();

        Assertions.assertThat(storage.get(processInstanceId))
                .isNull();

        storage.indexState(TestUtils.createProcessInstanceEvent(processInstanceId, PROCESS_ID, null, null, ProcessInstanceState.ACTIVE.ordinal()));

        ProcessInstance processInstance = storage.get(processInstanceId);
        Assertions.assertThat(processInstance)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", processInstanceId)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("state", ProcessInstanceState.ACTIVE.ordinal())
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("rootProcessId", null)
                .hasFieldOrPropertyWithValue("variables", null);

        return processInstanceId;
    }

}
