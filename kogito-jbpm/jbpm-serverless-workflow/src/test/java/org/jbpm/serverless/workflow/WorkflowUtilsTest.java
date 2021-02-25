/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.serverless.workflow;

import java.util.HashMap;
import java.util.List;

import org.jbpm.serverless.workflow.parser.util.ServerlessWorkflowUtils;
import org.jbpm.serverless.workflow.parser.util.WorkflowAppContext;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.events.EventDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.interfaces.State;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;
import io.serverlessworkflow.api.states.DefaultState;
import io.serverlessworkflow.api.states.InjectState;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WorkflowUtilsTest extends BaseServerlessTest {

    @Test
    public void testGetObjectMapper() {
        BaseObjectMapper objectMapper = ServerlessWorkflowUtils.getObjectMapper("json");
        assertNotNull(objectMapper);
        assertThat(objectMapper).isInstanceOf(JsonObjectMapper.class);

        objectMapper = ServerlessWorkflowUtils.getObjectMapper("yml");
        assertNotNull(objectMapper);
        assertThat(objectMapper).isInstanceOf(YamlObjectMapper.class);

        assertThrows(IllegalArgumentException.class, () -> ServerlessWorkflowUtils.getObjectMapper("unsupported"));

        assertThrows(IllegalArgumentException.class, () -> ServerlessWorkflowUtils.getObjectMapper(null));
    }

    @Test
    public void testGetWorkflowStartState() {
        assertThat(ServerlessWorkflowUtils.getWorkflowStartState(singleInjectStateWorkflow)).isNotNull();
        assertThat(ServerlessWorkflowUtils.getWorkflowStartState(singleInjectStateWorkflow)).isInstanceOf(InjectState.class);
    }

    @Test
    public void testGetWorkflowEndStatesSingle() {
        List<State> endStates = ServerlessWorkflowUtils.getWorkflowEndStates(singleInjectStateWorkflow);
        assertThat(endStates).isNotNull();
        assertThat(endStates).hasSize(1);
        State endState = endStates.get(0);
        assertThat(endState).isNotNull();
        assertThat(endState).isInstanceOf(InjectState.class);
    }

    @Test
    public void testGetWorkflowEndStatesMulti() {
        List<State> endStates = ServerlessWorkflowUtils.getWorkflowEndStates(multiInjectStateWorkflow);
        assertThat(endStates).isNotNull();
        assertThat(endStates).hasSize(2);
        State endState1 = endStates.get(0);
        assertThat(endState1).isNotNull();
        assertThat(endState1).isInstanceOf(InjectState.class);
        State endState2 = endStates.get(1);
        assertThat(endState2).isNotNull();
        assertThat(endState2).isInstanceOf(InjectState.class);
    }

    @Test
    public void testGetStatesByType() {
        List<State> relayStates = ServerlessWorkflowUtils.getStatesByType(multiInjectStateWorkflow, DefaultState.Type.INJECT);
        assertThat(relayStates).isNotNull();
        assertThat(relayStates).hasSize(2);
        assertThat(relayStates.get(0)).isInstanceOf(InjectState.class);
        assertThat(relayStates.get(1)).isInstanceOf(InjectState.class);

        List<State> noOperationStates = ServerlessWorkflowUtils.getStatesByType(multiInjectStateWorkflow, DefaultState.Type.OPERATION);
        assertThat(noOperationStates).isNotNull();
        assertThat(noOperationStates).hasSize(0);
    }

    @Test
    public void testIncludesSupportedStates() {
        assertThat(ServerlessWorkflowUtils.includesSupportedStates(singleInjectStateWorkflow)).isTrue();
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
    public void testDataConditionScript() {
        assertThat(ServerlessWorkflowUtils.conditionScript("$.customers[?(@.age  > 18)]")).isNotNull();
        assertThat(ServerlessWorkflowUtils.conditionScript("$.customers[?(@.age  > 18)]"))
                .isEqualTo(
                        "return !((java.util.List<java.lang.String>) com.jayway.jsonpath.JsonPath.parse(((com.fasterxml.jackson.databind.JsonNode)kcontext.getVariable(\"workflowdata\")).toString()).read(\"$.customers[?(@.age  > 18)]\")).isEmpty();");
    }

    @Test
    public void testResolveFunctionMetadata() {
        FunctionDefinition function = new FunctionDefinition().withName("testfunction1").withMetadata(
                new HashMap<String, String>() {
                    {
                        put("testprop1", "customtestprop1val");
                    }
                });

        String testProp1Val = ServerlessWorkflowUtils.resolveFunctionMetadata(function, "testprop1",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp1Val).isNotNull();
        assertThat(testProp1Val).isEqualTo("customtestprop1val");

        String testProp2Val = ServerlessWorkflowUtils.resolveFunctionMetadata(function, "testprop2",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp2Val).isNotNull();
        assertThat(testProp2Val).isEqualTo("testprop2val");
    }

    @Test
    public void testResolveEvenDefinitiontMetadata() {
        EventDefinition eventDefinition = new EventDefinition().withName("testevent1").withMetadata(
                new HashMap<String, String>() {
                    {
                        put("testprop1", "customtestprop1val");
                    }
                });

        String testProp1Val = ServerlessWorkflowUtils.resolveEvenDefinitiontMetadata(eventDefinition, "testprop1",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp1Val).isNotNull();
        assertThat(testProp1Val).isEqualTo("customtestprop1val");

        String testProp2Val = ServerlessWorkflowUtils.resolveEvenDefinitiontMetadata(eventDefinition, "testprop2",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp2Val).isNotNull();
        assertThat(testProp2Val).isEqualTo("testprop2val");
    }

    @Test
    public void testResolveStatetMetadata() {
        DefaultState defaultState = new DefaultState().withName("teststate1").withMetadata(
                new HashMap<String, String>() {
                    {
                        put("testprop1", "customtestprop1val");
                    }
                });

        String testProp1Val = ServerlessWorkflowUtils.resolveStatetMetadata(defaultState, "testprop1",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp1Val).isNotNull();
        assertThat(testProp1Val).isEqualTo("customtestprop1val");

        String testProp2Val = ServerlessWorkflowUtils.resolveStatetMetadata(defaultState, "testprop2",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp2Val).isNotNull();
        assertThat(testProp2Val).isEqualTo("testprop2val");
    }

    @Test
    public void testResolveWorkflowMetadata() {
        Workflow workflow = new Workflow().withId("workflowid1").withMetadata(
                new HashMap<String, String>() {
                    {
                        put("testprop1", "customtestprop1val");
                    }
                });

        String testProp1Val = ServerlessWorkflowUtils.resolveWorkflowMetadata(workflow, "testprop1",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp1Val).isNotNull();
        assertThat(testProp1Val).isEqualTo("customtestprop1val");

        String testProp2Val = ServerlessWorkflowUtils.resolveWorkflowMetadata(workflow, "testprop2",
                WorkflowAppContext.ofAppResources());
        assertThat(testProp2Val).isNotNull();
        assertThat(testProp2Val).isEqualTo("testprop2val");
    }
}
