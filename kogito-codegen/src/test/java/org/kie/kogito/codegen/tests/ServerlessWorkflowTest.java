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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;

public class ServerlessWorkflowTest extends AbstractCodegenTest {

    @ParameterizedTest
    @ValueSource(strings = {"serverless/single-operation.sw.json", "serverless/single-operation.sw.yml"})
    public void testSingleFunctionCallWorkflow(String processLocation) throws Exception {

        Application app = generateCodeProcessesOnly(processLocation);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("function");

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
        ((DefaultProcessEventListenerConfig)app.config().process().processEventListeners()).register(listener);


        Process<? extends Model> p = app.processes().processById("function");

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

        Process<? extends Model> p = app.processes().processById("function");

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

        Process<? extends Model> p = app.processes().processById("function");

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

        Process<? extends Model> p = app.processes().processById("singleservice");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();

        String jsonParamStr = "{\n" +
                "  \"name\": \"john\"\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonParamObj =  mapper.readTree(jsonParamStr);


        parameters.put("workflowdata", jsonParamObj);
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("workflowdata");
        
        assertThat(result.toMap().get("workflowdata")).isInstanceOf(JsonNode.class);

        JsonNode dataOut = (JsonNode) result.toMap().get("workflowdata");

        assertThat(dataOut.get("result").textValue()).isEqualTo("Hello john");
    }

    @Test
    public void testSubFlowWorkflow() throws Exception {

        Application app = generateCodeProcessesOnly("serverless/single-subflow.sw.json", "serverless/called-subflow.sw.json");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("singlesubflow");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}