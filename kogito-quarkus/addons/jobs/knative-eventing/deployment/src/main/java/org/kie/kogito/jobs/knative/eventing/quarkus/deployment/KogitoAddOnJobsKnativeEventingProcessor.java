/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.knative.eventing.quarkus.deployment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.workflow.core.node.TimerNode;
import org.kie.kogito.addons.quarkus.knative.eventing.deployment.KogitoCloudEventsBuildItem;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerator;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.api.event.ProcessInstanceContextJobCloudEvent;
import org.kie.kogito.quarkus.addons.common.deployment.KogitoCapability;
import org.kie.kogito.quarkus.addons.common.deployment.RequireCapabilityKogitoAddOnProcessor;
import org.kie.kogito.quarkus.extensions.spi.deployment.HasProcessExtension;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;

import io.quarkus.deployment.IsTest;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

public class KogitoAddOnJobsKnativeEventingProcessor extends RequireCapabilityKogitoAddOnProcessor {

    static final String FEATURE = "kogito-addon-jobs-knative-eventing-extension";

    KogitoAddOnJobsKnativeEventingProcessor() {
        super(KogitoCapability.PROCESSES);
    }

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public ReflectiveClassBuildItem eventsApiReflection() {
        return new ReflectiveClassBuildItem(true,
                true,
                true,
                JobCloudEvent.class.getName(),
                ProcessInstanceContextJobCloudEvent.class.getName(),
                CreateProcessInstanceJobRequestEvent.class.getName(),
                CancelJobRequestEvent.class.getName(),
                CancelJobRequestEvent.JobId.class.getName());
    }

    @BuildStep(onlyIfNot = IsTest.class, onlyIf = HasProcessExtension.class)
    public void buildCloudEventsMetadata(KogitoProcessContainerGeneratorBuildItem processContainerBuildItem,
            BuildProducer<KogitoCloudEventsBuildItem> cloudEventsBuildItemProducer) {
        final Set<CloudEventMeta> cloudEvents = new LinkedHashSet<>();
        List<KogitoWorkflowProcess> processes = processContainerBuildItem.getProcessContainerGenerators().stream()
                .flatMap(processContainerGenerator -> processContainerGenerator.getProcesses().stream())
                .map(ProcessGenerator::getProcessExecutable)
                .map(ProcessExecutableModelGenerator::process)
                .collect(Collectors.toList());

        for (KogitoWorkflowProcess process : processes) {
            process.getNodesRecursively().stream()
                    .filter(TimerNode.class::isInstance)
                    .map(TimerNode.class::cast)
                    .forEach(timer -> {
                        String eventSource = "/process/" + process.getId();
                        cloudEvents.add(new CloudEventMeta(CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST, eventSource, EventKind.PRODUCED));
                        cloudEvents.add(new CloudEventMeta(CancelJobRequestEvent.CANCEL_JOB_REQUEST, eventSource, EventKind.PRODUCED));
                    });
        }
        if (!cloudEvents.isEmpty()) {
            cloudEventsBuildItemProducer.produce(new KogitoCloudEventsBuildItem(cloudEvents));
        }
    }
}
