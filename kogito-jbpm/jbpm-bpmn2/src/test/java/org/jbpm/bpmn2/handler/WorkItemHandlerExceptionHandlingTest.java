/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2.handler;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.VariableScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException.HandlingStrategy;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

import static org.jbpm.process.core.context.variable.VariableScope.VARIABLE_STRICT_ENABLED_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorkItemHandlerExceptionHandlingTest extends JbpmBpmn2TestCase {

    private static Boolean strictVariableSetting = Boolean.parseBoolean(System.getProperty(VARIABLE_STRICT_ENABLED_PROPERTY, Boolean.FALSE.toString()));

    @BeforeAll
    public static void setup() throws Exception {
        VariableScope.setVariableStrictOption(false);
    }

    @AfterAll
    public static void clean() throws Exception {
        VariableScope.setVariableStrictOption(strictVariableSetting);
    }

    @Test
    public void testErrornousHandlerWithStrategyComplete() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.COMPLETE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean");
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());

        assertProcessVarValue(processInstance, "isChecked", "true");

        KogitoWorkItem handledWorkItem = (KogitoWorkItem) workItemHandler.getWorkItem();
        assertEquals(KogitoWorkItem.COMPLETED, handledWorkItem.getState());
    }

    @Test
    public void testErrornousHandlerWithStrategyCompleteWaitState() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.COMPLETE);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());

        KogitoWorkItem receiveWorkItem = testHandler.getWorkItem();

        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        kruntime.getKogitoWorkItemManager().completeWorkItem(receiveWorkItem.getStringId(), results);
        assertProcessVarValue(processInstance, "isChecked", "true");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testErrornousHandlerWithStrategyAbort() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.ABORT);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean", params);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertProcessVarValue(processInstance, "isChecked", "false");

        KogitoWorkItem handledWorkItem = (KogitoWorkItem) workItemHandler.getWorkItem();
        assertEquals(KogitoWorkItem.ABORTED, handledWorkItem.getState());

    }

    @Test
    public void testErrornousHandlerWithStrategyAbortWaitState() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.ABORT);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean", params);
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());

        KogitoWorkItem receiveWorkItem = testHandler.getWorkItem();

        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        kruntime.getKogitoWorkItemManager().completeWorkItem(receiveWorkItem.getStringId(), results);
        assertProcessVarValue(processInstance, "isChecked", "false");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testErrornousHandlerWithStrategyRethrow() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.RETHROW);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean", params);
        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.getState());
    }

    @Test
    public void testErrornousHandlerWithStrategyRetry() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.RETRY);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean", params);
        assertEquals(KogitoProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertProcessVarValue(processInstance, "isChecked", "true");

    }

    @Test
    public void testErrornousHandlerWithStrategyRetryWaitState() throws Exception {

        kruntime = createKogitoProcessRuntime("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.RETRY);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.boolean", params);
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());

        KogitoWorkItem receiveWorkItem = testHandler.getWorkItem();

        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        kruntime.getKogitoWorkItemManager().completeWorkItem(receiveWorkItem.getStringId(), results);
        assertProcessVarValue(processInstance, "isChecked", "true");

    }
}
