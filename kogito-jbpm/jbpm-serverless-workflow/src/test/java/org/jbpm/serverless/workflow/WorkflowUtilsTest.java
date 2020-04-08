/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.serverless.workflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jbpm.serverless.workflow.api.choices.DefaultChoice;
import org.jbpm.serverless.workflow.api.events.EventDefinition;
import org.jbpm.serverless.workflow.api.interfaces.State;
import org.jbpm.serverless.workflow.api.mapper.BaseObjectMapper;
import org.jbpm.serverless.workflow.api.mapper.JsonObjectMapper;
import org.jbpm.serverless.workflow.api.mapper.YamlObjectMapper;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.RelayState;
import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class WorkflowUtilsTest extends BaseServerlessTest {

    @Test
    public void testGetObjectMapper() {
        BaseObjectMapper objectMapper = ServerlessWorkflowUtils.getObjectMapper("json");
        assertNotNull(objectMapper);
        assertThat(objectMapper).isInstanceOf(JsonObjectMapper.class);

        objectMapper = ServerlessWorkflowUtils.getObjectMapper("yml");
        assertNotNull(objectMapper);
        assertThat(objectMapper).isInstanceOf(YamlObjectMapper.class);

        assertThrows(IllegalArgumentException.class, () -> {
            ServerlessWorkflowUtils.getObjectMapper("unsupported");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ServerlessWorkflowUtils.getObjectMapper(null);
        });

    }

    @Test
    public void testGetWorkflowStartState() {
        assertThat(ServerlessWorkflowUtils.getWorkflowStartState(singleRelayStateWorkflow)).isNotNull();
        assertThat(ServerlessWorkflowUtils.getWorkflowStartState(singleRelayStateWorkflow)).isInstanceOf(RelayState.class);

    }

    @Test
    public void testGetWorkflowEndStatesSingle() {
        List<State> endStates = ServerlessWorkflowUtils.getWorkflowEndStates(singleRelayStateWorkflow);
        assertThat(endStates).isNotNull();
        assertThat(endStates).hasSize(1);
        State endState = endStates.get(0);
        assertThat(endState).isNotNull();
        assertThat(endState).isInstanceOf(RelayState.class);
    }

    @Test
    public void testGetWorkflowEndStatesMulti() {
        List<State> endStates = ServerlessWorkflowUtils.getWorkflowEndStates(multiRelayStateWorkflow);
        assertThat(endStates).isNotNull();
        assertThat(endStates).hasSize(2);
        State endState1 = endStates.get(0);
        assertThat(endState1).isNotNull();
        assertThat(endState1).isInstanceOf(RelayState.class);
        State endState2 = endStates.get(1);
        assertThat(endState2).isNotNull();
        assertThat(endState2).isInstanceOf(RelayState.class);
    }

    @Test
    public void testGetStatesByType() {
        List<State> relayStates = ServerlessWorkflowUtils.getStatesByType(multiRelayStateWorkflow, DefaultState.Type.RELAY);
        assertThat(relayStates).isNotNull();
        assertThat(relayStates).hasSize(2);
        assertThat(relayStates.get(0)).isInstanceOf(RelayState.class);
        assertThat(relayStates.get(1)).isInstanceOf(RelayState.class);

        List<State> noOperationStates = ServerlessWorkflowUtils.getStatesByType(multiRelayStateWorkflow, DefaultState.Type.OPERATION);
        assertThat(noOperationStates).isNotNull();
        assertThat(noOperationStates).hasSize(0);
    }

    @Test
    public void testIncludesSupportedStates() {
        assertThat(ServerlessWorkflowUtils.includesSupportedStates(singleRelayStateWorkflow)).isTrue();
    }

    @Test
    public void testGetWorkflowEventFor() {
        assertThat(ServerlessWorkflowUtils.getWorkflowEventFor(eventDefOnlyWorkflow, "sampleEvent")).isNotNull();
        assertThat(ServerlessWorkflowUtils.getWorkflowEventFor(eventDefOnlyWorkflow, "sampleEvent")).isInstanceOf(EventDefinition.class);
    }

    @Test
    public void testSysOutFunctionScript() {
        String script = "$.a $.b";
        assertThat(ServerlessWorkflowUtils.sysOutFunctionScript(script)).isNotNull();
    }

    @Test
    public void testGetJsonPathScript() {
        String script = "$.a $.b";
        assertThat(ServerlessWorkflowUtils.getJsonPathScript(script)).isNotNull();
    }

    @Test
    public void testGetInjectScript() throws Exception {
        String toInject = "{\n" +
                "  \"name\": \"john\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode toInjectObj = mapper.readTree(toInject);

        assertThat(ServerlessWorkflowUtils.getInjectScript(toInjectObj)).isNotNull();
    }

    @Test
    public void testConditionScript() throws Exception {
        assertThat(ServerlessWorkflowUtils.conditionScript("$.name", DefaultChoice.Operator.EQUALS, "john")).isNotNull();
        assertThat(ServerlessWorkflowUtils.conditionScript("$.name", DefaultChoice.Operator.EQUALS, "john"))
                .isEqualTo("return workflowdata.get(\"name\").textValue().equals(\"john\");");
    }

}
