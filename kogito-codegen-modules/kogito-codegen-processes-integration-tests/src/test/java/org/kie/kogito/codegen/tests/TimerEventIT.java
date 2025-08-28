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

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.ProcessCompletedCountDownProcessEventListener;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertSize;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.getFirst;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class TimerEventIT extends AbstractCodegenIT {

    private static final int TIME_OUT = 500000;

    @Test
    void testIntermediateCycleTimerEvent() throws Exception {

        Application app = generateCodeProcessesOnly("timer/IntermediateCatchEventTimerCycleISO.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("timer", 3);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        boolean completed = listener.waitTillCompleted(TIME_OUT);
        assertThat(completed).isTrue();

        assertThat(processInstance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        processInstance.abort();

        assertThat(processInstance.status()).isEqualTo(KogitoProcessInstance.STATE_ABORTED);
    }

    @Test
    void testIntermediateDurationTimerEvent() throws Exception {

        Application app = generateCodeProcessesOnly("timer/IntermediateCatchEventTimerDurationISO.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("timer", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();

        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testIntermediateDateTimerEvent() throws Exception {

        Application app = generateCodeProcessesOnly("timer/IntermediateCatchEventTimerDateISO.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("timer", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("IntermediateCatchEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        parameters.put("date", plusTwoSeconds.toString());
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();
        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testBoundaryDurationTimerEventOnTask() throws Exception {

        Application app = generateCodeProcessesOnly("timer/TimerBoundaryEventDurationISOOnTask.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("TimerBoundaryEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();
        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testBoundaryCycleTimerEventOnTask() throws Exception {

        Application app = generateCodeProcessesOnly("timer/TimerBoundaryEventCycleISOOnTask.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("TimerBoundaryEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();
        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testBoundaryDateTimerEventOnTask() throws Exception {

        Application app = generateCodeProcessesOnly("timer/TimerBoundaryEventDateISOOnTask.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("TimerBoundaryEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        parameters.put("date", plusTwoSeconds.toString());
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();
        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testBoundaryDurationTimerEventOnSubProcess() throws Exception {

        Application app = generateCodeProcessesOnly("timer/TimerBoundaryEventDurationISO.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        ProcessCompletedCountDownProcessEventListener processEventListener = new ProcessCompletedCountDownProcessEventListener();
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(processEventListener);

        Process<? extends Model> p = app.get(Processes.class).processById("TimerBoundaryEvent");

        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(listener.waitTillCompleted(TIME_OUT)).isTrue();
        assertThat(processEventListener.waitTillCompleted(TIME_OUT)).isTrue();
        await().until(() -> p.instances().stream().count() == 0);
    }

    @Test
    void testStartTimerEvent() throws Exception {

        Application app = generateCodeProcessesOnly("timer/StartTimerDuration.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("timer fired", 1);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);

        Process<? extends Model> p = app.get(Processes.class).processById("defaultPackage.TimerProcess");
        // activate to schedule timers
        activate(app, p);

        boolean completed = listener.waitTillCompleted(TIME_OUT);
        assertThat(completed).isTrue();

        await().atMost(TIME_OUT, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertSize(p.instances(), ProcessInstanceReadMode.MUTABLE, 1));

        ProcessInstance<?> processInstance = getFirst(p.instances());
        assertThat(processInstance).isNotNull();

        assertThat(processInstance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        processInstance.abort();
        assertThat(processInstance.status()).isEqualTo(KogitoProcessInstance.STATE_ABORTED);

        await().until(() -> p.instances().stream().count() == 0);
    }

    private static void activate(Application app, Process<? extends Model> p) {
        UnitOfWorkExecutor.executeInUnitOfWork(app.unitOfWorkManager(), () -> {
            p.activate();
            return null;
        });
    }

    @Test
    void testStartTimerEventTimeCycle() throws Exception {
        Application app = generateCodeProcessesOnly("timer/StartTimerCycle.bpmn2");
        assertThat(app).isNotNull();

        NodeLeftCountDownProcessEventListener listener = new NodeLeftCountDownProcessEventListener("timer fired", 2);
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(listener);

        Process<? extends Model> p = app.get(Processes.class).processById("defaultPackage.TimerProcess");
        // activate to schedule timers
        activate(app, p);

        boolean completed = listener.waitTillCompleted(TIME_OUT);
        assertThat(completed).isTrue();

        await().atMost(TIME_OUT, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertSize(p.instances(), ProcessInstanceReadMode.MUTABLE, 2));

        ProcessInstance<?> processInstance = getFirst(p.instances());
        assertThat(processInstance).isNotNull();

        assertThat(processInstance.status()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);
        // deactivate to cancel timer, so there should be no more timers fired
        p.deactivate();

        // reset the listener to make sure nothing more is triggered
        listener.reset(1);
        completed = listener.waitTillCompleted(3000);
        assertThat(completed).isFalse();
        // same amount of instances should be active as before deactivation

        assertSize(p.instances(), ProcessInstanceReadMode.MUTABLE, 2);
    }
}
