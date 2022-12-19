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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.TimerNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.addons.quarkus.knative.eventing.deployment.KogitoCloudEventsBuildItem;
import org.kie.kogito.codegen.process.ProcessContainerGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerator;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.api.event.ProcessInstanceContextJobCloudEvent;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.knative.eventing.quarkus.deployment.KogitoAddOnJobsKnativeEventingProcessor.FEATURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KogitoAddOnJobsKnativeEventingProcessorTest {

    private static final String PROCESS1_ID = "PROCESS1_ID";
    private static final String PROCESS2_ID = "PROCESS2_ID";
    private static final String PROCESS3_ID = "PROCESS3_ID";
    private static final String PROCESS4_ID = "PROCESS4_ID";

    private static final KogitoWorkflowProcess PROCESS1 = mockProcess1();
    private static final KogitoWorkflowProcess PROCESS2 = mockProcess2();
    private static final KogitoWorkflowProcess PROCESS3 = mockProcess3();
    private static final KogitoWorkflowProcess PROCESS4 = mockProcess4();

    @Mock
    private BuildProducer<KogitoCloudEventsBuildItem> buildItemProducer;

    @Captor
    private ArgumentCaptor<KogitoCloudEventsBuildItem> cloudEventsBuildItemCaptor;

    @Test
    void buildCloudEventsMetadataWithEvents() {
        ProcessGenerator processGenerator1 = mockProcessGenerator(PROCESS1);
        ProcessGenerator processGenerator2 = mockProcessGenerator(PROCESS2);
        ProcessGenerator processGenerator3 = mockProcessGenerator(PROCESS3);
        ProcessGenerator processGenerator4 = mockProcessGenerator(PROCESS4);

        ProcessContainerGenerator containerGenerator1 = mockProcessContainerGenerator(processGenerator1, processGenerator2);
        ProcessContainerGenerator containerGenerator2 = mockProcessContainerGenerator(processGenerator3);
        ProcessContainerGenerator containerGenerator3 = mockProcessContainerGenerator(processGenerator4);

        Set<ProcessContainerGenerator> containerGenerators = Set.of(containerGenerator1, containerGenerator2, containerGenerator3);
        KogitoProcessContainerGeneratorBuildItem kogitoProcessContainerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(containerGenerators);

        KogitoAddOnJobsKnativeEventingProcessor processor = new KogitoAddOnJobsKnativeEventingProcessor();
        processor.buildCloudEventsMetadata(singletonList(kogitoProcessContainerGeneratorBuildItem), buildItemProducer);
        verify(buildItemProducer).produce(cloudEventsBuildItemCaptor.capture());
        KogitoCloudEventsBuildItem kogitoCloudEventsBuildItem = cloudEventsBuildItemCaptor.getValue();
        assertThat(kogitoCloudEventsBuildItem).isNotNull();
        assertThat(kogitoCloudEventsBuildItem.getCloudEvents()).hasSize(4);
        assertProcessWasIncluded(PROCESS1_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasNotIncluded(PROCESS2_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS3_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasNotIncluded(PROCESS4_ID, kogitoCloudEventsBuildItem.getCloudEvents());
    }

    @Test
    void buildCloudEventsMetadataWithoutEvents() {
        ProcessGenerator processGenerator1 = mockProcessGenerator(PROCESS2);
        ProcessGenerator processGenerator2 = mockProcessGenerator(PROCESS4);

        ProcessContainerGenerator containerGenerator1 = mockProcessContainerGenerator(processGenerator1);
        ProcessContainerGenerator containerGenerator2 = mockProcessContainerGenerator(processGenerator2);

        Set<ProcessContainerGenerator> containerGenerators = Set.of(containerGenerator1, containerGenerator2);
        KogitoProcessContainerGeneratorBuildItem kogitoProcessContainerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(containerGenerators);

        KogitoAddOnJobsKnativeEventingProcessor processor = new KogitoAddOnJobsKnativeEventingProcessor();
        processor.buildCloudEventsMetadata(singletonList(kogitoProcessContainerGeneratorBuildItem), buildItemProducer);
        verify(buildItemProducer, never()).produce(any());
    }

    @Test
    void eventsApiReflection() {
        ReflectiveClassBuildItem reflectiveClassBuildItem = new KogitoAddOnJobsKnativeEventingProcessor().eventsApiReflection();
        assertThat(reflectiveClassBuildItem.getClassNames())
                .hasSize(5)
                .containsExactlyInAnyOrder(JobCloudEvent.class.getName(),
                        ProcessInstanceContextJobCloudEvent.class.getName(),
                        CreateProcessInstanceJobRequestEvent.class.getName(),
                        CancelJobRequestEvent.class.getName(),
                        CancelJobRequestEvent.JobId.class.getName());
    }

    @Test
    void featureBuildItem() {
        FeatureBuildItem featureBuildItem = new KogitoAddOnJobsKnativeEventingProcessor().feature();
        assertThat(featureBuildItem).isNotNull();
        assertThat(featureBuildItem.getName()).isEqualTo(FEATURE);
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess1() {
        Node startNode = new StartNode();
        Node timerNode = new TimerNode();
        timerNode.setId(1);
        Node endNode = new EndNode();
        return mockProcess(PROCESS1_ID, startNode, timerNode, endNode);
    }

    /**
     * No jobs service events are produced by this process.
     */
    private static KogitoWorkflowProcess mockProcess2() {
        Node startNode = new StartNode();
        Node humanTaskNode = new HumanTaskNode();
        Node scriptNode = new ActionNode();
        Node endNode = new EndNode();
        return mockProcess(PROCESS2_ID, startNode, humanTaskNode, scriptNode, endNode);
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess3() {
        Node startNode = new StartNode();
        Node timerNode = new TimerNode();
        timerNode.setId(1);
        Node endNode = new EndNode();
        return mockProcess(PROCESS3_ID, startNode, timerNode, endNode);
    }

    /**
     * No jobs service events are produced by this process.
     */
    private static KogitoWorkflowProcess mockProcess4() {
        Node startNode = new StartNode();
        Node scriptNode = new ActionNode();
        Node endNode = new EndNode();
        return mockProcess(PROCESS4_ID, startNode, scriptNode, endNode);
    }

    private static void assertProcessWasIncluded(String processId, Set<? extends CloudEventMeta> cloudEvents) {
        assertThat(findCloudEventMeta(cloudEvents, CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST, "/process/" + processId, EventKind.PRODUCED)).isTrue();
        assertThat(findCloudEventMeta(cloudEvents, CancelJobRequestEvent.CANCEL_JOB_REQUEST, "/process/" + processId, EventKind.PRODUCED)).isTrue();
    }

    private static void assertProcessWasNotIncluded(String processId, Set<? extends CloudEventMeta> cloudEvents) {
        assertThat(findCloudEventMeta(cloudEvents, CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST, "/process/" + processId, EventKind.PRODUCED)).isFalse();
        assertThat(findCloudEventMeta(cloudEvents, CancelJobRequestEvent.CANCEL_JOB_REQUEST, "/process/" + processId, EventKind.PRODUCED)).isFalse();
    }

    private static boolean findCloudEventMeta(Set<? extends CloudEventMeta> cloudEvents, String type, String source, EventKind kind) {
        return cloudEvents.stream().anyMatch(cloudEvent -> type.equals(cloudEvent.getType()) &&
                source.equals(cloudEvent.getSource()) &&
                kind == cloudEvent.getKind());
    }

    private static ProcessContainerGenerator mockProcessContainerGenerator(ProcessGenerator... processGenerators) {
        ProcessContainerGenerator processContainerGenerator = mock(ProcessContainerGenerator.class);
        List<ProcessGenerator> processes = Arrays.asList(processGenerators);
        doReturn(processes).when(processContainerGenerator).getProcesses();
        return processContainerGenerator;
    }

    private static ProcessGenerator mockProcessGenerator(KogitoWorkflowProcess process) {
        ProcessGenerator processGenerator = mock(ProcessGenerator.class);
        ProcessExecutableModelGenerator processExecutableModelGenerator = mock(ProcessExecutableModelGenerator.class);
        doReturn(process).when(processExecutableModelGenerator).process();
        doReturn(processExecutableModelGenerator).when(processGenerator).getProcessExecutable();
        return processGenerator;
    }

    private static KogitoWorkflowProcess mockProcess(String processId, Node... nodes) {
        KogitoWorkflowProcess process = mock(KogitoWorkflowProcess.class);
        lenient().doReturn(processId).when(process).getId();
        List<Node> nodeList = Arrays.asList(nodes);
        doReturn(nodeList).when(process).getNodesRecursively();
        return process;
    }
}
