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

import org.jbpm.bpmn2.escalation.EscalationEndEventModel;
import org.jbpm.bpmn2.escalation.EscalationEndEventProcess;
import org.jbpm.bpmn2.event.EndEventSignalWithDataModel;
import org.jbpm.bpmn2.event.EndEventSignalWithDataProcess;
import org.jbpm.bpmn2.event.ErrorEndEventModel;
import org.jbpm.bpmn2.event.ErrorEndEventProcess;
import org.jbpm.bpmn2.event.MessageEndEventModel;
import org.jbpm.bpmn2.event.MessageEndEventProcess;
import org.jbpm.bpmn2.event.OnEntryExitDesignerScriptProcessModel;
import org.jbpm.bpmn2.event.OnEntryExitDesignerScriptProcessProcess;
import org.jbpm.bpmn2.event.OnEntryExitMixedNamespacedScriptProcessModel;
import org.jbpm.bpmn2.event.OnEntryExitMixedNamespacedScriptProcessProcess;
import org.jbpm.bpmn2.event.OnEntryExitNamespacedScriptProcessModel;
import org.jbpm.bpmn2.event.OnEntryExitNamespacedScriptProcessProcess;
import org.jbpm.bpmn2.event.OnEntryExitScriptProcessModel;
import org.jbpm.bpmn2.event.OnEntryExitScriptProcessProcess;
import org.jbpm.bpmn2.event.ParallelSplitModel;
import org.jbpm.bpmn2.event.ParallelSplitProcess;
import org.jbpm.bpmn2.event.ParallelSplitTerminateModel;
import org.jbpm.bpmn2.event.ParallelSplitTerminateProcess;
import org.jbpm.bpmn2.event.SignalEndEventModel;
import org.jbpm.bpmn2.event.SignalEndEventProcess;
import org.jbpm.bpmn2.event.SubprocessWithParallelSplitTerminateModel;
import org.jbpm.bpmn2.event.SubprocessWithParallelSplitTerminateProcess;
import org.jbpm.process.workitem.builtin.SystemOutWorkItemHandler;
import org.jbpm.test.utils.ProcessTestHelper;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.event.impl.MessageProducer;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.SignalFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class EndEventTest extends JbpmBpmn2TestCase {

    @Test
    public void testImplicitEndParallel() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<ParallelSplitModel> process = ParallelSplitProcess.newProcess(app);
        ProcessInstance<ParallelSplitModel> processInstance = process.createInstance(process.createModel());
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testErrorEndEventProcess() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<ErrorEndEventModel> process = ErrorEndEventProcess.newProcess(app);
        ProcessInstance<ErrorEndEventModel> processInstance = process.createInstance(process.createModel());
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_ABORTED);
        assertThat(((org.kie.kogito.process.impl.AbstractProcessInstance<ErrorEndEventModel>) processInstance)
                .internalGetProcessInstance().getOutcome()).isEqualTo("error");
    }

    @Test
    public void testEscalationEndEventProcess() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<EscalationEndEventModel> process = EscalationEndEventProcess.newProcess(app);
        ProcessInstance<EscalationEndEventModel> processInstance = process.createInstance(process.createModel());
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_ABORTED);

    }

    @Test
    public void testSignalEnd() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<SignalEndEventModel> process = SignalEndEventProcess.newProcess(app);
        SignalEndEventModel model = process.createModel();
        model.setX("MyValue");
        ProcessInstance<SignalEndEventModel> processInstance = process.createInstance(model);
        processInstance.start();
    }

    @Test
    public void testMessageEnd() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        MessageEndEventProcess definition = (MessageEndEventProcess) MessageEndEventProcess.newProcess(app);
        StringBuilder message = new StringBuilder();
        definition.setProducer__2(new MessageProducer<String>() {

            @Override
            public void produce(KogitoProcessInstance pi, String eventData) {
                message.append(eventData);
            }
        });
        MessageEndEventModel model = definition.createModel();
        model.setX("MyValue");
        org.kie.kogito.process.ProcessInstance<MessageEndEventModel> instance = definition.createInstance(model);
        instance.start();
        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(message.toString()).isEqualTo("MyValue");
    }

    @Test
    public void testOnEntryExitScript() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<OnEntryExitScriptProcessModel> process = OnEntryExitScriptProcessProcess.newProcess(app);
        OnEntryExitScriptProcessModel model = process.createModel();
        List<String> myList = new ArrayList<>();
        model.setList(myList);
        ProcessInstance<OnEntryExitScriptProcessModel> processInstance = process.createInstance(model);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);
        assertThat(myList).hasSize(4);
    }

    @Test
    public void testOnEntryExitNamespacedScript() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<OnEntryExitNamespacedScriptProcessModel> process = OnEntryExitNamespacedScriptProcessProcess.newProcess(app);
        OnEntryExitNamespacedScriptProcessModel model = process.createModel();
        List<String> myList = new ArrayList<>();
        model.setList(myList);
        ProcessInstance<OnEntryExitNamespacedScriptProcessModel> processInstance = process.createInstance(model);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);
        assertThat(myList).hasSize(4);
    }

    @Test
    public void testOnEntryExitMixedNamespacedScript() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<OnEntryExitMixedNamespacedScriptProcessModel> process = OnEntryExitMixedNamespacedScriptProcessProcess.newProcess(app);
        OnEntryExitMixedNamespacedScriptProcessModel model = process.createModel();
        List<String> myList = new ArrayList<>();
        model.setList(myList);
        ProcessInstance<OnEntryExitMixedNamespacedScriptProcessModel> processInstance = process.createInstance(model);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);
        assertThat(myList).hasSize(4);
    }

    @Test
    public void testOnEntryExitScriptDesigner() {
        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "MyTask", new SystemOutWorkItemHandler());
        org.kie.kogito.process.Process<OnEntryExitDesignerScriptProcessModel> process = OnEntryExitDesignerScriptProcessProcess.newProcess(app);
        OnEntryExitDesignerScriptProcessModel model = process.createModel();
        List<String> myList = new ArrayList<>();
        model.setList(myList);
        ProcessInstance<OnEntryExitDesignerScriptProcessModel> processInstance = process.createInstance(model);
        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
        assertThat(myList).hasSize(4);
    }

    @Test
    public void testTerminateWithinSubprocessEnd() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<SubprocessWithParallelSplitTerminateModel> process = SubprocessWithParallelSplitTerminateProcess.newProcess(app);
        SubprocessWithParallelSplitTerminateModel model = process.createModel();
        ProcessInstance<SubprocessWithParallelSplitTerminateModel> processInstance = process.createInstance(model);
        processInstance.start();
        processInstance.send(SignalFactory.of("signal1", null));
        assertThat(processInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testTerminateEnd() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<ParallelSplitTerminateModel> process = ParallelSplitTerminateProcess.newProcess(app);
        ParallelSplitTerminateModel model = process.createModel();
        ProcessInstance<ParallelSplitTerminateModel> processInstance = process.createInstance(model);
        processInstance.start();
        processInstance.send(SignalFactory.of("Signal 1", null));
        assertThat(processInstance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalEndWithData() {
        Application app = ProcessTestHelper.newApplication();
        org.kie.kogito.process.Process<EndEventSignalWithDataModel> process = EndEventSignalWithDataProcess.newProcess(app);
        EndEventSignalWithDataModel model = process.createModel();
        ProcessInstance<EndEventSignalWithDataModel> processInstance = process.createInstance(model);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(org.jbpm.process.instance.ProcessInstance.STATE_COMPLETED);
    }
}
