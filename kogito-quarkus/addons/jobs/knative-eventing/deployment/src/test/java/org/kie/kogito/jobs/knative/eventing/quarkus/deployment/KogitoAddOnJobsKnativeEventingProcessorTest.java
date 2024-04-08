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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.AsyncEventNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateBasedNode;
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
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
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
import static org.kie.kogito.jobs.knative.eventing.quarkus.deployment.KogitoAddOnJobsKnativeEventingProcessor.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KogitoAddOnJobsKnativeEventingProcessorTest {

    private static final String DURATION = "PT15S";

    private static final String PROCESS1_ID = "PROCESS1_ID";
    private static final String PROCESS2_ID = "PROCESS2_ID";
    private static final String PROCESS3_ID = "PROCESS3_ID";
    private static final String PROCESS4_ID = "PROCESS4_ID";
    private static final String PROCESS5_ID = "PROCESS5_ID";
    private static final String PROCESS6_ID = "PROCESS6_ID";
    private static final String PROCESS7_ID = "PROCESS7_ID";
    private static final String PROCESS8_ID = "PROCESS8_ID";
    private static final String PROCESS9_ID = "PROCESS9_ID";
    private static final String PROCESS10_ID = "PROCESS10_ID";
    private static final String PROCESS11_ID = "PROCESS11_ID";
    private static final String PROCESS12_ID = "PROCESS12_ID";
    private static final String PROCESS13_ID = "PROCESS13_ID";

    private static final KogitoWorkflowProcess PROCESS1 = mockProcess1();
    private static final KogitoWorkflowProcess PROCESS2 = mockProcess2();
    private static final KogitoWorkflowProcess PROCESS3 = mockProcess3();
    private static final KogitoWorkflowProcess PROCESS4 = mockProcess4();
    private static final KogitoWorkflowProcess PROCESS5 = mockProcess5();
    private static final KogitoWorkflowProcess PROCESS6 = mockProcess6();
    private static final KogitoWorkflowProcess PROCESS7 = mockProcess7();
    private static final KogitoWorkflowProcess PROCESS8 = mockProcess8();
    private static final KogitoWorkflowProcess PROCESS9 = mockProcess9();
    private static final KogitoWorkflowProcess PROCESS10 = mockProcess10();
    private static final KogitoWorkflowProcess PROCESS11 = mockProcess11();
    private static final KogitoWorkflowProcess PROCESS12 = mockProcess12();

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
        ProcessGenerator processGenerator5 = mockProcessGenerator(PROCESS5);
        ProcessGenerator processGenerator6 = mockProcessGenerator(PROCESS6);
        ProcessGenerator processGenerator7 = mockProcessGenerator(PROCESS7);
        ProcessGenerator processGenerator8 = mockProcessGenerator(PROCESS8);
        ProcessGenerator processGenerator9 = mockProcessGenerator(PROCESS9);
        ProcessGenerator processGenerator10 = mockProcessGenerator(PROCESS10);
        ProcessGenerator processGenerator11 = mockProcessGenerator(PROCESS11);
        ProcessGenerator processGenerator12 = mockProcessGenerator(PROCESS12);

        ProcessContainerGenerator containerGenerator1 = mockProcessContainerGenerator(processGenerator1, processGenerator2);
        ProcessContainerGenerator containerGenerator2 = mockProcessContainerGenerator(processGenerator3);
        ProcessContainerGenerator containerGenerator3 = mockProcessContainerGenerator(processGenerator4, processGenerator5);
        ProcessContainerGenerator containerGenerator4 = mockProcessContainerGenerator(processGenerator6, processGenerator7,
                processGenerator8, processGenerator9, processGenerator10, processGenerator11, processGenerator12);

        Set<ProcessContainerGenerator> containerGenerators = Set.of(containerGenerator1, containerGenerator2, containerGenerator3, containerGenerator4);
        KogitoProcessContainerGeneratorBuildItem kogitoProcessContainerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(containerGenerators);

        KogitoAddOnJobsKnativeEventingProcessor processor = new KogitoAddOnJobsKnativeEventingProcessor();
        processor.buildCloudEventsMetadata(singletonList(kogitoProcessContainerGeneratorBuildItem), buildItemProducer);
        verify(buildItemProducer).produce(cloudEventsBuildItemCaptor.capture());
        KogitoCloudEventsBuildItem kogitoCloudEventsBuildItem = cloudEventsBuildItemCaptor.getValue();
        assertThat(kogitoCloudEventsBuildItem).isNotNull();
        assertThat(kogitoCloudEventsBuildItem.getCloudEvents()).hasSize(20);
        assertProcessWasIncluded(PROCESS1_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasNotIncluded(PROCESS2_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS3_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasNotIncluded(PROCESS4_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS5_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS6_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS7_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS8_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS9_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS10_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS11_ID, kogitoCloudEventsBuildItem.getCloudEvents());
        assertProcessWasIncluded(PROCESS12_ID, kogitoCloudEventsBuildItem.getCloudEvents());
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
    void jobsApiReflection() {
        ReflectiveClassBuildItem reflectiveClassBuildItem = new KogitoAddOnJobsKnativeEventingProcessor().jobsApiReflection();
        assertThat(reflectiveClassBuildItem.getClassNames()).hasSize(21);
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
        timerNode.setId(WorkflowElementIdentifierFactory.fromExternalFormat(1L));
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
        timerNode.setId(WorkflowElementIdentifierFactory.fromExternalFormat(1L));
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

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess5() {
        Node startNode = new StartNode();
        Node asyncEventNode = new AsyncEventNode(mock(org.kie.api.definition.process.Node.class));
        Node endNode = new EndNode();
        return mockProcess(PROCESS5_ID, startNode, asyncEventNode, endNode);
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess6() {
        Node startNode = new StartNode();
        Node eventNode = new EventNode();
        eventNode.setMetaData(Metadata.CUSTOM_SLA_DUE_DATE, "PT15S");
        Node endNode = new EndNode();
        return mockProcess(PROCESS6_ID, startNode, eventNode, endNode);
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess7() {
        Node startNode = new StartNode();
        StateBasedNode stateBasedNode = new StateBasedNode();
        stateBasedNode.setTimeout(DURATION);
        Node endNode = new EndNode();
        return mockProcess(PROCESS7_ID, startNode, stateBasedNode, endNode);
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess8() {
        Node startNode = new StartNode();
        Node endNode = new EndNode();
        KogitoWorkflowProcess process = mockProcess(PROCESS8_ID, startNode, endNode);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(Metadata.CUSTOM_SLA_DUE_DATE, DURATION);
        doReturn(metadata).when(process).getMetaData();
        return process;
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess9() {
        return mockProcessWithUserTaskDeadline(PROCESS9_ID, NOT_STARTED_NOTIFY_PARAMETER, "deadline1");
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess10() {
        return mockProcessWithUserTaskDeadline(PROCESS10_ID, NOT_COMPLETED_NOTIFY_PARAMETER, "deadline2");
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess11() {
        return mockProcessWithUserTaskDeadline(PROCESS11_ID, NOT_STARTED_REASSIGN_PARAMETER, "deadline3");
    }

    /**
     * Produces jobs service related events when the addon is installed.
     */
    private static KogitoWorkflowProcess mockProcess12() {
        return mockProcessWithUserTaskDeadline(PROCESS12_ID, NOT_COMPLETED_REASSIGN_PARAMETER, "deadline4");
    }

    private static KogitoWorkflowProcess mockProcessWithUserTaskDeadline(String processId, String deadline, String value) {
        Node startNode = new StartNode();
        Node endNode = new EndNode();
        HumanTaskNode humanTaskNode = new HumanTaskNode();
        humanTaskNode.getWork().setParameter(deadline, value);
        return mockProcess(processId, startNode, humanTaskNode, endNode);
    }

    private static void assertProcessWasIncluded(String processId, Set<? extends CloudEventMeta> cloudEvents) {
        assertThat(findCloudEventMeta(cloudEvents, CreateJobEvent.TYPE, "/process/" + processId, EventKind.PRODUCED)).isTrue();
        assertThat(findCloudEventMeta(cloudEvents, DeleteJobEvent.TYPE, "/process/" + processId, EventKind.PRODUCED)).isTrue();
    }

    private static void assertProcessWasNotIncluded(String processId, Set<? extends CloudEventMeta> cloudEvents) {
        assertThat(findCloudEventMeta(cloudEvents, CreateJobEvent.TYPE, "/process/" + processId, EventKind.PRODUCED)).isFalse();
        assertThat(findCloudEventMeta(cloudEvents, DeleteJobEvent.TYPE, "/process/" + processId, EventKind.PRODUCED)).isFalse();
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
