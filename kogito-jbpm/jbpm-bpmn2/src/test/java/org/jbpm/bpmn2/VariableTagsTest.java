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
package org.jbpm.bpmn2;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.io.ClassPathResource;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ABORTED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;

public class VariableTagsTest extends JbpmBpmn2TestCase {

    @Test
    public void testProcessWithMissingRequiredVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-required-variable-tags.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        assertThatExceptionOfType(VariableViolationException.class).isThrownBy(() -> kruntime.startProcess("approvals"));
    }

    @Test
    public void testProcessWithRequiredVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-required-variable-tags.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");

        KogitoProcessInstance processInstance = kruntime.startProcess("approvals", parameters);
        assertThat(processInstance.getState()).isEqualTo(STATE_ACTIVE);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testProcessWithReadonlyVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-readonly-variable-tags.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");

        KogitoProcessInstance processInstance = kruntime.startProcess("approvals", parameters);
        assertThat(processInstance.getState()).isEqualTo(STATE_ACTIVE);
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();

        assertThatExceptionOfType(VariableViolationException.class)
                .isThrownBy(() -> kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), Collections.singletonMap("ActorId", "john")));

        kruntime.abortProcessInstance(processInstance.getStringId());

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testProcessWithCustomVariableTag() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-custom-variable-tags.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                if (event.hasTag("onlyAdmin")) {
                    throw new VariableViolationException(((KogitoProcessInstance) event.getProcessInstance()).getStringId(), event.getVariableId(), "Variable can only be set by admins");
                }
            }

        });

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");

        assertThatExceptionOfType(VariableViolationException.class).isThrownBy(() -> kruntime.startProcess("approvals", parameters));
    }

    @Test
    public void testRequiredVariableFiltering() {
        List<BpmnProcess> processes = BpmnProcess.from(new ClassPathResource("variable-tags/approval-with-custom-variable-tags.bpmn2"));
        BpmnProcess process = processes.get(0);
        Map<String, Object> params = new HashMap<>();
        params.put("approver", "john");

        org.kie.kogito.process.ProcessInstance<BpmnVariables> instance = process.createInstance(BpmnVariables.create(params));
        instance.start();

        assertThat(instance.status()).isEqualTo(STATE_ACTIVE);

        assertThat(instance.variables().toMap()).hasSize(1);
        assertThat(instance.variables().toMap(BpmnVariables.OUTPUTS_ONLY)).isEmpty();
        assertThat(instance.variables().toMap(BpmnVariables.INPUTS_ONLY)).isEmpty();
        assertThat(instance.variables().toMap(BpmnVariables.INTERNAL_ONLY)).isEmpty();
        assertThat(instance.variables().toMap(v -> v.hasTag("onlyAdmin"))).hasSize(1).containsEntry("approver", "john");

        instance.abort();

        assertThat(instance.status()).isEqualTo(STATE_ABORTED);
    }
}
