/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.monitoring.core.common.process;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public class MetricsProcessEventListener extends DefaultKogitoProcessEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsProcessEventListener.class);
    private static Map<String, AtomicInteger> gaugeMap = new HashMap<>();
    private final String identifier;
    private final KogitoGAV gav;
    private final MeterRegistry meterRegistry;

    public MetricsProcessEventListener(String identifier, KogitoGAV gav, MeterRegistry meterRegistry) {
        this.identifier = identifier;
        this.gav = gav;
        this.meterRegistry = meterRegistry;
    }

    private Counter getNumberOfProcessInstancesStartedCounter(String appId, String processId) {
        return Counter
                .builder("kogito_process_instance_started_total")
                .description("Started Process Instances")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId), Tag.of("artifactId", gav.getArtifactId()), Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
    }

    private Counter getNumberOfSLAsViolatedCounter(String appId, String processId, String nodeName) {
        return Counter
                .builder("kogito_process_instance_sla_violated_total")
                .description("Process Instances SLA Violated")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId), Tag.of("node_name", nodeName), Tag.of("artifactId", gav.getArtifactId()),
                        Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
    }

    private Counter getNumberOfProcessInstancesCompletedCounter(String appId, String processId, String nodeName) {
        return Counter
                .builder("kogito_process_instance_completed_total")
                .description("Completed Process Instances")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId), Tag.of("node_name", nodeName), Tag.of("artifactId", gav.getArtifactId()),
                        Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
    }

    private AtomicInteger getRunningProcessInstancesGauge(String appId, String processId) {
        if (gaugeMap.containsKey(appId + processId)) {
            return gaugeMap.get(appId + processId);
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Gauge.builder("kogito_process_instance_running_total", atomicInteger, AtomicInteger::doubleValue)
                .description("Running Process Instances")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId), Tag.of("artifactId", gav.getArtifactId()), Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
        gaugeMap.put(appId + processId, atomicInteger);
        return atomicInteger;
    }

    private DistributionSummary getProcessInstancesDurationSummary(String appId, String processId) {
        return DistributionSummary.builder("kogito_process_instance_duration_seconds")
                .description("Process Instances Duration")
                .tags(Arrays.asList(Tag.of("app_id", appId), Tag.of("process_id", processId), Tag.of("artifactId", gav.getArtifactId()), Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
    }

    private DistributionSummary getWorkItemsDurationSummary(String name) {
        return DistributionSummary.builder("kogito_work_item_duration_seconds")
                .description("Work Items Duration")
                .tags(Arrays.asList(Tag.of("name", name), Tag.of("artifactId", gav.getArtifactId()), Tag.of("version", gav.getVersion())))
                .register(meterRegistry);
    }

    protected void recordRunningProcessInstance(String containerId, String processId) {
        getRunningProcessInstancesGauge(containerId, processId).incrementAndGet();
    }

    protected static double millisToSeconds(long millis) {
        return millis / 1000.0;
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        LOGGER.debug("After process started event: {}", event);
        final ProcessInstance processInstance = event.getProcessInstance();
        getNumberOfProcessInstancesStartedCounter(identifier, processInstance.getProcessId()).increment();
        recordRunningProcessInstance(identifier, processInstance.getProcessId());
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.debug("After process completed event: {}", event);
        final KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        getRunningProcessInstancesGauge(identifier, processInstance.getProcessId()).decrementAndGet();

        getNumberOfProcessInstancesCompletedCounter(identifier, processInstance.getProcessId(), String.valueOf(processInstance.getState())).increment();

        if (processInstance.getStartDate() != null) {
            final double duration = millisToSeconds(processInstance.getEndDate().getTime() - processInstance.getStartDate().getTime());
            getProcessInstancesDurationSummary(identifier, processInstance.getProcessId()).record(duration);
            LOGGER.debug("Process Instance duration: {}s", duration);
        }
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.debug("Before Node left event: {}", event);
        final KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
        if (nodeInstance instanceof KogitoWorkItemNodeInstance) {
            KogitoWorkItemNodeInstance wi = (KogitoWorkItemNodeInstance) nodeInstance;
            if (wi.getTriggerTime() != null) {
                final String name = (String) wi.getWorkItem().getParameters().getOrDefault("TaskName", wi.getWorkItem().getName());
                final double duration = millisToSeconds(wi.getLeaveTime().getTime() - wi.getTriggerTime().getTime());
                getWorkItemsDurationSummary(name).record(duration);
                LOGGER.debug("Work Item {}, duration: {}s", name, duration);
            }
        }
    }

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
        LOGGER.debug("After SLA violated event: {}", event);
        final ProcessInstance processInstance = event.getProcessInstance();
        if (processInstance != null && event.getNodeInstance() != null) {
            getNumberOfSLAsViolatedCounter(identifier, processInstance.getProcessId(), event.getNodeInstance().getNodeName()).increment();
        }
    }
}
