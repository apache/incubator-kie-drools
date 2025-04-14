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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.api.event.process.SignalEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.process.EventDescription;
import org.kie.kogito.process.GroupedNamedDataType;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.uow.UnitOfWork;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SignalEventIT extends AbstractCodegenIT {

    @Test
    public void testIntermediateThrowSignal() throws Exception {
        Application app = generateCode(Collections.singletonMap(TYPE.PROCESS, Collections.singletonList("signalevent/IntermediateThrowEventSignal.bpmn2")));
        KogitoProcessEventListener listener = mock(KogitoProcessEventListener.class);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        assertThat(app).isNotNull();
        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateThrowEventSignal");
        Model m = p.createModel();
        m.update(Collections.singletonMap("x", "Javierito"));
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        ArgumentCaptor<SignalEvent> signalEvent = ArgumentCaptor.forClass(SignalEvent.class);
        verify(listener).onSignal(signalEvent.capture());
        assertThat(signalEvent.getValue().getSignalName()).isEqualTo("MySignal");
        assertThat(signalEvent.getValue().getSignal()).isEqualTo("Javierito");

    }

    @Test
    public void testIntermediateEndSignal() throws Exception {
        Application app = generateCode(Collections.singletonMap(TYPE.PROCESS, Collections.singletonList("signalevent/EndEventSignalWithData.bpmn2")));
        KogitoProcessEventListener listener = mock(KogitoProcessEventListener.class);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        assertThat(app).isNotNull();
        Process<? extends Model> p = app.get(Processes.class).processById("src.simpleEndSignal");
        ProcessInstance<?> processInstance = p.createInstance(p.createModel());
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        ArgumentCaptor<SignalEvent> signalEvent = ArgumentCaptor.forClass(SignalEvent.class);
        verify(listener).onSignal(signalEvent.capture());
        assertThat(signalEvent.getValue().getSignalName()).isEqualTo("Signal1");
        assertThat(signalEvent.getValue().getSignal()).isEqualTo("Some value");
    }

    @Test
    public void testIntermediateSignalEventWithData() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("signalevent/IntermediateCatchEventSignal.bpmn2"));
        resourcesTypeMap.put(TYPE.USER_TASK, Collections.singletonList("signalevent/IntermediateCatchEventSignal.bpmn2"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEventSignal");

        Model m = p.createModel();

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        Set<EventDescription<?>> eventDescriptions = processInstance.events();
        assertThat(eventDescriptions)
                .hasSize(1)
                .extracting("event").contains("workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("workItem");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.id());

        List<WorkItem> workItems = processInstance.workItems();
        assertThat(workItems).hasSize(1);

        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        eventDescriptions = processInstance.events();
        assertThat(eventDescriptions)
                .hasSize(1)
                .extracting("event").contains("MyMessage");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal");
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.id());

        processInstance.send(Sig.of("MyMessage", "test"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");

        assertEmpty(p.instances());
    }

    @Test
    public void testBoundarySignalEventWithData() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("signalevent/BoundarySignalEventOnTask.bpmn2"));
        resourcesTypeMap.put(TYPE.USER_TASK, Collections.singletonList("signalevent/BoundarySignalEventOnTask.bpmn2"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BoundarySignalOnTask");

        Model m = p.createModel();

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        Set<EventDescription<?>> eventDescriptions = processInstance.events();
        assertThat(eventDescriptions)
                .hasSize(2)
                .extracting("event").contains("MySignal", "workItemCompleted");
        assertThat(eventDescriptions)
                .extracting("eventType").contains("signal", "workItem");
        assertThat(eventDescriptions)
                .extracting("dataType").hasAtLeastOneElementOfType(GroupedNamedDataType.class);
        assertThat(eventDescriptions)
                .extracting("processInstanceId").contains(processInstance.id());

        processInstance.send(Sig.of("MySignal", "test"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");

        assertEmpty(p.instances());
    }

    @Test
    public void testBoundaryInterruptingSignalEventWithData() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("signalevent/BoundaryInterruptingSignalEventOnTask.bpmn2"));
        resourcesTypeMap.put(TYPE.USER_TASK, Collections.singletonList("signalevent/BoundaryInterruptingSignalEventOnTask.bpmn2"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BoundarySignalOnTask");

        Model m = p.createModel();

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        processInstance.send(Sig.of("MySignal", "test"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");

        assertEmpty(p.instances());
    }

    @Test
    public void testIntermediateSignalEventWithDataControlledByUnitOfWork() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("signalevent/IntermediateCatchEventSignal.bpmn2"));
        resourcesTypeMap.put(TYPE.USER_TASK, Collections.singletonList("signalevent/IntermediateCatchEventSignal.bpmn2"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        // create first unit of work
        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEventSignal");

        Model m = p.createModel();

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        uow.end();
        // after the unit of work is ended process instance shows up in the list
        assertOne(p.instances());

        uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();
        List<WorkItem> workItems = processInstance.workItems();
        assertThat(workItems).hasSize(1);

        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        uow.end();

        uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();
        processInstance.send(Sig.of("MyMessage", "test"));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKey("x");
        assertThat(result.toMap().get("x")).isEqualTo("test");

        uow.end();
        // after unit of work is ended instance is gone from the list
        assertEmpty(p.instances());
    }
}
