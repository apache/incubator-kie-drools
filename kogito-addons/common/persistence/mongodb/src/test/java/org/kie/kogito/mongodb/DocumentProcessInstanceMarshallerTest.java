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
package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import org.bson.Document;
import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.serialization.process.MarshallerContextName;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentProcessInstanceMarshallerTest {

    ProcessInstanceMarshallerService marshaller =
            ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withContextEntries(singletonMap(MarshallerContextName.MARSHALLER_FORMAT, "json")).build();

    static BpmnProcess process;
    static Document doc;

    @BeforeAll
    static void setup() throws URISyntaxException, IOException {
        process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.configure();

    }

    @Test
    void testMarshalProcessInstance() {
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        processInstance.start();
        doc = Document.parse(new String(marshaller.marshallProcessInstance(processInstance)));
        assertNotNull(doc, "Marshalled value should not be null");
        assertThat(doc.get("id")).isEqualTo(processInstance.id());
        assertThat(doc.get("description")).isEqualTo(processInstance.description());
        assertThat(doc.get("context", Document.class).getList("variable", Document.class).size()).isEqualTo(1);
        assertThat(doc.get("context", Document.class).getList("variable", Document.class).get(0).get("name")).isEqualTo("test");
        assertThat(doc.get("context", Document.class).getList("variable", Document.class).get(0).get("value", Document.class).get("value")).isEqualTo("testValue");

    }

    @Test
    void testUnmarshalProcessInstance() {
        ProcessInstance<BpmnVariables> processInstance = (ProcessInstance<BpmnVariables>) marshaller.unmarshallProcessInstance(doc.toJson().getBytes(), process);
        assertNotNull(processInstance, "Unmarshalled value should not be null");
        assertThat(processInstance.id()).isEqualTo(doc.get("id"));
        assertThat(processInstance.description()).isEqualTo(doc.get("description"));
        assertThat(processInstance.description()).isEqualTo("User Task");
        Collection<? extends ProcessInstance<BpmnVariables>> values = process.instances().values();
        assertThat(values).isNotEmpty();
        BpmnVariables variables = processInstance.variables();
        String testVar = (String) variables.get("test");
        assertThat(testVar).isEqualTo("testValue");
    }

    @Test
    void testProcessInstanceReadOnly() {
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        processInstance.start();
        doc = Document.parse(new String(marshaller.marshallProcessInstance(processInstance)));
        ProcessInstance<BpmnVariables> processInstanceReadOnly = (ProcessInstance<BpmnVariables>) marshaller.unmarshallProcessInstance(doc.toJson().getBytes(), process);
        assertNotNull(processInstanceReadOnly, "Unmarshalled value should not be null");
        ProcessInstance<BpmnVariables> pi = (ProcessInstance<BpmnVariables>) marshaller.unmarshallReadOnlyProcessInstance(doc.toJson().getBytes(), process);
        assertNotNull(pi, "Unmarshalled value should not be null");
    }

    @Test
    void testDocumentMarshallingException() {
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> marshaller.marshallProcessInstance(processInstance));
    }

}
