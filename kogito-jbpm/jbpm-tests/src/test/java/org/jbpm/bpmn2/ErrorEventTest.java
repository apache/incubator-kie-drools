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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.error.EndErrorModel;
import org.jbpm.bpmn2.error.EndErrorProcess;
import org.jbpm.bpmn2.error.ErrorBoundaryEventOnServiceTaskModel;
import org.jbpm.bpmn2.error.ErrorBoundaryEventOnServiceTaskProcess;
import org.jbpm.bpmn2.error.ErrorVariableModel;
import org.jbpm.bpmn2.error.ErrorVariableProcess;
import org.jbpm.bpmn2.event.BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRefModel;
import org.jbpm.bpmn2.event.BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRefProcess;
import org.jbpm.bpmn2.event.BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRefModel;
import org.jbpm.bpmn2.event.BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRefProcess;
import org.jbpm.bpmn2.event.BoundaryErrorEventSubProcessExceptionMappingModel;
import org.jbpm.bpmn2.event.BoundaryErrorEventSubProcessExceptionMappingProcess;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.bpmn2.objects.ExceptionOnPurposeHandler;
import org.jbpm.bpmn2.objects.MyError;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventListener;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.utils.EventTrackerProcessListener;
import org.jbpm.test.utils.ProcessTestHelper;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.kogito.Application;
import org.kie.kogito.handlers.AlwaysThrowingComponent_throwException__8DA0CD88_0714_43C1_B492_A70FADE42361_Handler;
import org.kie.kogito.handlers.HelloService_helloException_ServiceTask_2_Handler;
import org.kie.kogito.handlers.LoggingComponent_logException__E5B0E78B_0112_42F4_89FF_0DCC4FCB6BCD_Handler;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorEventTest extends JbpmBpmn2TestCase {

    @Test
    public void testEventSubprocessError() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-EventSubprocessError.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessError");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessErrorThrowOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-EventSubprocessError.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                throw new MyError();

            }

            @Override
            public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                manager.abortWorkItem(workItem.getStringId());
            }

        });

        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessError");

        assertProcessInstanceFinished(processInstance, kruntime);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessErrorWithErrorCode() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-EventSubprocessErrorHandlingWithErrorCode.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script2")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubprocessErrorHandlingWithErrorCode");

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "Script1", "starterror", "Script2", "end2", "eventsubprocess");
        assertProcessVarValue(processInstance, "CapturedException", "java.lang.RuntimeException: XXX");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testEventSubprocessErrorWithOutErrorCode() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/EventSubprocessErrorHandlingWithOutErrorCode.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script2")) {
                    executednodes.add(((KogitoNodeInstance) event.getNodeInstance()).getStringId());
                }
            }

        };
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoProcessInstance processInstance = kruntime.startProcess("order-fulfillment-bpm.ccc");

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "Script1", "starterror", "Script2", "end2", "eventsubprocess");
        assertProcessVarValue(processInstance, "CapturedException", "java.lang.RuntimeException: XXX");
        assertThat(executednodes).hasSize(1);

    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("ErrorBoundaryEventInterrupting");
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testErrorBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorBoundaryEventOnTask");

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).hasSize(2);

        KogitoWorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "start", "split", "User Task", "User task error attached", "error end event");
        assertNotNodeTriggered(processInstance.getStringId(), "Script Task", "error1", "error2");
    }

    @Test
    public void testErrorBoundaryEventOnServiceTask() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener listener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, listener);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        ProcessTestHelper.registerHandler(app, "org.jbpm.bpmn2.objects.HelloService_helloException_ServiceTask_2_Handler", new HelloService_helloException_ServiceTask_2_Handler());

        org.kie.kogito.process.Process<ErrorBoundaryEventOnServiceTaskModel> definition = ErrorBoundaryEventOnServiceTaskProcess.newProcess(app);

        ErrorBoundaryEventOnServiceTaskModel model = definition.createModel();
        model.setS("test");
        org.kie.kogito.process.ProcessInstance<ErrorBoundaryEventOnServiceTaskModel> instance = definition.createInstance(model);
        instance.start();

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).hasSize(1);
        ProcessTestHelper.completeWorkItem(instance, "john", Collections.emptyMap());

        assertThat(instance.status()).isEqualTo(org.kie.kogito.process.ProcessInstance.STATE_COMPLETED);

        assertThat(listener.tracked())
                .anyMatch(ProcessTestHelper.triggered("start"))
                .anyMatch(ProcessTestHelper.triggered("split"))
                .anyMatch(ProcessTestHelper.triggered("User Task"))
                .anyMatch(ProcessTestHelper.triggered("Service task error attached"))
                .anyMatch(ProcessTestHelper.triggered("end0"))
                .anyMatch(ProcessTestHelper.triggered("Script Task"))
                .anyMatch(ProcessTestHelper.triggered("error2"))
                .noneMatch(ProcessTestHelper.triggered("end"));

    }

    @Test
    public void testErrorBoundaryEventOnBusinessRuleTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ErrorBoundaryEventOnBusinessRuleTask.bpmn2",
                "BPMN2-ErrorBoundaryEventOnBusinessRuleTask.drl");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-ErrorBoundaryEventOnBusinessRuleTask");

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "business rule task error attached", "error1");
    }

    @Test
    public void testMultiErrorBoundaryEventsOnBusinessRuleTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiErrorBoundaryEventsOnBusinessRuleTask.bpmn2",
                "BPMN2-MultiErrorBoundaryEventsOnBusinessRuleTask.drl");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person());

        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-MultiErrorBoundaryEventeOnBusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "business rule task error attached",
                "NPE Script Task", "error1");

        kruntime.getKieSession().dispose();

        kruntime = createKogitoProcessRuntime("BPMN2-MultiErrorBoundaryEventsOnBusinessRuleTask.bpmn2",
                "BPMN2-MultiErrorBoundaryEventsOnBusinessRuleTask.drl");

        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        params = new HashMap<>();
        params.put("person", new Person("unsupported"));
        KogitoProcessInstance processInstance2 = kruntime.startProcess("BPMN2-MultiErrorBoundaryEventeOnBusinessRuleTask", params);
        assertProcessInstanceFinished(processInstance2, kruntime);
        assertNodeTriggered(processInstance2.getStringId(), "start", "business rule task error attached",
                "UOE Script Task", "error2");
    }

    @Test
    public void testCatchErrorBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                if (workItem.getParameter("ActorId").equals("mary")) {
                    throw new MyError();
                }
            }

            @Override
            public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                manager.abortWorkItem(workItem.getStringId());
            }

        });

        KogitoProcessInstance processInstance = kruntime.startProcess("ErrorBoundaryEventOnTask");

        assertProcessInstanceActive(processInstance);
        assertNodeTriggered(processInstance.getStringId(), "start", "split", "User Task", "User task error attached",
                "Script Task", "error1", "error2");

    }

    @Test
    public void testErrorSignallingExceptionServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExceptionServiceProcess-ErrorSignalling.bpmn2");

        StandaloneBPMNProcessTest.runTestErrorSignallingExceptionServiceTask(kruntime);
    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");

        StandaloneBPMNProcessTest.runTestSignallingExceptionServiceTask(kruntime);
    }

    @Test
    public void testEventSubProcessErrorWithScript() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-EventSubProcessErrorWithScript.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Request Handler", new SignallingTaskHandlerDecorator(ExceptionOnPurposeHandler.class, "Error-90277"));
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Error Handler", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("EventSubProcessErrorWithScript");

        assertProcessInstanceAborted(processInstance);
        assertThat(((WorkflowProcessInstance) processInstance).getOutcome()).isEqualTo("90277");

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testErrorBoundaryEventOnEntry() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BoundaryErrorEventCatchingOnEntryException.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventOnEntry");

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);
        assertThat(handler.getWorkItems()).hasSize(1);
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testErrorBoundaryEventOnExit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BoundaryErrorEventCatchingOnExitException.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventOnExit");

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);
        KogitoWorkItem workItem = handler.getWorkItem();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertThat(handler.getWorkItems()).hasSize(1);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-BoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef.bpmn2");
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithWorkItemExecutionError() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-BoundaryErrorEventDefaultHandlerByErrorCode.bpmn2");
        WorkItemExecutionErrorWorkItemHandler handler = new WorkItemExecutionErrorWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventDefaultHandlerByErrorCode");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-BoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef.bpmn2");
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRef() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener listener = new EventTrackerProcessListener();
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        ProcessTestHelper.registerProcessEventListener(app, listener);
        org.kie.kogito.process.Process<BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRefModel> definition =
                BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRefProcess.newProcess(app);
        org.kie.kogito.process.ProcessInstance<BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRefModel> instance = definition.createInstance(definition.createModel());
        instance.start();

        assertThat(listener.tracked())
                .anyMatch(ProcessTestHelper.triggered("Start"))
                .anyMatch(ProcessTestHelper.triggered("User Task"))
                .anyMatch(ProcessTestHelper.left("MyBoundaryErrorEvent"));

    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRef() throws Exception {

        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener listener = new EventTrackerProcessListener();
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        ProcessTestHelper.registerProcessEventListener(app, listener);
        org.kie.kogito.process.Process<BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRefModel> definition =
                BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRefProcess.newProcess(app);
        org.kie.kogito.process.ProcessInstance<BoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRefModel> instance = definition.createInstance(definition.createModel());
        instance.start();

        assertThat(listener.tracked())
                .anyMatch(ProcessTestHelper.triggered("Start"))
                .anyMatch(ProcessTestHelper.triggered("User Task"))
                .anyMatch(ProcessTestHelper.left("MyBoundaryErrorEvent"));
    }

    @Test
    public void testBoundaryErrorEventSubProcessExceptionMapping() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);
        org.kie.kogito.process.Process<BoundaryErrorEventSubProcessExceptionMappingModel> definition =
                BoundaryErrorEventSubProcessExceptionMappingProcess.newProcess(app);
        org.kie.kogito.process.ProcessInstance<BoundaryErrorEventSubProcessExceptionMappingModel> instance = definition.createInstance(definition.createModel());
        instance.start();

        assertThat(instance.variables().getVar1())
                .isNotNull()
                .isInstanceOf(RuntimeException.class);

    }

    @Test
    public void testBoundaryErrorEventStructureRef() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-BoundaryErrorEventStructureRef.bpmn2");
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("BoundaryErrorEventStructureRef");

        assertNodeTriggered(processInstance.getStringId(), "Start", "User Task", "MyBoundaryErrorEvent");
    }

    @Test
    public void testEndErrorWithSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime("org/jbpm/bpmn2/error/BPMN2-EndErrorWithEventSubprocess.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("EndErrorWithEventSubprocess");

        assertNodeTriggered(processInstance.getStringId(), "start", "task", "subprocess-task");

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEndError() throws Exception {
        Application app = ProcessTestHelper.newApplication();
        EventTrackerProcessListener eventTrackerProcessListener = new EventTrackerProcessListener();
        ProcessTestHelper.registerProcessEventListener(app, eventTrackerProcessListener);
        org.kie.kogito.process.Process<EndErrorModel> processDefinition = EndErrorProcess.newProcess(app);
        org.kie.kogito.process.ProcessInstance<EndErrorModel> instance = processDefinition.createInstance(processDefinition.createModel());
        instance.start();

        assertThat(eventTrackerProcessListener.tracked()).anyMatch(ProcessTestHelper.triggered("start"));
        assertThat(eventTrackerProcessListener.tracked()).anyMatch(ProcessTestHelper.triggered("task"));
        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_ABORTED);

    }

    @Test
    public void testErrorVariable() throws Exception {

        Application app = ProcessTestHelper.newApplication();
        ProcessTestHelper.registerHandler(app, "Service Task", new WorkItemExecutionErrorWorkItemHandler("MY_ERROR"));
        ProcessTestHelper.registerHandler(app, "org.jbpm.bpmn2.services.AlwaysThrowingComponent_throwException__8DA0CD88_0714_43C1_B492_A70FADE42361_Handler",
                new AlwaysThrowingComponent_throwException__8DA0CD88_0714_43C1_B492_A70FADE42361_Handler());
        ProcessTestHelper.registerHandler(app, "org.jbpm.bpmn2.services.LoggingComponent_logException__E5B0E78B_0112_42F4_89FF_0DCC4FCB6BCD_Handler",
                new LoggingComponent_logException__E5B0E78B_0112_42F4_89FF_0DCC4FCB6BCD_Handler());
        org.kie.kogito.process.Process<ErrorVariableModel> processDefinition = ErrorVariableProcess.newProcess(app);
        ErrorVariableModel model = processDefinition.createModel();
        model.setTheException("theException");
        org.kie.kogito.process.ProcessInstance<ErrorVariableModel> instance = processDefinition.createInstance(model);
        instance.start();

        assertThat(instance.variables().getTheException()).isInstanceOf(WorkItemExecutionException.class);
        assertThat(instance.status()).isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    class ExceptionWorkItemHandler implements KogitoWorkItemHandler {

        @Override
        public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            throw new RuntimeException();
        }

        @Override
        public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        }

    }

    class WorkItemExecutionErrorWorkItemHandler implements KogitoWorkItemHandler {

        private final String errorCode;

        public WorkItemExecutionErrorWorkItemHandler() {
            this("500");
        }

        public WorkItemExecutionErrorWorkItemHandler(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
            throw new WorkItemExecutionException(errorCode);
        }

        @Override
        public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        }

    }
}
