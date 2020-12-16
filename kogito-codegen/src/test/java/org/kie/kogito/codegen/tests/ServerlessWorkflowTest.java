/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessWorkItemTransitionEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.services.identity.StaticIdentityProvider;

public class ServerlessWorkflowTest extends AbstractCodegenTest {

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-operation.sw.json", "serverless/single-operation.sw.yml"})
    public void testSingleFunctionCallWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("function");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-operation-with-delay.sw.json", "serverless/single-operation-with-delay.sw.yml"})
    public void testSingleFunctionCallWithDelayWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("SmallDelay", 1);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);


        Process<? extends Model> p = app.get(Processes.class).processById("function");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        boolean completed = listener.waitTillCompleted(5000);
        assertThat(completed).isTrue();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-operation-many-functions.sw.json", "serverless/single-operation-many-functions.sw.yml"})
    public void testMultipleFunctionsCallWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("function");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/multiple-operations.sw.json", "serverless/multiple-operations.sw.yml"})
    public void testMultipleOperationsWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("function");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-service-operation.sw.json", "serverless/single-service-operation.sw.yml"})
    public void testBasicServiceWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("singleservice");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{\n" +
                "  \"name\": \"john\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("result").textValue()).isEqualTo("Hello john");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-inject-state.sw.json", "serverless/single-inject-state.sw.yml"})
    public void testSingleInjectWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("singleinject");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("name").textValue()).isEqualTo("john");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/switch-state.sw.json", "serverless/switch-state.sw.yml"})
    public void testApproveSwitchStateWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("switchworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("decision").textValue()).isEqualTo("Approved");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/switch-state-deny.sw.json", "serverless/switch-state-deny.sw.yml"})
    public void testDenySwitchStateWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("switchworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("decision").textValue()).isEqualTo("Denied");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/switch-state-end-condition.sw.json", "serverless/switch-state-end-condition.sw.yml"})
    public void testSwitchStateWithEndConditionWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("switchworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSubFlowWorkflow() throws Exception {

        Application app = generateCodeProcessesOnly("serverless/single-subflow.sw.json", "serverless/called-subflow.sw.json");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("singlesubflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("parentData").textValue()).isEqualTo("parentTestData");
        assertThat(dataOut.get("childData").textValue()).isEqualTo("childTestData");

    }

    @Test
    public void testParallelExecWorkflow() throws Exception {
        try {
            Application app = generateCodeProcessesOnly("serverless/parallel-state.sw.json", "serverless/parallel-state-branch1.sw.json", "serverless/parallel-state-branch2.sw.json");
            assertThat(app).isNotNull();

            Process<? extends Model> p = app.get(Processes.class).processById("parallelworkflow");

            Model m = p.createModel();
            Map<String, Object> parameters = new HashMap<>();

            String jsonParamStr = "{}";

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


            parameters.put("workflowdata", jsonParamObj);
            m.fromMap(parameters);

            ProcessInstance<?> processInstance = p.createInstance(m);
            processInstance.start();

            assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

            Model result = (Model) processInstance.variables();
            assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

            assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

            JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

            assertThat(dataOut.get("branch1data").textValue()).isEqualTo("testBranch1Data");
            assertThat(dataOut.get("branch2data").textValue()).isEqualTo("testBranch2Data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/decision-workflow.sw.json", "serverless/decision-workflow.sw.yml"})
    public void testSingleDecisionWorkflow(String processLocation) throws Exception {

        Policy<?> securityPolicy = SecurityPolicy.of(new StaticIdentityProvider("workflow"));

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();
        final List<String> workItemTransitionEvents = new ArrayList<>();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("BEFORE:: " + event);
            }

            @Override
            public void afterWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("AFTER:: " + event);
            }
        });

        Process<? extends Model> p = app.get(Processes.class).processById("decisionworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        parameters.put("approvaldecision", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();



        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<WorkItem> workItems = processInstance.workItems(securityPolicy);
        assertEquals(1, workItems.size());
        assertEquals("approval", workItems.get(0).getName());

        //String decisionParamStr = "{\"result\": \"approved\"}";
        String decisionParamStr = "{\n" +
                "  \"decisions\" : [ {\n" +
                "    \"result\" : \"approved\"\n" +
                "  } ]\n" +
                "}\n";

        JsonNode decisionParamObj = mapper.readTree(decisionParamStr);

        Map<String, Object> completionMap = new HashMap<>();
        completionMap.put("decision", decisionParamObj);

        processInstance.completeWorkItem(workItems.get(0).getId(), completionMap, securityPolicy);

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        assertThat(workItemTransitionEvents).hasSize(4);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("workflowdata", "approvaldecision");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);
        assertThat(result.toMap().get("approvaldecision")).isInstanceOf(JsonNode.class);

        JsonNode workflowdataOut = (JsonNode) result.toMap().get("workflowdata");
        JsonNode approvalDecisionOut = (JsonNode) result.toMap().get("approvaldecision");

        assertThat(workflowdataOut.get("decision").textValue()).isEqualTo("Approved");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/multi-decision-workflow.sw.json", "serverless/multi-decision-workflow.sw.yml"})
    public void testMultiDecisionWorkflow(String processLocation) throws Exception {

        Policy<?> securityPolicy = SecurityPolicy.of(new StaticIdentityProvider("workflow"));

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();
        final List<String> workItemTransitionEvents = new ArrayList<>();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("BEFORE:: " + event);
            }

            @Override
            public void afterWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("AFTER:: " + event);
            }
        });

        Process<? extends Model> p = app.get(Processes.class).processById("multidecisionworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        parameters.put("approvaldecision", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<WorkItem> workItems = processInstance.workItems(securityPolicy);
        assertEquals(1, workItems.size());
        assertEquals("firstfunction", workItems.get(0).getName());

        String decisionParamStr = "{\"result\": \"approved\"}";
        JsonNode decisionParamObj = mapper.readTree(decisionParamStr);

        Map<String, Object> decisionMap = new HashMap<>();
        decisionMap.put("decision", decisionParamObj);

        processInstance.completeWorkItem(workItems.get(0).getId(), decisionMap, securityPolicy);

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);


        workItems = processInstance.workItems(securityPolicy);
        assertEquals(1, workItems.size());
        assertEquals("secondfunction", workItems.get(0).getName());

        processInstance.completeWorkItem(workItems.get(0).getId(), decisionMap, securityPolicy);

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        assertThat(workItemTransitionEvents).hasSize(8);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(3).containsKeys("workflowdata", "firstfunctiondecision", "secondfunctiondecision");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);
        assertThat(result.toMap().get("firstfunctiondecision")).isInstanceOf(JsonNode.class);
        assertThat(result.toMap().get("secondfunctiondecision")).isInstanceOf(JsonNode.class);

        JsonNode firstDecisionOut = (JsonNode) result.toMap().get("firstfunctiondecision");
        JsonNode secondDecisionOut = (JsonNode) result.toMap().get("secondfunctiondecision");
        assertThat(firstDecisionOut.get("result").textValue()).isEqualTo("approved");
        assertThat(secondDecisionOut.get("result").textValue()).isEqualTo("approved");
    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/rule-workflow.sw.json", "serverless/rule-workflow.sw.yml"})
    public void testRuleWorkflow(String processLocation) throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList(processLocation));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("serverless/workflowrule.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("ruleworkflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{ \"person\": { \"age\": \"21\" } }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");

        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode workflowdataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(workflowdataOut.get("person").get("age").textValue()).isEqualTo("21");
        assertThat(workflowdataOut.get("person").get("adult").textValue()).isEqualTo("true");

    }

    @ParameterizedTest
    @ValueSource(strings = {"serverless/prchecker.sw.json", "serverless/prchecker.sw.yml"})
    public void testPrCheckerWorkflow(String processLocation) throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("prchecker");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj = mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        System.clearProperty("jbpm.enable.multi.con");
    }
}