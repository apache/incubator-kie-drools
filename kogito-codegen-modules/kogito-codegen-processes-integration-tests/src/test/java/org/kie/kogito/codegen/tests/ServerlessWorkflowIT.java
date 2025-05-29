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
package org.kie.kogito.codegen.tests;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class ServerlessWorkflowIT extends AbstractCodegenIT {

    @ParameterizedTest
    @ValueSource(strings = { "serverless/single-operation.sw.json", "serverless/single-operation.sw.yml" })
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
    @ValueSource(strings = { "serverless/single-operation-with-delay.sw.json", "serverless/single-operation-with-delay.sw.yml" })
    public void testSingleFunctionCallWithDelayWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("End", 1);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);

        Process<? extends Model> p = app.get(Processes.class).processById("function");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        boolean completed = listener.waitTillCompleted(5000);
        assertThat(completed).isTrue();

        await().until(() -> p.instances().stream().count() == 0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "serverless/single-operation-many-functions.sw.json", "serverless/single-operation-many-functions.sw.yml" })
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
    @ValueSource(strings = { "serverless/single-operation-no-actions.sw.json", "serverless/single-operation-no-actions.sw.yml" })
    public void testNoActionOperationStateWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("noactions");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @ParameterizedTest
    @ValueSource(strings = { "serverless/multiple-operations.sw.json", "serverless/multiple-operations.sw.yml" })
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
    @ValueSource(strings = { "serverless/single-service-operation.sw.json", "serverless/single-service-operation.sw.yml" })
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
    @ValueSource(strings = { "serverless/single-inject-state.sw.json", "serverless/single-inject-state.sw.yml" })
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
    @ValueSource(strings = { "serverless/switch-state.sw.json", "serverless/switch-state.sw.yml" })
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
    @ValueSource(strings = { "serverless/switch-state-deny.sw.json", "serverless/switch-state-deny.sw.yml" })
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
    @ValueSource(strings = { "serverless/switch-state-end-condition.sw.json", "serverless/switch-state-end-condition.sw.yml" })
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

    @ParameterizedTest
    @ValueSource(strings = { "serverless/prchecker.sw.json", "serverless/prchecker.sw.yml" })
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
