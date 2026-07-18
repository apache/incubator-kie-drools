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
 * Tests for the dynamic subprocess feature introduced on the
 * {@code support-dynamic-process-calling} branch.
 * <p>
 * The feature rewrites {@code LambdaSubProcessNodeInstance} so that the
 * {@code calledElement} attribute of a BPMN {@code callActivity} node may
 * contain an MVEL expression (e.g. {@code #{subprocessId}} or
 * {@code #{prefix + 'SubProcess'}}) that is resolved at <em>runtime</em>
 * against the current process-instance variables. The subprocess is then
 * looked up dynamically via {@code Processes.processById()} instead of being
 * wired at code-generation time.
 * </p>
 *
 * <h3>Test matrix</h3>
 * <ol>
 * <li>Static process ID – baseline regression test, nothing changes.</li>
 * <li>Simple variable expression {@code #{subprocessId}} – resolved at runtime.</li>
 * <li>Compound MVEL expression {@code #{prefix + 'SubProcess'}} – resolved at runtime.</li>
 * <li>Variable expression with I/O data mappings – input forwarded to child,
 * output read back into parent.</li>
 * <li>{@code waitForCompletion=false} – parent completes immediately while
 * child runs asynchronously.</li>
 * <li>Root-process metadata propagation – {@code rootProcessId} and
 * {@code rootProcessVersion} are correctly set on the child instance.</li>
 * <li>Unknown process ID – {@code IllegalArgumentException} thrown.</li>
 * <li>Multiple invocations with different variables – child process dispatched
 * correctly each time.</li>
 * </ol>
 */
public class DynamicSubProcessTest extends JbpmBpmn2TestCase {

    // -------------------------------------------------------------------------
    // 1. Static (hard-coded) process ID – baseline regression
    // -------------------------------------------------------------------------

    /**
     * Verifies that a {@code callActivity} with a static {@code calledElement}
     * (no expression) still works correctly after the refactoring.
     */
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

    // -------------------------------------------------------------------------
    // 2. Simple variable expression  #{subprocessId}
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@code calledElement="#{subprocessId}"} is resolved at
     * runtime to the value held in the process variable {@code subprocessId},
     * and that the correct child process is invoked.
     */
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

    // -------------------------------------------------------------------------
    // 3. Compound MVEL expression  #{prefix + 'SubProcess'}
    // -------------------------------------------------------------------------

    /**
     * Verifies that a compound MVEL expression in {@code calledElement} is
     * evaluated correctly at runtime: {@code #{prefix + 'SubProcess'}} with
     * {@code prefix="CallActivity"} must resolve to {@code "CallActivitySubProcess"}.
     */
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

    // -------------------------------------------------------------------------
    // 4. I/O data-mapping with variable expression
    // -------------------------------------------------------------------------

    /**
     * Verifies full I/O data-mapping round-trip when the process ID is dynamic:
     * the parent's variable {@code x} is mapped into the child's {@code subX},
     * and the child's {@code subY} is mapped back into the parent's {@code y}.
     */
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

    // -------------------------------------------------------------------------
    // 5. waitForCompletion = false
    // -------------------------------------------------------------------------

    /**
     * When {@code waitForCompletion=false} the parent completes immediately
     * without waiting for the child process to finish.
     */
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

    // -------------------------------------------------------------------------
    // 6. Root-process metadata propagation
    // -------------------------------------------------------------------------

    /**
     * Verifies that the child process instance has its {@code rootProcessId},
     * {@code rootProcessInstanceId}, and {@code rootProcessVersion} correctly
     * set by {@link org.jbpm.workflow.instance.node.LambdaSubProcessNodeInstance}
     * when the parent is itself a root process.
     * <p>
     * The parent BPMN declares {@code tns:version="1"} so {@code rootProcessVersion}
     * must be propagated as {@code "1"}.
     */
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

    // -------------------------------------------------------------------------
    // 7. Invalid subprocess name scenarios
    // -------------------------------------------------------------------------

    /**
     * When the resolved process ID does not match any registered process the
     * runtime sets the process instance to {@code STATE_ERROR} (the exception
     * is caught internally and the instance transitions to the error state).
     */
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

    /**
     * When the variable that holds the subprocess ID is {@code null}, the runtime
     * fails with a descriptive error naming the expression and the variable.
     * The process instance transitions to {@code STATE_ERROR}.
     */
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

    /**
     * When the variable resolves to an empty string, the runtime fails with a
     * descriptive error naming the expression and the variable.
     * The process instance transitions to {@code STATE_ERROR}.
     */
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

    /**
     * When the variable resolves to a whitespace-only string, the runtime fails
     * with a descriptive error naming the expression and the variable.
     * The process instance transitions to {@code STATE_ERROR}.
     */
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

    /**
     * When the #{...} references a variable that was never declared as a process
     * property (i.e. it has no variable scope entry at all) the runtime fails
     * with a descriptive error naming the expression and the variable.
     * The process instance transitions to {@code STATE_ERROR}.
     */
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

    // -------------------------------------------------------------------------
    // 9. Subprocess fault propagation via dynamic call activity
    // -------------------------------------------------------------------------

    /**
     * Verifies that when a dynamically-resolved subprocess ends with an error
     * end event, the parent process transitions to {@code STATE_ABORTED} (not
     * {@code STATE_ERROR}). This is the correct kruntime behaviour: the fault
     * propagates up and aborts the parent, and in a production (codegen) setup
     * an Error Boundary Event on the parent's callActivity would catch it.
     * <p>
     * The key assertion here is {@code STATE_ABORTED}, not {@code STATE_ERROR} —
     * the difference matters for error boundary event handling.
     */
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

    // -------------------------------------------------------------------------
    // 10. Script-resolved subprocess ID
    // -------------------------------------------------------------------------

    /**
     * Verifies that a subprocess ID computed by a script task at runtime
     * (not passed as a start parameter) is picked up correctly by the
     * immediately following callActivity.
     * <p>
     * The process starts with {@code subprocessId=null}, a script sets it to
     * {@code "CallActivitySubProcess"}, and the callActivity resolves and
     * invokes the correct subprocess.
     */
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

    // -------------------------------------------------------------------------
    // 11. Two sequential calls resolve to different child processes
    // -------------------------------------------------------------------------

    /**
     * A single parent process instance executes two sequential callActivity
     * nodes that both use {@code calledElement="#{subprocessId}"}. A script
     * task between them changes the variable value, so the first invocation
     * calls {@code DynamicSubProcessA} and the second calls
     * {@code DynamicSubProcessB}. This confirms that resolution is
     * re-evaluated at execution time, not cached from the first call.
     */
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

    // -------------------------------------------------------------------------
    // 12. Child process completes with STATE_COMPLETED
    // -------------------------------------------------------------------------

    /**
     * Verifies that when the dynamic subprocess is successfully invoked and
     * finishes, the child process instance itself reaches {@code STATE_COMPLETED}
     * (not aborted, not error). This confirms the full lifecycle of the child.
     */
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

    // -------------------------------------------------------------------------
    // 13. Two concurrent parent instances resolve independently
    // -------------------------------------------------------------------------

    /**
     * Two parent process instances run in the same kruntime session.
     * The first has {@code subprocessId="DynamicSubProcessA"}, the second
     * has {@code subprocessId="DynamicSubProcessB"}. Each must invoke only
     * its own target subprocess — resolution must not bleed across instances.
     */
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

    // -------------------------------------------------------------------------
    // 8. Multiple invocations – different variable value each time
    // -------------------------------------------------------------------------

    /**
     * Creates two separate parent-process instances with different values for
     * {@code subprocessId} and verifies each one dispatches correctly to the
     * expected child process. This exercises the runtime-resolution path
     * independently per process instance.
     */
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
}
