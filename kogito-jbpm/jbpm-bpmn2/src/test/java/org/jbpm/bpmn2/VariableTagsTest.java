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

import org.drools.core.io.impl.ClassPathResource;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.VariableViolationException;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ABORTED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;

public class VariableTagsTest extends JbpmBpmn2TestCase {

    @Test
    public void testProcessWithMissingRequiredVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-required-variable-tags.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        assertThrows(VariableViolationException.class, () -> kruntime.startProcess("approvals"));
    }
    
    @Test
    public void testProcessWithRequiredVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-required-variable-tags.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");
        
        KogitoProcessInstance processInstance = kruntime.startProcess("approvals", parameters);
        assertEquals(STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        
        assertProcessInstanceFinished(processInstance, kruntime);
    }
    
    @Test
    public void testProcessWithReadonlyVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-readonly-variable-tags.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");
        
        KogitoProcessInstance processInstance = kruntime.startProcess("approvals", parameters);
        assertEquals(STATE_ACTIVE, processInstance.getState());
        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
                
        assertThrows(VariableViolationException.class, () -> kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), Collections.singletonMap("ActorId", "john")));

        kruntime.abortProcessInstance(processInstance.getStringId());
        
        assertProcessInstanceFinished(processInstance, kruntime);
    }
    
    @Test
    public void testProcessWithCustomVariableTag() throws Exception {
        kruntime = createKogitoProcessRuntime("variable-tags/approval-with-custom-variable-tags.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                if (event.hasTag("onlyAdmin")) {
                    throw new VariableViolationException( (( KogitoProcessInstance ) event.getProcessInstance()).getStringId(), event.getVariableId(), "Variable can only be set by admins");
                }
            }
            
        });
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("approver", "john");
        
        assertThrows(VariableViolationException.class, () -> kruntime.startProcess("approvals", parameters));
    }

    @Test
    public void testRequiredVariableFiltering() {
        List<BpmnProcess> processes = BpmnProcess.from(new ClassPathResource("variable-tags/approval-with-custom-variable-tags.bpmn2"));
        BpmnProcess process = processes.get(0);        
        Map<String, Object> params = new HashMap<>();
        params.put("approver", "john");

        org.kie.kogito.process.ProcessInstance<BpmnVariables> instance = process.createInstance(BpmnVariables.create(params));
        instance.start();
        
        assertEquals(STATE_ACTIVE, instance.status());
        
        assertThat(instance.variables().toMap()).hasSize(1);
        assertThat(instance.variables().toMap(BpmnVariables.OUTPUTS_ONLY)).hasSize(0);
        assertThat(instance.variables().toMap(BpmnVariables.INPUTS_ONLY)).hasSize(0);
        assertThat(instance.variables().toMap(BpmnVariables.INTERNAL_ONLY)).hasSize(0);
        assertThat(instance.variables().toMap(v -> v.hasTag("onlyAdmin"))).hasSize(1).containsEntry("approver", "john");
        
        instance.abort();
        
        assertEquals(STATE_ABORTED, instance.status());
    }
}
