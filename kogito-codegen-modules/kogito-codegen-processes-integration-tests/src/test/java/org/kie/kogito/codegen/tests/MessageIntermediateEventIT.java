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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.api.event.process.MessageEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.SignalFactory;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessageIntermediateEventIT extends AbstractCodegenIT {

    @Test
    public void testMessageEndEventProcess() throws Exception {
        Application app = generateCodeProcessesOnly("messageevent/MessageEndEvent.bpmn2");
        ProcessEventListener listener = mock(KogitoProcessEventListener.class);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        Process<? extends Model> p = app.get(Processes.class).processById("MessageEndEvent");
        Model m = p.createModel();
        m.update(Collections.singletonMap("customerId", "Javierito"));
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        ArgumentCaptor<MessageEvent> messageEvent = ArgumentCaptor.forClass(MessageEvent.class);
        verify(listener).onMessage(messageEvent.capture());
        assertThat(messageEvent.getValue().getMessageName()).isEqualTo("processedcustomers");
        assertThat(messageEvent.getValue().getMessage()).isEqualTo("Javierito");
    }

    @Test
    public void testMessageThrowEventProcess() throws Exception {
        Application app = generateCodeProcessesOnly("messageevent/IntermediateThrowEventMessage.bpmn2");
        ProcessEventListener listener = mock(KogitoProcessEventListener.class);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateThrowEventMessage");
        Model m = p.createModel();
        m.update(Collections.singletonMap("customerId", "Javierito"));
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        ArgumentCaptor<MessageEvent> messageEvent = ArgumentCaptor.forClass(MessageEvent.class);
        verify(listener).onMessage(messageEvent.capture());
        assertThat(messageEvent.getValue().getMessageName()).isEqualTo("customers");
        assertThat(messageEvent.getValue().getMessage()).isEqualTo("Javierito");
    }

    @Test
    public void testMessageCatchEventProcess() throws Exception {

        Application app = generateCodeProcessesOnly("messageevent/IntermediateCatchEventMessage.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEventMessage");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(SignalFactory.of("Message-customers", "CUS-00998877"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("customerId")
                .containsEntry("customerId", "CUS-00998877");
    }

    @Test
    public void testMessageBoundaryCatchEventProcess() throws Exception {

        Application app = generateCodeProcessesOnly("messageevent/BoundaryMessageEventOnTask.bpmn2");
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BoundaryMessageOnTask");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(SignalFactory.of("Message-customers", "CUS-00998877"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("customerId")
                .containsEntry("customerId", "CUS-00998877");
    }

    @Test
    public void malformedShouldThrowException() {
        assertThatExceptionOfType(ProcessCodegenException.class).isThrownBy(() -> {
            generateCodeProcessesOnly("messageevent/EventNodeMalformed.bpmn2");
        });
    }
}
