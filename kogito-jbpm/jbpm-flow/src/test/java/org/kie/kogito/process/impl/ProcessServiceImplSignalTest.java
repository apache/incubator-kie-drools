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
package org.kie.kogito.process.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.flexible.AdHocFragment;
import org.kie.kogito.uow.UnitOfWorkManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProcessServiceImpl.signalProcessInstance.
 * Verifies signal routing for traditional signal events, message events, ad hoc nodes,
 * non-existent instances, and non-matching signals.
 */
class ProcessServiceImplSignalTest {

    private ProcessServiceImpl processService;
    private Process<TestModel> process;
    private ProcessInstances<TestModel> processInstances;
    private ProcessInstance<TestModel> processInstance;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setup() {
        Application application = mock(Application.class);
        process = mock(Process.class);
        processInstances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);
        UnitOfWorkManager unitOfWorkManager = mock(UnitOfWorkManager.class);
        ConfigBean configBean = mock(ConfigBean.class);

        when(application.unitOfWorkManager()).thenReturn(unitOfWorkManager);
        when(application.config()).thenReturn(mock(org.kie.kogito.Config.class));
        when(application.config().get(ConfigBean.class)).thenReturn(configBean);
        when(configBean.processInstanceLimit()).thenReturn((short) 100);
        when(process.instances()).thenReturn(processInstances);

        // Make the UoW execute the supplied callable immediately
        org.kie.kogito.uow.UnitOfWork unitOfWork = mock(org.kie.kogito.uow.UnitOfWork.class);
        when(unitOfWorkManager.newUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWorkManager.currentUnitOfWork()).thenReturn(unitOfWork);
        doAnswer(invocation -> {
            invocation.<org.kie.kogito.uow.WorkUnit<?>> getArgument(0).perform();
            return null;
        }).when(unitOfWork).intercept(any());

        processService = new ProcessServiceImpl(application);
    }

    // --- helpers ---

    private void givenInstanceWithEventTypes(String id, List<String> eventTypes, List<AdHocFragment> adHocFragments) {
        when(processInstances.findById(id)).thenReturn(Optional.of(processInstance));
        when(processInstance.eventTypes()).thenReturn(eventTypes);
        when(processInstance.adHocFragments()).thenReturn(adHocFragments);
        when(processInstance.checkError()).thenReturn(processInstance);
        when(processInstance.variables()).thenReturn(new TestModel());
    }

    // --- signal event tests ---

    @Test
    void signalAccepted_whenInstanceWaitingForSignalEvent() {
        String id = "pi-1";
        String signalName = "HelloMartin";

        givenInstanceWithEventTypes(id, List.of(signalName), Collections.emptyList());

        Optional<TestModel> result = processService.signalProcessInstance(process, id, "data", signalName);

        assertThat(result).isPresent();
        verify(processInstance).send(any());
    }

    @Test
    void signalAccepted_whenInstanceWaitingForMessageEvent() {
        // Message catch events register their listener key as "Message-<name>"
        String id = "pi-2";
        String signalName = "MyMessage";

        givenInstanceWithEventTypes(id, List.of("Message-" + signalName), Collections.emptyList());

        Optional<TestModel> result = processService.signalProcessInstance(process, id, "data", signalName);

        assertThat(result).isPresent();
        verify(processInstance).send(any());
    }

    @Test
    void signalAccepted_whenInstanceHasMatchingAdHocFragment() {
        String id = "pi-3";
        String adHocName = "AdHocTask";
        AdHocFragment fragment = new AdHocFragment.Builder(adHocName).withName(adHocName).withAutoStart(false).build();

        givenInstanceWithEventTypes(id, Collections.emptyList(), List.of(fragment));

        Optional<TestModel> result = processService.signalProcessInstance(process, id, null, adHocName);

        assertThat(result).isPresent();
        verify(processInstance).send(any());
    }

    @Test
    void signalAccepted_whenInstanceHasBothSignalEventAndAdHocFragment() {
        String id = "pi-4";
        String signalName = "HelloMartin";
        String adHocName = "AdHocTask";
        AdHocFragment fragment = new AdHocFragment.Builder(adHocName).withName(adHocName).withAutoStart(false).build();

        givenInstanceWithEventTypes(id, List.of(signalName), List.of(fragment));

        assertThat(processService.signalProcessInstance(process, id, "data", signalName)).isPresent();
        assertThat(processService.signalProcessInstance(process, id, null, adHocName)).isPresent();
    }

    @Test
    void signalAccepted_whenInstanceHasMultipleAdHocFragments() {
        String id = "pi-5";
        String node1 = "AdHocTask1";
        String node2 = "AdHocTask2";
        AdHocFragment f1 = new AdHocFragment.Builder(node1).withName(node1).withAutoStart(false).build();
        AdHocFragment f2 = new AdHocFragment.Builder(node2).withName(node2).withAutoStart(true).build();

        givenInstanceWithEventTypes(id, Collections.emptyList(), List.of(f1, f2));

        assertThat(processService.signalProcessInstance(process, id, null, node1)).isPresent();
        assertThat(processService.signalProcessInstance(process, id, null, node2)).isPresent();
    }

    // --- rejection / empty result tests ---

    @Test
    void signalRejected_whenInstanceNotWaitingForSignal() {
        String id = "pi-6";

        givenInstanceWithEventTypes(id, List.of("OtherSignal"), Collections.emptyList());

        Optional<TestModel> result = processService.signalProcessInstance(process, id, "data", "HelloMartin");

        assertThat(result).isEmpty();
    }

    @Test
    void signalRejected_whenInstanceDoesNotExist() {
        when(processInstances.findById("non-existent")).thenReturn(Optional.empty());

        Optional<TestModel> result = processService.signalProcessInstance(process, "non-existent", "data", "HelloMartin");

        assertThat(result).isEmpty();
    }

    @Test
    void signalRejected_whenInstanceHasNoEventsAndNoAdHocFragments() {
        String id = "pi-7";

        givenInstanceWithEventTypes(id, Collections.emptyList(), Collections.emptyList());

        Optional<TestModel> result = processService.signalProcessInstance(process, id, "data", "HelloMartin");

        assertThat(result).isEmpty();
    }

    // --- edge cases ---

    @Test
    void signalAccepted_whenEventTypesContainsNullAlongsideMatchingSignal() {
        // getEventTypes() may contain null entries; ensure no NPE in the filter
        String id = "pi-8";
        String signalName = "HelloMartin";

        List<String> eventTypesWithNulls = new java.util.ArrayList<>();
        eventTypesWithNulls.add(null);
        eventTypesWithNulls.add(signalName);
        eventTypesWithNulls.add(null);
        givenInstanceWithEventTypes(id, eventTypesWithNulls, Collections.emptyList());

        Optional<TestModel> result = processService.signalProcessInstance(process, id, "data", signalName);

        assertThat(result).isPresent();
    }

    // --- test model ---

    static class TestModel implements MappableToModel<TestModel>, Model {
        @Override
        public TestModel toModel() {
            return this;
        }
    }
}
