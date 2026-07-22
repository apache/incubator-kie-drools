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
package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.bpmn2.collaboration.CollaborationBoundaryMessageModel;
import org.jbpm.bpmn2.collaboration.CollaborationBoundaryMessageProcess;
import org.jbpm.bpmn2.collaboration.CollaborationIntermediateMessageModel;
import org.jbpm.bpmn2.collaboration.CollaborationIntermediateMessageProcess;
import org.jbpm.bpmn2.collaboration.CollaborationStartMessageModel;
import org.jbpm.bpmn2.collaboration.CollaborationStartMessageProcess;
import org.jbpm.process.workitem.builtin.DoNothingWorkItemHandler;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.process.SignalFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CollaborationTest extends JbpmBpmn2TestCase {

    @Test
    public void testBoundaryMessageCollaboration() throws Exception {
        Application application = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(application, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<CollaborationBoundaryMessageModel> processDefinition = CollaborationBoundaryMessageProcess.newProcess(application);
        CollaborationBoundaryMessageModel variables = processDefinition.createModel();
        variables.setMessageId("2");
        org.kie.kogito.process.ProcessInstance<CollaborationBoundaryMessageModel> processInstance = processDefinition.createInstance(variables);
        processInstance.start();
        processInstance.send(SignalFactory.of("Message-collaboration", new Message("1", "example")));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        processInstance.send(SignalFactory.of("Message-collaboration", new Message("2", "example")));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testStartMessageCollaboration() throws Exception {
        final List<String> processInstanceId = new ArrayList<>();
        Application application = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerProcessEventListener(application, new DefaultKogitoProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                processInstanceId.add(event.getProcessInstance().getId());
            }
        });
        ProcessTestHelper.registerHandler(application, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<CollaborationStartMessageModel> processDefinition = CollaborationStartMessageProcess.newProcess(application);

        processDefinition.send(SignalFactory.of("collaboration", new Message("1", "example")));

        assertThat(processInstanceId).hasSize(1);

    }

    @Test
    public void testStartMessageCollaborationNoMatch() throws Exception {
        final List<String> processInstanceId = new ArrayList<>();
        Application application = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerProcessEventListener(application, new DefaultKogitoProcessEventListener() {
            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                processInstanceId.add(event.getProcessInstance().getId());
            }
        });
        ProcessTestHelper.registerHandler(application, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<CollaborationStartMessageModel> processDefinition = CollaborationStartMessageProcess.newProcess(application);

        processDefinition.send(SignalFactory.of("Message-collaboration", new Message("2", "example")));

        assertThat(processInstanceId).hasSize(0);

    }

    @Test
    public void testIntermediateMessageCollaboration() throws Exception {
        Application application = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(application, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<CollaborationIntermediateMessageModel> processDefinition = CollaborationIntermediateMessageProcess.newProcess(application);
        CollaborationIntermediateMessageModel variables = processDefinition.createModel();
        variables.setMessageId("2");
        org.kie.kogito.process.ProcessInstance<CollaborationIntermediateMessageModel> processInstance = processDefinition.createInstance(variables);
        processInstance.start();
        processInstance.send(SignalFactory.of("Message-collaboration", new Message("1", "example")));

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        processInstance.send(SignalFactory.of("Message-collaboration", new Message("2", "example")));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

    }

    @Test
    public void testInvalidIntermediateMessageCollaboration() throws Exception {
        Application application = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(application, "Human Task", new DoNothingWorkItemHandler());
        org.kie.kogito.process.Process<CollaborationIntermediateMessageModel> processDefinition = CollaborationIntermediateMessageProcess.newProcess(application);
        CollaborationIntermediateMessageModel variables = processDefinition.createModel();
        variables.setMessageId("2");
        org.kie.kogito.process.ProcessInstance<CollaborationIntermediateMessageModel> processInstance = processDefinition.createInstance(variables);
        processInstance.start();

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            processInstance.send(SignalFactory.of("Message-collaboration", new Message(null, "example")));
        });
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        processInstance.send(SignalFactory.of("Message-collaboration", new Message("2", "example")));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
