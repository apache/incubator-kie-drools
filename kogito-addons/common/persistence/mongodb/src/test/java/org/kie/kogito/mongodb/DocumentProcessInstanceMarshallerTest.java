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
package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.bson.Document;
import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;

class DocumentProcessInstanceMarshallerTest {

    ProcessInstanceMarshallerService marshaller =
            ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies()
                    .withContextEntries(singletonMap(MarshallerContextName.MARSHALLER_FORMAT, MarshallerContextName.MARSHALLER_FORMAT_JSON)).build();

    private BpmnProcess newProcess() throws URISyntaxException, IOException {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application = StaticApplicationAssembler.instance().newStaticApplication(null, processConfig, "BPMN2-UserTask.bpmn2");

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> compiledProcess = container.processById(processId);

        abort(compiledProcess.instances());
        return (BpmnProcess) compiledProcess;
    }

    @Test
    void testMarshalProcessInstance() throws Exception {
        BpmnProcess process = newProcess();
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(singletonMap("test", "testValue")));
        Document doc = Document.parse(new String(marshaller.marshallProcessInstance(processInstance)));
        assertThat(doc).as("Marshalled value should not be null").isNotNull()
                .containsEntry("id", processInstance.id())
                .containsEntry("description", processInstance.description());
        assertThat(doc.get("context", Document.class).getList("variable", Document.class)).hasSize(1);
        assertThat(doc.get("context", Document.class).getList("variable", Document.class).get(0)).containsEntry("name", "test");
        assertThat(doc.get("context", Document.class).getList("variable", Document.class).get(0).get("value", Document.class)).containsEntry("value", "testValue");
    }

    @Test
    void testUnmarshalProcessInstance() throws Exception {
        BpmnProcess process = newProcess();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        byte[] data = marshaller.marshallProcessInstance(processInstance);
        Document doc = Document.parse(new String(data));
        assertThat(processInstance).as("Unmarshalled value should not be null").isNotNull();
        assertThat(processInstance.id()).isEqualTo(doc.get("id"));
        assertThat(processInstance.description()).isEqualTo(doc.get("description")).isEqualTo("User Task");
        BpmnVariables variables = processInstance.variables();
        String testVar = (String) variables.get("test");
        assertThat(testVar).isEqualTo("testValue");
    }

    @Test
    void testProcessInstanceReadOnly() throws Exception {
        BpmnProcess process = newProcess();
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        Document doc = Document.parse(new String(marshaller.marshallProcessInstance(processInstance)));
        ProcessInstance<BpmnVariables> processInstanceReadOnly = (ProcessInstance<BpmnVariables>) marshaller.unmarshallProcessInstance(doc.toJson().getBytes(), process);
        assertThat(processInstanceReadOnly).as("Unmarshalled value should not be null").isNotNull();
        ProcessInstance<BpmnVariables> pi = (ProcessInstance<BpmnVariables>) marshaller.unmarshallReadOnlyProcessInstance(doc.toJson().getBytes(), process);
        assertThat(pi).as("Unmarshalled value should not be null").isNotNull();
    }

}
