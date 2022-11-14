/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class SLAComplianceTest extends JbpmBpmn2TestCase {

    @Test
    public void testSLAonProcessViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLA.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA was not violated while it is expected").isTrue();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);
    }

    @Test
    public void testSLAonProcessMet() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLA.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_MET);
    }

    @Test
    public void testSLAonUserTaskViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLAOnTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA was not violated while it is expected").isTrue();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);

        Collection<KogitoNodeInstance> active = ((KogitoWorkflowProcessInstance) processInstance).getKogitoNodeInstances();
        assertThat(active).hasSize(1);

        KogitoNodeInstance userTaskNode = active.iterator().next();

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);
        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonUserTaskMet() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLAOnTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Collection<KogitoNodeInstance> active = ((KogitoWorkflowProcessInstance) processInstance).getKogitoNodeInstances();
        assertThat(active).hasSize(1);

        KogitoNodeInstance userTaskNode = active.iterator().next();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        // Whereas in memory it is already met
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_MET);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_MET);

    }

    @Test
    public void testSLAonProcessViolatedExternalTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLA.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKieRuntime().getEnvironment().set("SLATimerMode", "false");

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA should not violated by timer").isFalse();

        // simulate external tracking of sla
        kruntime.signalEvent("slaViolation", null, processInstance.getStringId());

        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA was not violated while it is expected").isTrue();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonUserTaskViolatedExternalTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLAOnTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKieRuntime().getEnvironment().set("SLATimerMode", "false");

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA should not violated by timer").isFalse();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Collection<KogitoNodeInstance> active = ((KogitoWorkflowProcessInstance) processInstance).getKogitoNodeInstances();
        assertThat(active).hasSize(1);

        KogitoNodeInstance userTaskNode = active.iterator().next();

        // simulate external tracking of sla
        kruntime.signalEvent("slaViolation:" + userTaskNode.getStringId(), null, processInstance.getStringId());

        slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA was not violated while it is expected").isTrue();

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 0);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) userTaskNode, 1);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonProcessViolatedWithExpression() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLAExpr.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "3s");

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask", parameters);
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(10, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA was not violated while it is expected").isTrue();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonProcessViolatedNoTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSLA.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKieRuntime().getEnvironment().set("SLATimerMode", "false");

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA should not violated by timer").isFalse();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_PENDING);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

        slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonCatchEventViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventSignalWithSLAOnEvent.bpmn2");

        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        boolean slaViolated = latch.await(5, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA should be violated by timer").isTrue();

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Collection<KogitoNodeInstance> active = ((KogitoWorkflowProcessInstance) processInstance).getKogitoNodeInstances();
        assertThat(active).hasSize(1);

        KogitoNodeInstance eventNode = active.iterator().next();

        kruntime.signalEvent("MyMessage", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 0);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 1);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_VIOLATED);

    }

    @Test
    public void testSLAonCatchEventNotViolated() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterSLAViolated(SLAViolatedEvent event) {
                latch.countDown();
            }

        };
        kruntime = createKogitoProcessRuntime("BPMN2-IntermediateCatchEventSignalWithSLAOnEvent.bpmn2");

        kruntime.getProcessEventManager().addEventListener(listener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(KogitoProcessInstance.STATE_ACTIVE);

        Collection<KogitoNodeInstance> active = ((KogitoWorkflowProcessInstance) processInstance).getKogitoNodeInstances();
        assertThat(active).hasSize(1);

        KogitoNodeInstance eventNode = active.iterator().next();

        kruntime.signalEvent("MyMessage", null, processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);

        int slaCompliance = getSLAComplianceForProcessInstance(processInstance);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_NA);

        slaCompliance = getSLAComplianceForNodeInstance(processInstance.getStringId(), (org.jbpm.workflow.instance.NodeInstance) eventNode, 1);
        assertThat(slaCompliance).isEqualTo(KogitoProcessInstance.SLA_MET);

        boolean slaViolated = latch.await(3, TimeUnit.SECONDS);
        assertThat(slaViolated).as("SLA should not violated by timer").isFalse();

    }

    /*
     * Helper methods
     */

    private int getSLAComplianceForProcessInstance(KogitoProcessInstance processInstance) {
        int slaCompliance = ((org.jbpm.process.instance.ProcessInstance) processInstance).getSlaCompliance();
        return slaCompliance;
    }

    private int getSLAComplianceForNodeInstance(String processInstanceId, org.jbpm.workflow.instance.NodeInstance nodeInstance, int logType) {
        int slaCompliance = nodeInstance.getSlaCompliance();

        return slaCompliance;
    }
}
