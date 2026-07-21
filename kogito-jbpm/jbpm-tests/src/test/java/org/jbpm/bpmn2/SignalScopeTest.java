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

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.signalscope.*;
import org.jbpm.test.utils.ProcessTestHelper;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.SignalFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for signal scopes in Kogito.
 *
 * Signal Scopes:
 * - default: Broadcasts to all process instances
 * - project: Broadcasts to all process instances (same as default, for jBPM backwards compatibility)
 * - processInstance: Signals only the current process instance
 */
public class SignalScopeTest extends JbpmBpmn2TestCase {

    // ========================================
    // INTERMEDIATE EVENT TESTS
    // ========================================

    /**
     * Test that default scope broadcasts signal to all process instances.
     * This verifies that the default scope broadcasts signals to waiting process instances.
     * Uses two separate processes: one that throws the signal, one that catches it.
     */
    @Test
    public void testIntermediateEventWithDefaultScope() {
        Application app = ProcessTestHelper.newApplication();

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalIntermediateEventModel> catcherProcess = SignalScopeCatchSignalIntermediateEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalIntermediateEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Create thrower process instance that sends signal with default scope. Thrower completes immediately (no user task).
        Process<SignalScopeEmitDefaultScopeSignalModel> throwerProcess = SignalScopeEmitDefaultScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitDefaultScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Both processes should complete - thrower sent signal, catcher received it
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    /**
     * Test that project scope broadcasts signal to all process instances.
     * This is the core test verifying the new project scope functionality.
     * Uses two separate processes: one that throws the signal, one that catches it.
     */
    @Test
    public void testIntermediateEventWithProjectScope() {
        Application app = ProcessTestHelper.newApplication();

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalIntermediateEventModel> catcherProcess = SignalScopeCatchSignalIntermediateEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalIntermediateEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Create thrower process instance that sends signal with project scope. Thrower completes immediately (no user task)
        Process<SignalScopeEmitProjectScopeSignalModel> throwerProcess = SignalScopeEmitProjectScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProjectScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Both processes should complete - thrower sent signal, catcher received it
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    /**
     * Test that processInstance scope signals only within the same process instance.
     * The signal should NOT reach other process instances.
     * At the end the ProcessInstance scope signal is sent from within process instance programmatically to verify the instance completes.
     */
    @Test
    public void testIntermediateEventWithProcessInstanceScope() {
        Application app = ProcessTestHelper.newApplication();

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalIntermediateEventModel> catcherProcess = SignalScopeCatchSignalIntermediateEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalIntermediateEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Create thrower process that sends signal with processInstance scope. Thrower completes immediately (no user task)
        Process<SignalScopeEmitProcessInstanceScopeSignalModel> throwerProcess = SignalScopeEmitProcessInstanceScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProcessInstanceScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Thrower should complete, but catcher should still be waiting (signal didn't reach it)
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Sending a signal programmatically from within catcherInstance. Instance should complete now.
        catcherInstance.send(SignalFactory.of("testSignal", "intermediate-data"));
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    // ========================================
    // BOUNDARY EVENT TESTS
    // ========================================

    /**
     * Test that default scope signal triggers boundary events.
     */
    @Test
    public void testBoundaryEventWithDefaultScope() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalBoundaryEventModel> catcherProcess = SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(1);

        // Create thrower process instance that sends signal with default scope. Thrower completes immediately (no user task).
        Process<SignalScopeEmitDefaultScopeSignalModel> throwerProcess = SignalScopeEmitDefaultScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitDefaultScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Both processes should complete - thrower sent signal, catcher received it
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    /**
     * Test that project scope signal triggers boundary events.
     */
    @Test
    public void testBoundaryEventWithProjectScope() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalBoundaryEventModel> catcherProcess = SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(1);

        // Create thrower process instance that will send signal with project scope. Thrower completes immediately (no user task).
        Process<SignalScopeEmitProjectScopeSignalModel> throwerProcess = SignalScopeEmitProjectScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProjectScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Both processes should complete - thrower sent signal, catcher received it
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    /**
     * Test that processInstance scope signal only triggers boundary event on the specific instance.
     */
    @Test
    public void testBoundaryEventWithProcessInstanceScope() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        // Create catcher process that waits for signal
        Process<SignalScopeCatchSignalBoundaryEventModel> catcherProcess = SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> catcherInstance = catcherProcess.createInstance(catcherProcess.createModel());
        catcherInstance.start();
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(1);

        // Create thrower process that will send signal with processInstance scope. Thrower completes immediately (no user task).
        Process<SignalScopeEmitProcessInstanceScopeSignalModel> throwerProcess = SignalScopeEmitProcessInstanceScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProcessInstanceScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();

        // Thrower should complete, but catcher should still be waiting (signal didn't reach it)
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(catcherInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // Sending a signal programmatically from within catcherInstance. Instance should complete now.
        catcherInstance.send(SignalFactory.of("testSignal", "boundary-data"));
        assertInstanceWasCompleted(app, catcherInstance.id());
    }

    // ========================================
    // START EVENT TESTS
    // ========================================

    /**
     * Test that default scope signal starts process.
     */
    @Test
    public void testStartEventWithDefaultScope() {
        Application app = ProcessTestHelper.newApplication();
        final List<org.kie.api.runtime.process.ProcessInstance> startedProcesses = new ArrayList<>();
        ProcessTestHelper.registerProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void beforeProcessStarted(org.kie.api.event.process.ProcessStartedEvent event) {
                if (!event.getProcessInstance().getProcessId().contains("Emit")) { //we don't want thrower process to be saved
                    startedProcesses.add(event.getProcessInstance());
                }
            }
        });
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        // Create process that waits for signal to start instance
        Process<SignalScopeStartEventModel> eventStartedProcess = SignalScopeStartEventProcess.newProcess(app);

        // Create thrower process instance that will send signal with default scope. It completes immediately.
        Process<SignalScopeEmitDefaultScopeSignalModel> throwerProcess = SignalScopeEmitDefaultScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitDefaultScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // Process invoked by startEvent should be waiting in the ACTIVE state
        assertThat(startedProcesses).hasSize(1);
        assertThat(startedProcesses).extracting(org.kie.api.runtime.process.ProcessInstance::getProcessId).containsExactly("SignalScopeStartEvent");
        assertThat(startedProcesses.get(0).getState()).isEqualTo((org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE));
    }

    /**
     * Test that project scope signal starts process.
     */
    @Test
    public void testStartEventWithProjectScope() {
        Application app = ProcessTestHelper.newApplication();
        final List<org.kie.api.runtime.process.ProcessInstance> startedProcesses = new ArrayList<>();
        ProcessTestHelper.registerProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void beforeProcessStarted(org.kie.api.event.process.ProcessStartedEvent event) {
                if (!event.getProcessInstance().getProcessId().contains("Emit")) { //we don't want thrower process to be saved
                    startedProcesses.add(event.getProcessInstance());
                }
            }
        });
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        // Create process that waits for signal to start instance
        Process<SignalScopeStartEventModel> eventStartedProcess = SignalScopeStartEventProcess.newProcess(app);

        // Create thrower process instance that will send signal with project scope. It completes immediately.
        Process<SignalScopeEmitProjectScopeSignalModel> throwerProcess = SignalScopeEmitProjectScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProjectScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // Process invoked by startEvent should be waiting in the ACTIVE state
        assertThat(startedProcesses).hasSize(1);
        assertThat(startedProcesses).extracting(org.kie.api.runtime.process.ProcessInstance::getProcessId).containsExactly("SignalScopeStartEvent");
        assertThat(startedProcesses.get(0).getState()).isEqualTo((org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE));
    }

    /**
     * Test that processInstance scope signal does NOT start process.
     */
    @Test
    public void testStartEventWithProcessInstanceScope() {
        Application app = ProcessTestHelper.newApplication();
        final List<org.kie.api.runtime.process.ProcessInstance> startedProcesses = new ArrayList<>();
        ProcessTestHelper.registerProcessEventListener(app, new DefaultKogitoProcessEventListener() {
            @Override
            public void beforeProcessStarted(org.kie.api.event.process.ProcessStartedEvent event) {
                if (!event.getProcessInstance().getProcessId().contains("Emit")) { //we don't want thrower process to be saved
                    startedProcesses.add(event.getProcessInstance());
                }
            }
        });

        // Create process that waits for signal to start instance
        Process<SignalScopeStartEventModel> eventStartedProcess = SignalScopeStartEventProcess.newProcess(app);

        // Create thrower process instance that will send signal with processInstance scope. It completes immediately.
        Process<SignalScopeEmitProcessInstanceScopeSignalModel> throwerProcess = SignalScopeEmitProcessInstanceScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProcessInstanceScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // No process instance should be started because of processInstance signal scope
        assertThat(startedProcesses).hasSize(0);
    }

    // ========================================
    // MULTIPLE INSTANCES
    // ========================================

    /**
     * Verify that broadcast signaling with default scope reaches multiple process instances.
     */
    @Test
    public void testDefaultScopeBroadcastsToMultipleInstances() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        Process<SignalScopeCatchSignalBoundaryEventModel> process =
                SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);

        // Start THREE instances - all should wait for the signal
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance1 = process.createInstance(process.createModel());
        instance1.start();
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance2 = process.createInstance(process.createModel());
        instance2.start();
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance3 = process.createInstance(process.createModel());
        instance3.start();

        assertThat(instance1.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(instance2.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(instance3.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(3);

        // Create thrower process instance that will send signal with project scope.
        Process<SignalScopeEmitDefaultScopeSignalModel> throwerProcess = SignalScopeEmitDefaultScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitDefaultScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // All three instances should complete
        assertInstanceWasCompleted(app, instance1.id());
        assertInstanceWasCompleted(app, instance2.id());
        assertInstanceWasCompleted(app, instance3.id());
    }

    /**
     * Verify that broadcast signaling with project scope reaches multiple process instances.
     */
    @Test
    public void testProjectScopeBroadcastsToMultipleInstances() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        Process<SignalScopeCatchSignalBoundaryEventModel> process =
                SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);

        // Start THREE instances - all should wait for the signal
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance1 = process.createInstance(process.createModel());
        instance1.start();
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance2 = process.createInstance(process.createModel());
        instance2.start();
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance3 = process.createInstance(process.createModel());
        instance3.start();

        assertThat(instance1.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(instance2.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(instance3.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(3);

        // Create thrower process instance that will send signal with project scope.
        Process<SignalScopeEmitProjectScopeSignalModel> throwerProcess = SignalScopeEmitProjectScopeSignalProcess.newProcess(app);
        ProcessInstance<SignalScopeEmitProjectScopeSignalModel> throwerInstance = throwerProcess.createInstance(throwerProcess.createModel());
        throwerInstance.start();
        assertThat(throwerInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // All three instances should complete
        assertInstanceWasCompleted(app, instance1.id());
        assertInstanceWasCompleted(app, instance2.id());
        assertInstanceWasCompleted(app, instance3.id());
    }

    /**
     * Negative test: Verify that processInstance scope does NOT reach other instances.
     */
    @Test
    public void testProcessInstanceScopeDoesNotReachOtherInstances() {
        Application app = ProcessTestHelper.newApplication();
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ProcessTestHelper.registerHandler(app, "Human Task", handler);

        Process<SignalScopeCatchSignalBoundaryEventModel> process = SignalScopeCatchSignalBoundaryEventProcess.newProcess(app);

        // Start TWO instances
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance1 = process.createInstance(process.createModel());
        instance1.start();
        ProcessInstance<SignalScopeCatchSignalBoundaryEventModel> instance2 = process.createInstance(process.createModel());
        instance2.start();

        assertThat(instance1.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(instance2.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        assertThat(handler.getWorkItems()).hasSize(2);

        // Send signal to instance1 only (sending from ProcessInstance => process instance scope)- should NOT affect instance2
        instance1.send(SignalFactory.of("testSignal", "instance-boundary-data"));

        // Only instance1 should complete, instance2 should remain active
        assertThat(instance1.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(instance2.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }

    /**
     * Helper method to check if already removed instance was completed
     * 
     * @param app Application context
     * @param pid ID of the process instance to check
     */
    private static void assertInstanceWasCompleted(Application app, String pid) {
        assertThat(ProcessTestHelper.findRemovedInstance(app, pid))
                .isPresent()
                .get()
                .extracting(WorkflowProcessInstance::getState)
                .isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
