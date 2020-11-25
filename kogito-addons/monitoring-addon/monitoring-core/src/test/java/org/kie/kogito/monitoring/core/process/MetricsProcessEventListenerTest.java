/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.core.process;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.monitoring.core.MonitoringRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricsProcessEventListenerTest {

    MeterRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        MonitoringRegistry.addRegistry(registry);
    }

    @AfterEach
    public void cleanUp() {
        MonitoringRegistry.getDefaultMeterRegistry().remove(registry);
    }

    @Test
    public void testGaugeRunningProcesses() {
        // Arrange
        MetricsProcessEventListener eventListener = new MetricsProcessEventListener("myId");
        ProcessInstance processInstanceMock = mock(WorkflowProcessInstanceImpl.class);
        when(processInstanceMock.getProcessId()).thenReturn("myProcessId");

        ProcessStartedEvent processStartedEvent = mock(ProcessStartedEvent.class);
        when(processStartedEvent.getProcessInstance()).thenReturn(processInstanceMock);

        // Act
        eventListener.afterProcessStarted(processStartedEvent);
        eventListener.afterProcessStarted(processStartedEvent);

        // Assert
        assertEquals(2, registry.find("kie_process_instance_running_total")
                .gauge()
                .value());
    }
}
