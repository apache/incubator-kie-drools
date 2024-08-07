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
package org.kie.kogito.monitoring.core.common.process;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.api.event.process.ErrorEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.internal.utils.KogitoTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Counter.Builder;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

public class MetricsProcessEventListener extends DefaultKogitoProcessEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsProcessEventListener.class);
    private static Map<String, AtomicInteger> gaugeMap = new ConcurrentHashMap<>();
    private final String identifier;
    private final KogitoGAV gav;
    private final MeterRegistry meterRegistry;

    public MetricsProcessEventListener(String identifier, KogitoGAV gav, MeterRegistry meterRegistry) {
        this.identifier = identifier;
        this.gav = gav;
        this.meterRegistry = meterRegistry;
    }

    protected Counter buildCounter(String name, String description, String processId, Tag... tags) {
        Builder builder = Counter.builder(name)
                .description(description)
                .tag("app_id", identifier).tag("process_id", processId).tag("artifactId", gav.getArtifactId()).tag("version", gav.getVersion());
        for (Tag tag : tags) {
            builder.tag(tag.getKey(), tag.getValue());
        }
        return builder.register(meterRegistry);
    }

    protected AtomicInteger buildGauge(String name, String description, String processId, Tag... tags) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        io.micrometer.core.instrument.Gauge.Builder<AtomicInteger> builder = Gauge.builder(name, atomicInteger, AtomicInteger::doubleValue)
                .description(description)
                .tag("app_id", identifier).tag("process_id", processId).tag("artifactId", gav.getArtifactId()).tag("version", gav.getVersion());
        for (Tag tag : tags) {
            builder.tag(tag.getKey(), tag.getValue());
        }
        builder.register(meterRegistry);
        return atomicInteger;
    }

    protected DistributionSummary buildDistributionSummary(String name, String description, Tag... tags) {
        io.micrometer.core.instrument.DistributionSummary.Builder builder = DistributionSummary.builder(name)
                .description(description).tag("artifactId", gav.getArtifactId()).tag("version", gav.getVersion());
        for (Tag tag : tags) {
            builder.tag(tag.getKey(), tag.getValue());
        }
        return builder.register(meterRegistry);
    }

    private Counter getNumberOfProcessInstancesStartedCounter(String processId) {
        return buildCounter("kogito_process_instance_started_total", "Started Process Instances", processId);
    }

    private Counter getErrorCounter(String processId, String errorMessage) {
        return buildCounter("kogito_process_instance_error", "Number of errors that has occurred", processId, Tag.of("error_message", errorMessage));
    }

    private Counter getNumberOfSLAsViolatedCounter(String processId, String nodeName) {
        return buildCounter("kogito_process_instance_sla_violated_total", "Number of SLA violations that has ocurred", processId, Tag.of("node_name", nodeName));
    }

    private Counter getNumberOfProcessInstancesCompletedCounter(String processId, String state) {
        return buildCounter("kogito_process_instance_completed_total", "Completed Process Instances", processId, Tag.of("process_state", state));
    }

    private AtomicInteger getRunningProcessInstancesGauge(String processId) {
        return gaugeMap.computeIfAbsent(identifier + processId, k -> buildGauge("kogito_process_instance_running_total", "Running Process Instances", processId));
    }

    private DistributionSummary getProcessInstancesDurationSummary(String processId) {
        return buildDistributionSummary("kogito_process_instance_duration_seconds",
                "Process Instances Duration", Tag.of("process_id", processId), Tag.of("app_id", identifier));
    }

    private DistributionSummary getWorkItemsDurationSummary(String name) {
        return buildDistributionSummary("kogito_work_item_duration_seconds",
                "Work Items Duration", Tag.of("name", name));
    }

    private DistributionSummary getNodeInstancesDurationSummary(String processId, String nodeName) {
        return buildDistributionSummary("kogito_node_instance_duration_milliseconds", "Relevant nodes duration in milliseconds", Tag.of("process_id", processId),
                Tag.of("node_name", nodeName));
    }

    protected void recordRunningProcessInstance(String processId) {
        getRunningProcessInstancesGauge(processId).incrementAndGet();
    }

    protected static double millisToSeconds(long millis) {
        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent event) {
        LOGGER.debug("After process started event: {}", event);
        final ProcessInstance processInstance = event.getProcessInstance();
        getNumberOfProcessInstancesStartedCounter(processInstance.getProcessId()).increment();
        recordRunningProcessInstance(processInstance.getProcessId());
    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent event) {
        LOGGER.debug("After process completed event: {}", event);
        final KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        getRunningProcessInstancesGauge(processInstance.getProcessId()).decrementAndGet();

        getNumberOfProcessInstancesCompletedCounter(processInstance.getProcessId(), fromState(processInstance.getState())).increment();

        if (processInstance.getStartDate() != null) {
            final double duration = millisToSeconds(processInstance.getEndDate().getTime() - processInstance.getStartDate().getTime());
            getProcessInstancesDurationSummary(processInstance.getProcessId()).record(duration);
            LOGGER.debug("Process Instance duration: {}s", duration);
        }
    }

    @Override
    public void onError(ErrorEvent event) {
        LOGGER.debug("After Error event: {}", event);
        final KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) event.getProcessInstance();
        getErrorCounter(processInstance.getProcessId(), processInstance.getErrorMessage()).increment();
    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        LOGGER.debug("Before Node left event: {}", event);
        final KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
        if (nodeInstance instanceof KogitoWorkItemNodeInstance) {
            KogitoWorkItemNodeInstance wi = (KogitoWorkItemNodeInstance) nodeInstance;
            recordNodeDuration(getWorkItemsDurationSummary((String) wi.getWorkItem().getParameters().getOrDefault("TaskName", wi.getWorkItem().getName())), nodeInstance, TimeUnit.SECONDS);
        }
        String nodeName = (String) nodeInstance.getNode().getMetaData().get(KogitoTags.METRIC_NAME_METADATA);
        if (nodeName != null) {
            recordNodeDuration(getNodeInstancesDurationSummary(event.getProcessInstance().getProcessId(), nodeName), nodeInstance, TimeUnit.MILLISECONDS);
        }
    }

    private void recordNodeDuration(DistributionSummary summary, KogitoNodeInstance instance, TimeUnit target) {
        if (instance.getTriggerTime() != null) {
            double duration = target.convert(instance.getLeaveTime().getTime() - instance.getTriggerTime().getTime(), TimeUnit.MILLISECONDS);
            summary.record(duration);
            LOGGER.debug("Recorded {} {} because of node {} for summary {}", duration, target, instance.getNode().getName(), summary.getId().getName());
        }
    }

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {
        LOGGER.debug("After SLA violated event: {}", event);
        final ProcessInstance processInstance = event.getProcessInstance();
        if (processInstance != null && event.getNodeInstance() != null) {
            getNumberOfSLAsViolatedCounter(processInstance.getProcessId(), event.getNodeInstance().getNodeName()).increment();
        }
    }

    private static String fromState(int state) {
        switch (state) {
            case KogitoProcessInstance.STATE_ABORTED:
                return "Aborted";
            case KogitoProcessInstance.STATE_COMPLETED:
                return "Completed";
            case KogitoProcessInstance.STATE_ERROR:
                return "Error";
            default:
            case KogitoProcessInstance.STATE_ACTIVE:
                return "Active";
        }
    }

}
