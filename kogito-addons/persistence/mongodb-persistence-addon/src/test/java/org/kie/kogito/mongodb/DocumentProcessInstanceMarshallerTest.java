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

package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import org.bson.Document;
import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.marshalling.DocumentMarshallingException;
import org.kie.kogito.mongodb.marshalling.DocumentMarshallingStrategy;
import org.kie.kogito.mongodb.marshalling.DocumentProcessInstanceMarshaller;
import org.kie.kogito.mongodb.marshalling.DocumentUnmarshallingException;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentProcessInstanceMarshallerTest {

    DocumentProcessInstanceMarshaller marshaller = new DocumentProcessInstanceMarshaller(new DocumentMarshallingStrategy());

    static BpmnProcess process;
    static ProcessInstanceDocument doc;

    @BeforeAll
    static void setup() throws URISyntaxException, IOException {
        process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.configure();

    }

    @Test
    void testMarshalProcessInstance() {
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        processInstance.start();
        doc = marshaller.marshalProcessInstance(processInstance);
        assertNotNull(doc, "Marshalled value should not be null");
        assertThat(doc.getId()).isEqualTo(processInstance.id());
        assertThat(doc.getProcessInstance().get("description")).isEqualTo(processInstance.description());
        assertThat(doc.getProcessInstance().getList("variable", Document.class).size()).isEqualTo(1);
        assertThat(doc.getProcessInstance().getList("variable", Document.class).get(0).get("name")).isEqualTo("test");
        assertThat(doc.getProcessInstance().getList("variable", Document.class).get(0).get("value")).isEqualTo("testValue");

    }

    @Test
    void testUnmarshalProcessInstance() {
        ProcessInstance<BpmnVariables> processInstance = marshaller.unmarshallProcessInstance(doc, process);
        assertNotNull(processInstance, "Unmarshalled value should not be null");
        assertThat(processInstance.id()).isEqualTo(doc.getProcessInstance().get("id"));
        assertThat(processInstance.description()).isEqualTo(doc.getProcessInstance().get("description"));
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
        doc = marshaller.marshalProcessInstance(processInstance);
        ProcessInstance<BpmnVariables> processInstanceReadOnly = process.createReadOnlyInstance(marshaller.unmarshallWorkflowProcessInstance(doc, process));
        assertNotNull(processInstanceReadOnly, "Unmarshalled value should not be null");
        ProcessInstance<BpmnVariables> pi = marshaller.unmarshallReadOnlyProcessInstance(doc, process);
        assertNotNull(pi, "Unmarshalled value should not be null");
    }

    @Test
    void testDocumentMarshallingException() {
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "testValue")));
        assertThatExceptionOfType(DocumentMarshallingException.class).isThrownBy(() -> marshaller.marshalProcessInstance(processInstance));
    }

    @Test
    void testDocumentUnmarshallingException() {
        ProcessInstanceDocument document = new ProcessInstanceDocument();
        assertThrows(DocumentUnmarshallingException.class, () -> marshaller.unmarshallProcessInstance(document, process));
    }

    @Test
    void testNoStrategy() {
        DocumentProcessInstanceMarshaller m = new DocumentProcessInstanceMarshaller(null);
        assertNotNull(m, "DocumentProcessInstanceMarshaller can be created with no object marshalling strategy");
    }
}
