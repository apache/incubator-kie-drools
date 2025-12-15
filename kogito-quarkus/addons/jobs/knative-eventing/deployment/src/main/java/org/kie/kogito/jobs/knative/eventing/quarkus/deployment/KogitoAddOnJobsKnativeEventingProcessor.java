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
package org.kie.kogito.jobs.knative.eventing.quarkus.deployment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.node.AsyncEventNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.kie.api.definition.process.Node;
import org.kie.kogito.addons.quarkus.knative.eventing.deployment.KogitoCloudEventsBuildItem;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerator;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.OneOfCapabilityKogitoAddOnProcessor;
import org.kie.kogito.quarkus.extensions.spi.deployment.HasWorkflowExtension;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;

import io.quarkus.deployment.IsTest;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

public class KogitoAddOnJobsKnativeEventingProcessor extends OneOfCapabilityKogitoAddOnProcessor {

    static final String FEATURE = "kogito-addon-jobs-knative-eventing-extension";

    static final String NOT_STARTED_NOTIFY_PARAMETER = "NotStartedNotify";
    static final String NOT_COMPLETED_NOTIFY_PARAMETER = "NotCompletedNotify";
    static final String NOT_STARTED_REASSIGN_PARAMETER = "NotStartedReassign";
    static final String NOT_COMPLETED_REASSIGN_PARAMETER = "NotCompletedReassign";

    KogitoAddOnJobsKnativeEventingProcessor() {
        super(KogitoCapability.PROCESSES, KogitoCapability.SERVERLESS_WORKFLOW);
    }

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public ReflectiveClassBuildItem jobsApiReflection() {
        List<Class<?>> reflectiveClasses = new ArrayList<>();
        reflectiveClasses.addAll(org.kie.kogito.jobs.api.utils.ReflectionUtils.apiReflectiveClasses());
        reflectiveClasses.addAll(org.kie.kogito.jobs.service.api.utils.ReflectionUtils.apiReflectiveClasses());
        return ReflectiveClassBuildItem.builder(reflectiveClasses.toArray(new Class[] {}))
                .constructors()
                .fields()
                .build();
    }

    @BuildStep(onlyIfNot = IsTest.class, onlyIf = HasWorkflowExtension.class)
    public void buildCloudEventsMetadata(List<KogitoProcessContainerGeneratorBuildItem> processContainerBuildItem,
            BuildProducer<KogitoCloudEventsBuildItem> cloudEventsBuildItemProducer) {
        final Set<CloudEventMeta> cloudEvents = new LinkedHashSet<>();
        processContainerBuildItem.stream().flatMap(it -> it.getProcessContainerGenerators().stream())
                .flatMap(processContainerGenerator -> processContainerGenerator.getProcesses().stream())
                .map(ProcessGenerator::getProcessExecutable)
                .map(ProcessExecutableModelGenerator::process)
                .filter(KogitoAddOnJobsKnativeEventingProcessor::produceJobEvents)
                .forEach(process -> {
                    String eventSource = "/process/" + process.getId();
                    cloudEvents.add(new CloudEventMeta(CreateJobEvent.TYPE, eventSource, EventKind.PRODUCED));
                    cloudEvents.add(new CloudEventMeta(DeleteJobEvent.TYPE, eventSource, EventKind.PRODUCED));
                });
        if (!cloudEvents.isEmpty()) {
            cloudEventsBuildItemProducer.produce(new KogitoCloudEventsBuildItem(cloudEvents));
        }
    }

    private static boolean produceJobEvents(KogitoWorkflowProcess process) {
        if (hasSlaDueDate(process.getMetaData()) || hasProcessDuration(process.getMetaData())) {
            return true;
        }
        return process.getNodesRecursively().stream().anyMatch(KogitoAddOnJobsKnativeEventingProcessor::produceJobEvents);
    }

    private static boolean produceJobEvents(Node node) {
        if (node instanceof TimerNode) {
            return true;
        }
        if (node instanceof AsyncEventNode) {
            return true;
        }
        if (node instanceof EventNode) {
            return hasSlaDueDate(node.getMetaData());
        }
        if (node instanceof HumanTaskNode && hasDeadlines((HumanTaskNode) node)) {
            return true;
        }
        if (node instanceof StateBasedNode) {
            Map<?, ?> timers = ((StateBasedNode) node).getTimers();
            return (timers != null && !timers.isEmpty()) || hasSlaDueDate(node.getMetaData());
        }
        return false;
    }

    private static boolean hasSlaDueDate(Map<?, ?> metadata) {
        return hasMetadataAttribute(metadata, Metadata.CUSTOM_SLA_DUE_DATE);
    }

    private static boolean hasProcessDuration(Map<?, ?> metadata) {
        return hasMetadataAttribute(metadata, Metadata.PROCESS_DURATION);
    }

    private static boolean hasDeadlines(HumanTaskNode node) {
        return hasWorkParameter(node, NOT_STARTED_NOTIFY_PARAMETER) ||
                hasWorkParameter(node, NOT_COMPLETED_NOTIFY_PARAMETER) ||
                hasWorkParameter(node, NOT_STARTED_REASSIGN_PARAMETER) ||
                hasWorkParameter(node, NOT_COMPLETED_REASSIGN_PARAMETER);
    }

    private static boolean hasMetadataAttribute(Map<?, ?> metadata, String attributeName) {
        String value = (String) metadata.get(attributeName);
        return value != null && !value.isEmpty();
    }

    private static boolean hasWorkParameter(HumanTaskNode node, String parameterName) {
        return node.getWork().getParameter(parameterName) != null;
    }
}
