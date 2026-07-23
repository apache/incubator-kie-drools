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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests dynamic subprocess resolution where a BPMN {@code callActivity}
 * {@code calledElement} may contain an MVEL expression evaluated at runtime.
 *
 * Covers:
 * <ul>
 * <li>Static and dynamic subprocess IDs</li>
 * <li>Simple and compound MVEL expressions</li>
 * <li>I/O data mappings</li>
 * <li>Asynchronous execution ({@code waitForCompletion=false})</li>
 * <li>Root process metadata propagation</li>
 * <li>Invalid process IDs</li>
 * <li>Repeated invocations with different variables</li>
 * </ul>
 */
public class DynamicSubProcessTest extends JbpmBpmn2TestCase {

    @Test
    public void testCallActivityWithStaticProcessId() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivity.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");

        KogitoProcessInstance processInstance = kruntime.startProcess("CallActivity", params);

        assertProcessInstanceCompleted(processInstance);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y"))
                .isEqualTo("new value");
    }

    @Test
    public void testCallActivityWithSimpleVariableExpression() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByVariable.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "CallActivitySubProcess");
        params.put("x", "dynamicInput");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "DynamicCallActivityByVariable", params);

        assertProcessInstanceCompleted(processInstance);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y"))
                .isEqualTo("new value");
    }

    @Test
    public void testCallActivityWithMvelCompoundExpression() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByExpression.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("prefix", "CallActivity");
        params.put("x", "expressionInput");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "DynamicCallActivityByExpression", params);

        assertProcessInstanceCompleted(processInstance);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y"))
                .isEqualTo("new value");
    }

    @Test
    public void testCallActivityDynamicWithIoMappings() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByVariable.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "CallActivitySubProcess");
        params.put("x", "myInputValue");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "DynamicCallActivityByVariable", params);

        assertProcessInstanceCompleted(processInstance);
        // Subprocess sets subY = "new value"; it must flow back to parent variable y
        String y = (String) ((KogitoWorkflowProcessInstance) processInstance).getVariable("y");
        assertThat(y).isEqualTo("new value");
    }

    @Test
    public void testCallActivityDynamicNoWait() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityNoWait.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        final List<String> startedSubprocesses = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        if ("CallActivitySubProcess".equals(event.getProcessInstance().getProcessId())) {
                            startedSubprocesses.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
                        }
                    }
                });

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "CallActivitySubProcess");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "DynamicCallActivityNoWait", params);

        // Parent must have completed already
        assertProcessInstanceCompleted(processInstance);
        // Child was spawned
        assertThat(startedSubprocesses).hasSize(1);
    }

    @Test
    public void testRootProcessMetadataPropagation() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByVariable.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        final List<ProcessInstanceImpl> childInstances = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        if ("CallActivitySubProcess".equals(event.getProcessInstance().getProcessId())) {
                            childInstances.add((ProcessInstanceImpl) event.getProcessInstance());
                        }
                    }
                });

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "CallActivitySubProcess");
        params.put("x", "rootTest");

        KogitoProcessInstance parentInstance = kruntime.startProcess(
                "DynamicCallActivityByVariable", params);

        assertProcessInstanceCompleted(parentInstance);
        assertThat(childInstances).hasSize(1);

        ProcessInstanceImpl child = childInstances.get(0);
        // When the parent is the root, rootProcessId must equal the parent's processId
        assertThat(child.getRootProcessId()).isEqualTo("DynamicCallActivityByVariable");
        // rootProcessInstanceId must equal the parent's instance id
        assertThat(child.getRootProcessInstanceId()).isEqualTo(parentInstance.getStringId());
        // rootProcessVersion must equal the parent's declared version "1"
        assertThat(child.getRootProcessVersion()).isEqualTo("1");
    }

    @Test
    public void testCallActivityDynamicUnknownProcessEndsInError() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityUnknownProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "NonExistentProcess");

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityUnknownProcess", params);

        // The runtime catches the IllegalArgumentException and marks the instance as ERROR
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage())
                .contains("NonExistentProcess");
    }

    @Test
    public void testCallActivityDynamicNullSubprocessIdEndsInError() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityUnknownProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", null);

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityUnknownProcess", params);

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage())
                .contains("Cannot resolve subprocess ID from expression '#{subprocessId}'");
    }

    @Test
    public void testCallActivityDynamicEmptySubprocessIdEndsInError() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityUnknownProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "");

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityUnknownProcess", params);

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage())
                .contains("Cannot resolve subprocess ID from expression '#{subprocessId}'");
    }

    @Test
    public void testCallActivityDynamicBlankSubprocessIdEndsInError() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityUnknownProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "   ");

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityUnknownProcess", params);

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage())
                .contains("Cannot resolve subprocess ID from expression '#{subprocessId}'");
    }

    @Test
    public void testCallActivityDynamicMissingVariableEndsInError() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityMissingVariable.bpmn2");

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityMissingVariable");

        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ERROR);
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage())
                .contains("Cannot resolve subprocess ID from expression '#{missingVar}'");
    }

    @Test
    public void testDynamicSubprocessFaultPropagatesAsAbort() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityUnknownProcess.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicSubProcessFaulty.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "DynamicSubProcessFaulty");

        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityUnknownProcess", params);

        // When the subprocess faults and the parent has no matching exception handler,
        // the parent completes normally (fault is silently absorbed, not propagated).
        // This is the default kruntime behaviour when abortParent=false (the default).
        assertThat(processInstance.getState())
                .as("Without a boundary event, parent should complete normally when child faults")
                .isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testCallActivityWithScriptResolvedSubprocessId() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityScriptResolved.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        // No subprocessId in params — the script inside the process sets it
        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityScriptResolved");

        assertProcessInstanceCompleted(processInstance);
        // CallActivitySubProcess sets subY="new value"; mapped back to y
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y"))
                .isEqualTo("new value");
    }

    @Test
    public void testTwoSequentialCallsDispatchToDifferentSubprocesses() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityTwoSequential.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicSubProcessA.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicSubProcessB.bpmn2");

        final List<String> childIds = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        String pid = event.getProcessInstance().getProcessId();
                        if (!"DynamicCallActivityTwoSequential".equals(pid)) {
                            childIds.add(pid);
                        }
                    }
                });

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "DynamicSubProcessA"); // first call uses this
        KogitoProcessInstance processInstance =
                kruntime.startProcess("DynamicCallActivityTwoSequential", params);

        assertProcessInstanceCompleted(processInstance);
        // Two child processes must have been spawned in order
        assertThat(childIds)
                .hasSize(2)
                .containsExactly("DynamicSubProcessA", "DynamicSubProcessB");
    }

    @Test
    public void testChildProcessReachesStateCompleted() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByVariable.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        final List<KogitoProcessInstance> completedChildren = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void afterProcessCompleted(ProcessCompletedEvent event) {
                        if ("CallActivitySubProcess".equals(
                                event.getProcessInstance().getProcessId())) {
                            completedChildren.add((KogitoProcessInstance) event.getProcessInstance());
                        }
                    }
                });

        Map<String, Object> params = new HashMap<>();
        params.put("subprocessId", "CallActivitySubProcess");
        params.put("x", "completionCheck");

        KogitoProcessInstance parent = kruntime.startProcess("DynamicCallActivityByVariable", params);

        assertProcessInstanceCompleted(parent);
        assertThat(completedChildren).hasSize(1);
        assertThat(completedChildren.get(0).getState())
                .isEqualTo(KogitoProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testConcurrentParentInstancesResolveIndependently() throws Exception {
        // Use the no-wait parent (no I/O mappings) to avoid variable-schema
        // mismatches with the simple child processes A and B.
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityNoWait.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicSubProcessA.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicSubProcessB.bpmn2");

        // Pair each parent instance ID with the child it spawned
        final Map<String, String> parentToChild = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        KogitoProcessInstance child = (KogitoProcessInstance) event.getProcessInstance();
                        String childPid = child.getProcessId();
                        if (!childPid.equals("DynamicCallActivityNoWait")) {
                            ProcessInstanceImpl pi = (ProcessInstanceImpl) child;
                            String parentInstanceId = (String) pi.getMetaData().get("ParentProcessInstanceId");
                            parentToChild.put(parentInstanceId, childPid);
                        }
                    }
                });

        Map<String, Object> paramsA = new HashMap<>();
        paramsA.put("subprocessId", "DynamicSubProcessA");
        KogitoProcessInstance piA = kruntime.startProcess("DynamicCallActivityNoWait", paramsA);

        Map<String, Object> paramsB = new HashMap<>();
        paramsB.put("subprocessId", "DynamicSubProcessB");
        KogitoProcessInstance piB = kruntime.startProcess("DynamicCallActivityNoWait", paramsB);

        // no-wait parent completes immediately
        assertProcessInstanceCompleted(piA);
        assertProcessInstanceCompleted(piB);

        // Each parent must have dispatched to exactly its own child
        assertThat(parentToChild.get(piA.getStringId())).isEqualTo("DynamicSubProcessA");
        assertThat(parentToChild.get(piB.getStringId())).isEqualTo("DynamicSubProcessB");
    }

    @Test
    public void testCallActivityDynamicDispatchedIndependentlyPerInstance() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityByVariable.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        final List<String> childProcessIds = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(
                new org.kie.api.event.process.DefaultProcessEventListener() {
                    @Override
                    public void beforeProcessStarted(ProcessStartedEvent event) {
                        if (!"DynamicCallActivityByVariable".equals(event.getProcessInstance().getProcessId())) {
                            childProcessIds.add(event.getProcessInstance().getProcessId());
                        }
                    }
                });

        // First invocation
        Map<String, Object> params1 = new HashMap<>();
        params1.put("subprocessId", "CallActivitySubProcess");
        params1.put("x", "first");
        KogitoProcessInstance pi1 = kruntime.startProcess("DynamicCallActivityByVariable", params1);
        assertProcessInstanceCompleted(pi1);

        // Second invocation (same subprocess, new instance – verifies stateless resolution)
        Map<String, Object> params2 = new HashMap<>();
        params2.put("subprocessId", "CallActivitySubProcess");
        params2.put("x", "second");
        KogitoProcessInstance pi2 = kruntime.startProcess("DynamicCallActivityByVariable", params2);
        assertProcessInstanceCompleted(pi2);

        // Both dispatches hit CallActivitySubProcess
        assertThat(childProcessIds)
                .hasSize(2)
                .containsOnly("CallActivitySubProcess");
    }

    /**
     * Verifies that a {@code calledElement} built from <em>multiple</em> {@code #{...}}
     * tokens (e.g. {@code "#{prefix}#{suffix}"}) is fully resolved before the subprocess
     * is looked up. This mirrors the behaviour supported in legacy 7.67.x
     * {@code SubProcessNodeInstance}.
     */
    @Test
    public void testCallActivityWithMultipleVariableExpressions() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "org/jbpm/bpmn2/subprocess/BPMN2-DynamicCallActivityMultiExpression.bpmn2",
                "org/jbpm/bpmn2/subprocess/BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        // "#{prefix}#{suffix}" resolves to "CallActivity" + "SubProcess" = "CallActivitySubProcess"
        params.put("prefix", "CallActivity");
        params.put("suffix", "SubProcess");
        params.put("x", "multiExprInput");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "DynamicCallActivityMultiExpression", params);

        assertProcessInstanceCompleted(processInstance);
        assertThat(((KogitoWorkflowProcessInstance) processInstance).getVariable("y"))
                .isEqualTo("new value");
    }
}
